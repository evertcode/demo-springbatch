package com.evertcode.app;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.evertcode.app.model.User;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringBatchConfig.class);
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;

	@Value("input/demo.csv")
	private Resource inputCsv;

	@Bean
	public ItemReader<User> itemReader() {

		final FlatFileItemReader<User> reader = new FlatFileItemReader<>();
		final DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		final DefaultLineMapper<User> lineMapper = new DefaultLineMapper<>();
		final BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		final String[] tokens = { "id", "username", "transactiondate", "transactionamount" };

		tokenizer.setNames(tokens);
		reader.setResource(this.inputCsv);
		fieldSetMapper.setTargetType(User.class);
		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		reader.setLineMapper(lineMapper);

		return reader;
	}

	@Bean
	public ItemProcessor<User, User> itemProcessor() {
		return new ItemProcessor<User, User>() {

			@Override
			public User process(User item) throws Exception {
				LOGGER.info("Persistiendo {}", item);
				return item;
			}

		};
	}

	@Bean
	public ItemWriter<User> itemWriter() {
		return new ItemWriter<User>() {

			@Override
			public void write(List<? extends User> items) throws Exception {
				for (User user : items) {
					LOGGER.info("{}", user);
				}
			}

		};
	}
	
	@Bean
	public JdbcBatchItemWriter<User> itemWriterJdbc() {
		JdbcBatchItemWriter<User> itemWriter = new JdbcBatchItemWriter<>();

		itemWriter.setDataSource(this.dataSource);
		itemWriter.setSql("INSERT INTO DEMO VALUES (:id, :username, :transactionDate, :transactionAmount)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		itemWriter.afterPropertiesSet();

		return itemWriter;
	} 

	@Bean
	protected Step demoStep(ItemReader<User> reader, ItemProcessor<User, User> processor, JdbcBatchItemWriter<User> writer) {
		return this.stepBuilderFactory
				.get("demoStep")
				.<User, User>chunk(2)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}

	@Bean(name = "demoJob")
	protected Job job(@Qualifier("demoStep") Step demoStep) {
		return this.jobBuilderFactory
				.get("demoJob")
				.start(demoStep)
				.build();
	}

}
