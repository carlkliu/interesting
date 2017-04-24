package com.carl.interesting.common.constants;

/**
 * response front-end error code
 * 
 * @author Tianbao Liu
 * @version [version, Sep 2, 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class ResponseFrontEndErrorCode {
    // ===============================default================
    public static final int DEFAULT_ERROR = -1;
    
    // ===============================common================
    /**
     * session is invalid
     */
    public static final int SESSION_INVALID = 1;
    
    /**
     * user login other place
     */
    public static final int USER_LOGIN_OTHER_PLACE = 2;
    
    // ===============================login================
    /**
     * username or password incorrect
     */
    public static final int USERNAME_OR_PASSWORD_INCORRECT = 1001;
    
    // ===============================host================
    /**
     * detected cluster when add host ,response the error code to front-end
     */
    public static final int IS_IMPORT = 2001;
    
    // ===============================service(mon,mds,osd)================
    public static final int IS_DEL_LAST_MON = 3001;
    // ===============================pool================
    // ===============================access================
    // ===============================user================
    // ===============================monitor================
    // ===============================warning================
    // ===============================dashboard================
    // ===============================license================
}
