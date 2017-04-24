package com.carl.interesting.common.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 邮件验证
 * 
 * @author yue ren
 */
public class MyAuthenticator extends Authenticator {
    String userName = null;
    
    String password = null;
    
    public MyAuthenticator() {
    }
    
    public MyAuthenticator(String username, String password) {
        this.userName = username;
        this.password = password;
    }
    
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}
