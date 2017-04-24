package com.carl.interesting.login.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * login information cache
 * 
 * @author Carl Liu
 * @version [version, 3 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class LoginInfoCache {
    private Map<String, List<String>> map = new HashMap<String, List<String>>();
    
    private static final LoginInfoCache logincache = new LoginInfoCache();
    
    private LoginInfoCache() {
    }
    
    public static LoginInfoCache getInstance() {
        return logincache;
    }
    
    public void put(String key, List<String> list) {
        map.put(key, list);
    }
    
    public Map<String, List<String>> getUserInfoMap() {
        return map;
    }
    
    /**
     * 根据key获取 list 信息
     * 
     * @param key
     * @return [explain parameter]
     * @return List<String> [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public List<String> getUserInfo(String key) {
        Map<String, List<String>> mapData = getUserInfoMap();
        List<String> loginInfo = mapData.get(key);
        if (loginInfo == null) {
            loginInfo = new ArrayList<String>();
        }
        return loginInfo;
    }
    
    /**
     * valid X-Real-Ip exists into list.
     * 
     * @param realIp
     * @return [explain parameter]
     * @return boolean [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public boolean validRealIp(String realIp) {
        Map<String, List<String>> mapData = getUserInfoMap();
        Boolean flag = false;
        for (String key : mapData.keySet()) {
            for (String value : mapData.get(key)) {
                if (value.equals(realIp)) {
                    flag = true;
                }
            }
        }
        return flag;
    }
}
