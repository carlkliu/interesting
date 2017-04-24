package com.carl.interesting.common.util;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;

/**
 * log utility class
 * 
 * @author Carl Liu
 * @version [version, 21 Jul 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class LogUtil {
    protected NamedList<String> metadata;
    
    public void setMetadata(NamedList<String> metadata) {
        this.metadata = metadata;
    }
    
    public NamedList<String> getMetadata() {
        return metadata;
    }
    
    public String getMetadata(String key) {
        return (metadata != null && key != null) ? metadata.get(key) : null;
    }
    
    public void setMetadata(String key, String value) {
        if (key == null || value == null)
            throw new IllegalArgumentException(
                    "Exception metadata cannot be null!");
        if (metadata == null)
            metadata = new NamedList<String>();
        metadata.add(key, value);
    }
    
    public static void logError(Log log, Throwable e) {
        String stackTrace = toStr(e);
        String ignore = doIgnore(e, stackTrace);
        if (ignore != null) {
            log.info(ignore);
            return;
        }
        log.error(stackTrace);
    }
    
    public static void logError(Log log, Throwable e, String msg) {
        String stackTrace = msg + ':' + toStr(e);
        String ignore = doIgnore(e, stackTrace);
        if (ignore != null) {
            log.info(ignore);
            return;
        }
        log.error(stackTrace);
    }
    
    public static void logError(Log log, Throwable e, long errorcode,
            String msg) {
        String stackTrace = errorcode + ':' + msg + ':' + toStr(e);
        String ignore = doIgnore(e, stackTrace);
        if (ignore != null) {
            log.info(ignore);
            return;
        }
        log.error(stackTrace);
    }
    
    public static void logError(Log log, String msg) {
        String stackTrace = msg;
        String ignore = doIgnore(null, stackTrace);
        if (ignore != null) {
            log.info(ignore);
            return;
        }
        log.error(stackTrace);
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
    
    public static String toStr(Throwable e) {
        CharArrayWriter cw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(cw);
        e.printStackTrace(pw);
        pw.flush();
        return cw.toString();
        /**
         * This doesn't work for some reason!!!!! StringWriter sw = new
         * StringWriter(); PrintWriter pw = new PrintWriter(sw);
         * e.printStackTrace(pw); pw.flush(); return sw.toString();
         **/
    }
    
    /**
     * For test code - do not log exceptions that match any of the regular
     * expressions in ignorePatterns
     */
    public static Set<String> ignorePatterns;
    
    /**
     * Returns null if this exception does not match any ignore patterns, or a
     * message string to use if it does.
     */
    public static String doIgnore(Throwable t, String m) {
        if (ignorePatterns == null || m == null)
            return null;
        if (t != null && t instanceof AssertionError)
            return null;
        for (String regex : ignorePatterns) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(m);
            if (matcher.find())
                return "Ignoring exception matching " + regex;
        }
        return null;
    }
    
    public static Throwable getRootCause(Throwable t) {
        while (true) {
            Throwable cause = t.getCause();
            if (cause != null) {
                t = cause;
            }
            else {
                break;
            }
        }
        return t;
    }
}
