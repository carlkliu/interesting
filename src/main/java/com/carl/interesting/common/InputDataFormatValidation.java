package com.carl.interesting.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * valid front-end input data format class
 * 
 * @author Tianbao Liu
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
    
    public static final String POOL_NAME = "^(?!_)[a-zA-Z0-9_]{6,16}$";
    
    public static final String DUPLICATION = "^[1-9][0-9]*$";
    
    public static final String MON_NAME = "^[0-9a-zA-Z]{1,16}$";
    
    // TODO public static final String S3_ACCOUNT_NAME =
    // "^(?!_)[a-zA-Z0-9_]{6,16}$";
    // TODO public static final String S3_ACCOUNT_SECRET =
    // "^(?!_)[a-zA-Z0-9_]{6,16}$";
    // TODO public static final String S3_URL =
    // "^(?!_)[a-zA-Z0-9_]{6,16}$";
    // TODO public static final String S3_BUCKET =
    // "^(?!_)[a-zA-Z0-9_]{6,16}$";
    public static final String MDS_NAME = "^[0-9a-zA-Z]{1,16}$";
    
    public static final String IMAGE_NAME = "^(?!_)[a-zA-Z0-9_]{6,16}$";
    
    public static final String IMAGE_SIZE = "^\\-?\\d+(\\.\\d+)?$";
    
    public static boolean validateDataFormat(String data, String format) {
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }
}
