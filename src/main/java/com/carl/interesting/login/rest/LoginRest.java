package com.carl.interesting.login.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.carl.interesting.common.ResponseDataFormat;
import com.carl.interesting.common.ServiceResult;
import com.carl.interesting.common.session.CustomSessionManagement;
import com.carl.interesting.login.service.LoginServiceImpl;

import net.sf.json.JSONObject;

/**
 * login action
 * 
 * @author Carl Liu
 * @version [version, 1 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
@Path("interesting")
public class LoginRest {
    LoginServiceImpl loginService = LoginServiceImpl.getInstance();
    
    private static final String USERNAME = "username";
    
    private static final String PASSWORD = "password";
    
    private static final String START_LOGOUT = "startlogout";
    
    private static final String END = "end";
    
    private static final String RESULT = "result";
    
    /**
     * user login
     * 
     * @param request
     * @param userName
     * @param password
     * @return [explain parameter]
     * @return String [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    @Path("login")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
    public String login(@QueryParam(USERNAME) String username,
            @QueryParam(PASSWORD) String password,
            @QueryParam("language") String language,
            @Context HttpHeaders headers) {
        List<String> ipValue = CustomSessionManagement.getInstance()
                .getClientIP(headers);
        ServiceResult serviceResult = loginService.restCallLogin(ipValue,
                username,
                password,
                language);
        return ResponseDataFormat.responseString(serviceResult);
    }
    
    /**
     * user logout
     * 
     * @param request
     * @param userName
     * @return [explain parameter]
     * @return String [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    @Path("logout")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
    public String logout(@QueryParam(START_LOGOUT) String startLogout) {
        JSONObject result = new JSONObject();
        startLogout = END;
        result.put(RESULT, startLogout);
        return result.toString();
    }
}
