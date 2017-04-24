package com.carl.interesting.login.service;

import java.util.List;

import com.carl.interesting.common.InputDataFormatValidation;
import com.carl.interesting.common.ServiceResult;
import com.carl.interesting.common.constants.KeyConstant;
import com.carl.interesting.common.constants.LanguageKeys;
import com.carl.interesting.common.constants.ResponseFrontEndErrorCode;
import com.carl.interesting.common.session.CustomSessionManagement;
import com.carl.interesting.common.util.ConfigHelper;
import com.carl.interesting.user.service.UserServiceImpl;

/**
 * login service
 * 
 * @author Carl Liu
 * @version [version, 1 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class LoginServiceImpl {
    
    private static LoginServiceImpl loginServiceImpl;
    
    /**
     * single user login in multisite flag
     */
    private static final String NO = "no";
    
    UserServiceImpl userService = UserServiceImpl.getInstance();
    
    private LoginServiceImpl() {
    }
    
    public ServiceResult restCallLogin(List<String> ipValue, String username,
            String password, String language) {
        ServiceResult serviceResult = new ServiceResult();
        boolean flag = userService.validUser(username, password);
        if (!validFormatUsernameAndPassword(username, password) || !flag) {
            serviceResult.setResult(false);
            serviceResult.setErrorCode(
                    ResponseFrontEndErrorCode.USERNAME_OR_PASSWORD_INCORRECT);
            serviceResult.setMessage(
                    LanguageKeys.LOGIN_LOGIN_FUNC_USERNAME_PASSWORD_INCORRECT);
        }
        else {
            limitMultiUserLogin(username, ipValue);
            CustomSessionManagement.getInstance().setLanguage(ipValue.get(0),
                    language);
            serviceResult.setResult(true);
        }
        return serviceResult;
    }
    
    /**
     * valid username and password format
     * 
     * @param username
     * @param password
     * @return boolean true is username and password match format rule<br>
     * otherwise false.
     * @see [class,class#method,class#member]
     */
    private boolean validFormatUsernameAndPassword(String username,
            String password) {
        boolean flag = false;
        if (InputDataFormatValidation.validateDataFormat(username,
                InputDataFormatValidation.USERNAME)
                && InputDataFormatValidation.validateDataFormat(password,
                        InputDataFormatValidation.PASSWORD)) {
            flag = true;
        }
        return flag;
    }
    
    /**
     * @param userName
     * @return [explain parameter]
     * @return boolean [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    private void limitMultiUserLogin(String username, List<String> ipValue) {
        List<String> userIpList = LoginInfoCache.getInstance()
                .getUserInfo(username);
        // if map is not userinfo, username、ipvalue into map.
        if (userIpList.size() == 0) {
            LoginInfoCache.getInstance().put(username, ipValue);
        }
        else {
            if (NO.equals(ConfigHelper.get(KeyConstant.MULTIPOINT_LOGIN))) {
                LoginInfoCache.getInstance().put(username, ipValue);
            }
            else {
                // if client IP is not exists in map,client ip into map
                if (!userIpList.contains(ipValue.get(0))) {
                    userIpList.add(ipValue.get(0));
                    LoginInfoCache.getInstance().put(username, userIpList);
                }
            }
        }
    }
    
    public static LoginServiceImpl getInstance() {
        if (null == loginServiceImpl) {
            loginServiceImpl = new LoginServiceImpl();
        }
        return loginServiceImpl;
    }
}
