package com.carl.interesting.common.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import com.carl.interesting.common.constants.KeyConstant;
import com.carl.interesting.common.constants.ResponseFrontEndErrorCode;
import com.carl.interesting.login.service.LoginInfoCache;

/**
 * custom session management class
 * 
 * @author Tianbao Liu
 * @version [version, 8 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class CustomSessionManagement {
    /**
     * sesion map ,key is client IP,value is time millisecond.
     */
    public static Map<String, CustomSession> map = new HashMap<String, CustomSession>();
    
    private final static String X_REAL_IP = "X-Real-IP";
    
    private final static String DEFAULT_LANGUAGE = "zh-CN";
    
    private final static String REQUEST_IS_SCHEDULER = "0";
    
    /**
     * default timeout ,30 minutes
     */
    private Long defaultTimeout = Long
            .parseLong(KeyConstant.HTTP_REQUEST_SESSION_TIMEOUT);
    
    private static CustomSessionManagement customSessionManagement = new CustomSessionManagement();
    
    private CustomSessionManagement() {
    };
    
    public static CustomSessionManagement getInstance() {
        return customSessionManagement;
    }
    
    /**
     * update session last time
     * 
     * @param ip
     * @param date [explain parameter]
     * @return void [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    private void updateTime(String ip, Long date) {
        CustomSession cs = map.get(ip);
        if (cs == null) {
            cs = new CustomSession();
        }
        cs.setLastTime(date);
        map.put(ip, cs);
    }
    
    /**
     * set session language
     * 
     * @param ip
     * @param language [explain parameter]
     * @return void [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public void setLanguage(String ip, String language) {
        CustomSession cs = new CustomSession();
        cs.setLastTime(System.currentTimeMillis());
        cs.setLang(language);
        map.put(ip, cs);
    }
    
    /**
     * get session language
     * 
     * @param ip
     * @param language [explain parameter]
     * @return void [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public String getLanguage(String ip) {
        String language = DEFAULT_LANGUAGE;
        if (map.get(ip) != null && map.get(ip).getLang() != null) {
            language = map.get(ip).getLang();
        }
        return language;
    }
    
    /**
     * get client IP
     * 
     * @param headers
     * @return List<String> list
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public List<String> getClientIP(HttpHeaders headers) {
        MultivaluedMap<String, String> headerParams = headers
                .getRequestHeaders();
        return headerParams.get(X_REAL_IP);
    }
    
    /**
     * get client IP
     * 
     * @param headers
     * @return List<String> list
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public List<String> getClientIP(ContainerRequestContext request) {
        MultivaluedMap<String, String> headerParams = request.getHeaders();
        return headerParams.get(X_REAL_IP);
    }
    
    /**
     * valid session is timeout
     * 
     * @param ip
     * @return boolean [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    private boolean isTimeOut(String ip, long currentTime) {
        long lastTime = 0;
        boolean flag = false;
        // if IP not exists in map,lastTime is 0
        if (map.get(ip) != null && map.get(ip).getLastTime() != null) {
            lastTime = map.get(ip).getLastTime();
        }
        if ((currentTime - lastTime) > defaultTimeout) {
            flag = true;
        }
        return flag;
    }
    
    /**
     * filter HTTP request
     * 
     * @param request
     * @param scheduler
     * @return [explain parameter]
     * @return boolean [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public int filterReq(ContainerRequestContext request, String scheduler) {
        int flag = ResponseFrontEndErrorCode.SESSION_INVALID;
        long currentTime = System.currentTimeMillis();
        // if request is scheduler
        if (null != scheduler && REQUEST_IS_SCHEDULER.equals(scheduler)) {
            flag = 0;
        }
        else {
            String ip = getClientIP(request).get(0);
            // if allow single user login multisite or single user not login
            // multisite
            if (LoginInfoCache.getInstance().validRealIp(ip)) {
                // if request is not scheduler and session is not timeout
                if (!isTimeOut(ip, currentTime)) {
                    updateTime(ip, currentTime);
                    flag = 0;
                }
                // if session is timeout,remove session from map.
                else {
                    map.remove(ip);
                }
            }
            else {
                flag = ResponseFrontEndErrorCode.USER_LOGIN_OTHER_PLACE;
            }
        }
        return flag;
    }
}
