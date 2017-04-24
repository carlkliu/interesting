package com.carl.interesting.common.exception;

/**
 * Runtime exception for task
 * 
 * @author Jiaqi Yang
 * @version [version, Aug 16, 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class TaskRuntimeException extends RuntimeException {
    /**
     * remark content
     */
    private static final long serialVersionUID = 1L;
    
    private int errorcode;
    
    /**
     * <default constructor>
     */
    public TaskRuntimeException() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
     * <default constructor>
     */
    public TaskRuntimeException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * <default constructor>
     */
    public TaskRuntimeException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * <default constructor>
     */
    public TaskRuntimeException(String message, int errorCode) {
        super(message);
        this.errorcode = errorCode;
    }
    
    /**
     * <default constructor>
     */
    public TaskRuntimeException(Throwable cause, int errorCode) {
        super(cause);
        this.errorcode = errorCode;
    }
    
    /**
     * @return returns errorcode
     */
    public int getErrorcode() {
        return errorcode;
    }
}
