package com.evertcode.app.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by Hebert Hernandez on 2020/02/19.
 *
 */

@Getter
@Setter
@ToString
public class User implements Serializable {

    /**
     * Class version
     */
    private static final long serialVersionUID = -5630307903050011974L;

	/**
	 * ID transaction
	 */
    private Long id;

	/**
	 * Username transaction
	 */
    private String username;

	/**
	 * Transaction date
	 */
    private String transactionDate;

	/**
	 * Transaction amount
	 */
    private String transactionAmount;

}
