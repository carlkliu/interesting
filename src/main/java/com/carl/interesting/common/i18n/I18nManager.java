package com.carl.interesting.common.i18n;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.carl.interesting.common.i18n.entity.InterestingI18n;
import com.carl.interesting.common.session.CustomSessionManagement;

/**
 * <Simple feature description > <Detailed feature description>
 * 
 * @author zhangjie
 * @version [version, 12 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class I18nManager {
    private static Map<String, InterestingI18n> BUNDLE_MAP = new ConcurrentHashMap<>();
    
    public static final I18nManager INSTANCE = new I18nManager();
    
    private I18nManager() {
    }
    
    public InterestingI18n getEntity(String ip) {
        CustomSessionManagement manager = CustomSessionManagement.getInstance();
        String language = manager.getLanguage(ip);
        // String language = ip;
        if (!BUNDLE_MAP.containsKey(language)) {
            BUNDLE_MAP.put(language, new InterestingI18n(language));
        }
        return BUNDLE_MAP.get(language);
    }
}
