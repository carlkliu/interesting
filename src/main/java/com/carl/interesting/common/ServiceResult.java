package com.carl.interesting.common;

/**
 * rest call service,return result
 * 
 * @author Carl Liu
 * @version [version, Sep 9, 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class ServiceResult {
    private boolean result;
    
    private Object data;
    
    private int errorCode;
    
    private String message = "";
    
    private int total;
    
    public ServiceResult() {
    }
    
    public ServiceResult(Object data) {
        this.result = true;
        this.data = data;
    }
    
    public ServiceResult(Object data, int total) {
        this.result = true;
        this.data = data;
        this.total = total;
    }
    
    public ServiceResult(int errorCode, String message) {
        this.result = false;
        this.errorCode = errorCode;
        this.message = message;
    }
    
    /**
     * @return returns result
     */
    public boolean isResult() {
        return result;
    }
    
    /**
     * @param assgin values to result
     */
    public void setResult(boolean result) {
        this.result = result;
    }
    
    /**
     * @return returns data
     */
    public Object getData() {
        return data;
    }
    
    /**
     * @param assgin values to data
     */
    public void setData(Object data) {
        this.data = data;
    }
    
    /**
     * @return returns errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }
    
    /**
     * @param assgin values to errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    
    /**
     * @return returns message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * @param assgin values to message
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * @return returns total
     */
    public int getTotal() {
        return total;
    }
    
    /**
     * @param assgin values to total
     */
    public void setTotal(int total) {
        this.total = total;
    }
    
    /**
     * @return
     */
    @Override
    public String toString() {
        return "ServiceResult [result=" + result + ", data=" + data
                + ", errorCode=" + errorCode + ", message=" + message
                + ", total=" + total + "]";
    }
    
    /**
     * @return
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + errorCode;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + (this.result ? 1231 : 1237);
        result = prime * result + total;
        return result;
    }
    
    /**
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServiceResult other = (ServiceResult) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        }
        else if (!data.equals(other.data))
            return false;
        if (errorCode != other.errorCode)
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        }
        else if (!message.equals(other.message))
            return false;
        if (result != other.result)
            return false;
        if (total != other.total)
            return false;
        return true;
    }
}
