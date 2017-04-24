package com.carl.interesting.common.util;

import java.util.Iterator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Property file utility
 * 
 * @author Carl Liu
 * @version [version, 21 Jul 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class ConfigHelper {
    private static Log LOG = LogFactory.getLog(ConfigHelper.class);
    
    /**
     * file name
     */
    private static final String CONFIG_FILE = "interesting.properties";
    
    private static final int REFRESH_DELAY = 5000;
    
    private static PropertiesConfiguration config = null;
    static {
        String file = null;
        try {
            file = ConfigHelper.class.getClassLoader()
                    .getResource(CONFIG_FILE)
                    .getFile();
            config = new PropertiesConfiguration(file);
            config.setAutoSave(true);
            FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
            strategy.setRefreshDelay(REFRESH_DELAY);
            config.setReloadingStrategy(strategy);
        }
        catch (ConfigurationException e) {
            LogUtil.logError(LOG, e, "Fail to load " + file);
        }
    }
    
    /**
     * get all keys
     * 
     * @throws java.io.IOException [explain parameter]
     * @return Iterator<String> [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static Iterator<String> keys() throws java.io.IOException {
        return config.getKeys();
    }
    
    /**
     * get values by key,ex:key_name=value1,value2,return [value1,value2]
     * 
     * @param key
     * @return [explain parameter]
     * @return String[] [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static String[] getStringArray(String key) {
        return config.getStringArray(key);
    }
    
    /**
     * get value by key
     * 
     * @param key
     * @return [explain parameter]
     * @return String [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static String get(String key) {
        return get(key, "");
    }
    
    /**
     * get value by key
     * 
     * @param key
     * @param defaultVal
     * @return [explain parameter]
     * @return String [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static String get(String key, String defaultVal) {
        return config.getString(key, defaultVal);
    }
    
    /**
     * Persist key-value state into property file.
     * 
     * @param key
     * @param value [explain parameter]
     * @return void [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public static void put(String key, String value) {
        config.setProperty(key, value);
    }
}