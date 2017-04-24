package com.carl.interesting.common.exception;

/**
 * Data access custom exception
 * 
 * @author template
 * @version [version, 16 Jun 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class DataAccessException extends Exception {
    /**
     * remark content
     */
    private static final long serialVersionUID = 1L;
    
    public DataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
