package com.carl.interesting.user.service;

import com.carl.interesting.common.constants.KeyConstant;

/**
 * user service
 * 
 * @author Carl Liu
 * @version [version, 1 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class UserServiceImpl {
    private static UserServiceImpl userServiceImpl;
    
    private UserServiceImpl() {
    }
    
    public boolean validUser(String userName, String password) {
        boolean flag = false;
        String username = KeyConstant.USERNAME;
        String pwd = KeyConstant.PASSWORD;
        if (password.equals(pwd) && userName.equals(username)) {
            flag = true;
        }
        return flag;
    }
    /**
     * add User Entry
     * 
     * @param userEntry [explain parameter]
     * @return void [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    // public void addUserEntry(JSONObject userEntry) {
    // // TODO
    // }
    /**
     * delete User Entry By Id
     * 
     * @param userId
     * @return [explain parameter]
     * @return boolean [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    // public boolean deleteUserEntryById(String userId) {
    // // TODO
    // return true;
    // }
    /**
     * update User Entry By Id
     * 
     * @param userId [explain parameter]
     * @return void [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    // public void updateUserEntryById(String userId) {
    // // TODO
    // }
    /**
     * get all users
     * 
     * @return [explain parameter]
     * @return JSONObject [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    
    // public JSONObject getAllUsers() {
    // // TODO
    // return null;
    // }
    /**
     * get Users By UserId
     * 
     * @param userId
     * @return [explain parameter]
     * @return JSONObject [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    // public JSONObject getUsersByUserId(String userId) {
    // // TODO
    // return null;
    // }
    public static UserServiceImpl getInstance() {
        if (null == userServiceImpl) {
            userServiceImpl = new UserServiceImpl();
        }
        return userServiceImpl;
    }
}
