package com.carl.interesting.common.constants;

import com.carl.interesting.common.util.ConfigHelper;

/*
 * file name: JSONKeyConstants.java copyright: Unis Cloud Information Technology
 * Co., Ltd. Copyright 2016, All rights reserved description: <description>
 * mofidy staff: yangbin zhang mofidy time: 1 Ari 2016
 */
/**
 * constants for properties
 * 
 * @author Carl Liu
 * @version [version, 7 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class KeyConstant {
    /**
     * MircoService host IP
     */
    public static final String MICROSERVICE_HOST = ConfigHelper
            .get("microservice.host");
    
    public static final String MICROSERVICE_PROCESS_RUN_LOG = ConfigHelper
            .get("microservice.process.run.log");
    
    /**
     * MircoService server port
     */
    public static final String MICROSERVICE_PORT = ConfigHelper
            .get("microservice.port");
    
    public static final String USERNAME = ConfigHelper.get("username");
    
    public static final String PASSWORD = ConfigHelper.get("password");
    
    public static final String MULTIPOINT_LOGIN = "singleuser.multipoint.login";
    
    public static final String QUARTZ_THREAD_COUNT = ConfigHelper
            .get("quartz.thread.count");
    
    public static final String HTTP_REQUEST_SESSION_TIMEOUT = ConfigHelper
            .get("http.request.session.timeout");
}
