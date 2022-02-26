package com.evertcode.app;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Application
 */
public class DemoSpringBatchApplication {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoSpringBatchApplication.class);

    /**
     * Main entry point application
     *
     * @param args
     */
    public static void main(final String[] args) {
        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

        ctx.register(SpringConfig.class);
        ctx.register(SpringBatchConfig.class);
        ctx.refresh();

        runJob(ctx);

    }

    /**
     * Function for run job
     * @param ctx
     */
    private static void runJob(final AnnotationConfigApplicationContext ctx) {
        final JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
        final Job job = (Job) ctx.getBean("demoJob");

        LOGGER.info("Iniciando el Job: {}", "demoJob");

        try {
            final JobParameters jobParameters = new JobParametersBuilder().addString("jobID", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();

            final JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            LOGGER.info("Estatus del Job: {}", jobExecution.getStatus());

        } catch (final Exception e) {
            LOGGER.error("Fallo el Job {}", e.getMessage(), e);
        }

    }

}
