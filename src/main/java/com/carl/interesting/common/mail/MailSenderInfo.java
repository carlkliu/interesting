package com.carl.interesting.common.mail;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.carl.interesting.common.util.ConfigHelper;
import com.carl.interesting.common.util.LogUtil;


/**
 * 发送邮件需要使用的基本信息类
 * 
 * @author yue ren
 */
public class MailSenderInfo {
    private static final Log LOG = LogFactory.getLog(MailSenderInfo.class);
    
    /**
     * 发送邮件的服务器的IP和端口
     */
    private String mailServerHost = "";
    
    private String mailServerPort = "";
    
    /**
     * 邮件发送者的地址
     */
    private String fromAddress = "";
    
    /**
     * 邮件接收者的地址
     */
    private String toAddress;
    
    /**
     * 登陆邮件发送服务器的用户名和密码
     */
    private String userName = "";
    
    private String password = "";
    
    /**
     * 是否需要身份验证
     */
    private boolean validate = true;
    
    /**
     * is SSL
     */
    private boolean validateSsl = false;
    
    /**
     * 邮件主题
     */
    private String subject;
    
    /**
     * /邮件的文本内容
     */
    private String content;
    
    /**
     * 邮件附件的文件名
     */
    private String[] attachFileNames;
    
    public void setMailServerHost(String mailServerHost) {
        this.mailServerHost = mailServerHost;
    }
    
    public void setMailServerPort(String mailServerPort) {
        this.mailServerPort = mailServerPort;
    }
    
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getMailServerHost() {
        return mailServerHost;
    }
    
    public String getMailServerPort() {
        return mailServerPort;
    }
    
    /**
     * @return returns validateSsl
     */
    public boolean isValidateSsl() {
        return validateSsl;
    }
    
    /**
     * @param assgin values to validateSsl
     */
    public void setValidateSsl(boolean validateSsl) {
        this.validateSsl = validateSsl;
    }
    
    public boolean isValidate() {
        return validate;
    }
    
    public void setValidate(boolean validate) {
        this.validate = validate;
    }
    
    public String[] getAttachFileNames() {
        return attachFileNames;
    }
    
    public void setAttachFileNames(String[] fileNames) {
        this.attachFileNames = fileNames;
    }
    
    public String getFromAddress() {
        return fromAddress;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getToAddress() {
        return toAddress;
    }
    
    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String textContent) {
        this.content = textContent;
    }
    
    /**
     * 读取生成的mail.xml文件，解析出所要数据
     */
    public MailSenderInfo() {
        Document document = null;
        Node node = null;
        try {
            String mailPath = ConfigHelper.get("mail.xml");
            File file = new File(mailPath);
            if (!file.exists()) {
                return;
            }
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(file);
            node = document.selectSingleNode("/admin/address");
            this.fromAddress = node.getText();
            node = document.selectSingleNode("/admin/mailServerPort");
            this.mailServerPort = node.getText();
            node = document.selectSingleNode("/admin/mailServerHost");
            this.mailServerHost = node.getText();
            node = document.selectSingleNode("/admin/userName");
            this.userName = node.getText();
            node = document.selectSingleNode("/admin/password");
            this.password = node.getText();
            node = document.selectSingleNode("/admin/ssl");
            this.validateSsl = Boolean.parseBoolean(node.getText());
        }
        catch (Exception e) {
            LogUtil.logError(LOG, e, "Failed to read mail configuration");
        }
    }
    
    /**
     * 获得邮件会话属性
     */
    public Properties getProperties() {
        Properties p = new Properties();
        p.put("mail.smtp.host", this.mailServerHost);
        p.put("mail.smtp.port", this.mailServerPort);
        p.put("mail.smtp.auth", validate ? "true" : "false");
        return p;
    }
    
    /**
     * @return
     */
    @Override
    public String toString() {
        return "MailSenderInfo [mailServerHost=" + mailServerHost
                + ", mailServerPort=" + mailServerPort + ", fromAddress="
                + fromAddress + ", toAddress=" + toAddress + ", userName="
                + userName + ", password=" + password + ", validate=" + validate
                + ", validateSsl=" + validateSsl + ", subject=" + subject
                + ", content=" + content + ", attachFileNames="
                + Arrays.toString(attachFileNames) + "]";
    }
    
    /**
     * @return
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(attachFileNames);
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result
                + ((fromAddress == null) ? 0 : fromAddress.hashCode());
        result = prime * result
                + ((mailServerHost == null) ? 0 : mailServerHost.hashCode());
        result = prime * result
                + ((mailServerPort == null) ? 0 : mailServerPort.hashCode());
        result = prime * result
                + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        result = prime * result
                + ((toAddress == null) ? 0 : toAddress.hashCode());
        result = prime * result
                + ((userName == null) ? 0 : userName.hashCode());
        result = prime * result + (validate ? 1231 : 1237);
        result = prime * result + (validateSsl ? 1231 : 1237);
        return result;
    }
    
    /**
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MailSenderInfo other = (MailSenderInfo) obj;
        if (!Arrays.equals(attachFileNames, other.attachFileNames))
            return false;
        if (content == null) {
            if (other.content != null)
                return false;
        }
        else if (!content.equals(other.content))
            return false;
        if (fromAddress == null) {
            if (other.fromAddress != null)
                return false;
        }
        else if (!fromAddress.equals(other.fromAddress))
            return false;
        if (mailServerHost == null) {
            if (other.mailServerHost != null)
                return false;
        }
        else if (!mailServerHost.equals(other.mailServerHost))
            return false;
        if (mailServerPort == null) {
            if (other.mailServerPort != null)
                return false;
        }
        else if (!mailServerPort.equals(other.mailServerPort))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        }
        else if (!password.equals(other.password))
            return false;
        if (subject == null) {
            if (other.subject != null)
                return false;
        }
        else if (!subject.equals(other.subject))
            return false;
        if (toAddress == null) {
            if (other.toAddress != null)
                return false;
        }
        else if (!toAddress.equals(other.toAddress))
            return false;
        if (userName == null) {
            if (other.userName != null)
                return false;
        }
        else if (!userName.equals(other.userName))
            return false;
        if (validate != other.validate)
            return false;
        if (validateSsl != other.validateSsl)
            return false;
        return true;
    }
}
