package com.carl.interesting.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * valid front-end input data format class
 * 
 * @author Carl Liu
 * @version [version, Sep 1, 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class InputDataFormatValidation {
    public static final String USERNAME = "^.{1,16}$";
    
    public static final String PASSWORD = "^.{1,16}$";
    
    public static final String DESCRIPTION = "^[\\s\\S]{1,255}$";
    
    public static final String IPDARESS = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\."
            + "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\."
            + "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
    
    public static boolean validateDataFormat(String data, String format) {
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }
}
