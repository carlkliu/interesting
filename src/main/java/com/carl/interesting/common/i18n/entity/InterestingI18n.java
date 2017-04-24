package com.carl.interesting.common.i18n.entity;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.carl.interesting.common.constants.LanguageKeys;

/**
 * provide i18n api
 * 
 * @author zhangjie
 * @version [version, 12 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class InterestingI18n {
    private static final Log LOG = LogFactory.getLog(InterestingI18n.class);
    
    private static final String BASE_NAME = "language";
    
    private ResourceBundle bundle;
    
    /**
     * <default constructor>
     */
    public InterestingI18n(String language) {
        Locale locale = new Locale(language);
        bundle = ResourceBundle.getBundle(BASE_NAME, locale);
    }
    
    public String getString(String key) {
        String str = "";
        try {
            str = bundle.getString(key);
        }
        catch (MissingResourceException e) {
            LOG.debug("Failed to get resource,key:" + key);
        }
        return str;
    }
    
    public String getSessionInvalidKey() {
        return getString(LanguageKeys.COMMON_COMMON_FUNC_SESSION_INVALID);
    }
}
