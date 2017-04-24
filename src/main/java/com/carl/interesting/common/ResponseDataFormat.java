package com.carl.interesting.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.carl.interesting.common.constants.Constant;
import com.carl.interesting.common.i18n.I18nManager;
import com.carl.interesting.common.i18n.entity.InterestingI18n;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

/**
 * Utils for build common request and response
 * 
 * @author xupingzheng
 * @version [version, 16 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class ResponseDataFormat {
    public static Map<Long, InterestingI18n> map = new HashMap<Long, InterestingI18n>();
    
    /**
     * Build response message.
     * 
     * @param result
     * @param error
     * @param data
     * @param total
     * @return [explain parameter]
     * @return JSONObject [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static JSONObject build(boolean result, JSON error, Object data,
            long total) {
        JSONObject response = new JSONObject();
        response.put(Constant.RESULT, result);
        response.put(Constant.ERROR, null == error ? "" : error);
        response.put(Constant.DATA, null == data ? "" : data);
        response.put(Constant.TOTAL, total);
        return response;
    }
    
    /**
     * Build error message
     * 
     * @param errorCode
     * @param message
     * @return [explain parameter]
     * @return JSONObject [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static JSONObject error(int errorCode, String message) {
        JSONObject error = new JSONObject();
        error.put(Constant.CODE, errorCode);
        error.put(Constant.MESSAGE, null == message ? "" : message);
        return build(false, error, null, 0);
    }
    
    /**
     * Response error with errorcode
     * 
     * @param errorCode
     * @return [explain parameter]
     * @return JSONObject [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static JSONObject error(int errorCode) {
        return error(errorCode, null);
    }
    
    /**
     * Build error empty string message
     * 
     * @return [explain parameter]
     * @return JSONObject [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static JSONObject error() {
        return error(-1, null);
    }
    
    /**
     * Build info message with total
     * 
     * @param data
     * @param total
     * @return [explain parameter]
     * @return JSONObject [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static JSONObject info(Object data, long total) {
        return build(true, null, data, total);
    }
    
    /**
     * Build info message
     * 
     * @param data
     * @return [explain parameter]
     * @return JSONObject [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static JSONObject info(Object data) {
        return info(data, 0);
    }
    
    public static String responseString(ServiceResult result) {
        String res = "";
        if (null != result) {
            if (result.isResult()) {
                res = info(result.getData(), result.getTotal()).toString();
            }
            else {
                InterestingI18n i18n = map.get(Thread.currentThread().getId());
                if (i18n == null) {
                    i18n = I18nManager.INSTANCE.getEntity("");
                }
                String message = result.getMessage();
                if (message.contains("#")) {
                    String[] params = message.split("#");
                    message = i18n.getString(params[0]);
                    params = Arrays.copyOfRange(params, 1, params.length);
                    message = String.format(message, (Object[]) params);
                }
                else {
                    message = i18n.getString(message);
                }
                res = error(result.getErrorCode(), message).toString();
            }
        }
        return res;
    }
    
    /**
     * Get interesting i18n
     * 
     * @return [explain parameter]
     * @return interestingI18n [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static InterestingI18n getInterestingI18n() {
        return map.get(Thread.currentThread().getId());
    }
}
