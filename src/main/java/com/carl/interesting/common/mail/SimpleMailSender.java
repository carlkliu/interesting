package com.carl.interesting.common.mail;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import com.carl.interesting.common.util.LogUtil;

/**
 * 简单邮件（不带附件的附件）发送器
 * 
 * @author yue ren
 */
public class SimpleMailSender {
    private static final Log LOG = LogFactory.getLog(SimpleMailSender.class);
    
    /**
     * 以文本格式发送邮件
     * 
     * @param mailInfo 待发送的邮件的信息
     */
    public boolean sendTextMail(MailSenderInfo mailInfo) {
        try {
            // 判断是否需要身份认证
            MyAuthenticator authenticator = null;
            Properties pro = mailInfo.getProperties();
            if (mailInfo.isValidateSsl()) {
                // if by SSL
                pro.put("mail.smtp.starttls.enable", "true");
                pro.put("mail.smtp.socketFactory.class",
                        "javax.net.ssl.SSLSocketFactory");
            }
            if (mailInfo.isValidate()) {
                // 如果需要身份认证，则创建一个密码验证器
                authenticator = new MyAuthenticator(mailInfo.getUserName(),
                        mailInfo.getPassword());
            }
            // 根据邮件会话属性和密码验证器构造一个发送邮件的session
            Session sendMailSession = Session.getInstance(pro, authenticator);
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // 设置邮件消息的主要内容
            String mailContent = mailInfo.getContent();
            mailMessage.setText(mailContent);
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        }
        catch (MessagingException ex) {
            LogUtil.logError(LOG, ex);
            return false;
        }
    }
    
    /**
     * 以HTML格式发送邮件
     * 
     * @param mailInfo 待发送的邮件信息
     */
    public boolean sendHtmlMail(MailSenderInfo mailInfo) {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidateSsl()) {
            // if by SSL
            pro.put("mail.smtp.starttls.enable", "true");
            pro.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
        }
        // 如果需要身份认证，则创建一个密码验证器
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(),
                    mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro,
                authenticator);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            // Message.RecipientType.TO属性表示接收者的类型为TO
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
            Multipart mainPart = new MimeMultipart();
            // 创建一个包含HTML内容的MimeBodyPart
            BodyPart html = new MimeBodyPart();
            // 设置HTML内容
            html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
            mainPart.addBodyPart(html);
            // 将MiniMultipart对象设置为邮件内容
            mailMessage.setContent(mainPart);
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        }
        catch (MessagingException ex) {
            LogUtil.logError(LOG, ex);
        }
        return false;
    }
    
    /**
     * 验证邮箱是否存在
     * 
     * @param email
     * @return
     */
    public boolean verify(String email) {
        if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
            return false;
        }
        String host = "";
        String hostName = email.split("@")[1];
        Record[] result = null;
        SMTPClient client = new SMTPClient();
        try {
            // 查找MX记录
            Lookup lookup = new Lookup(hostName, Type.MX);
            lookup.run();
            if (lookup.getResult() != Lookup.SUCCESSFUL) {
                return false;
            }
            else {
                result = lookup.getAnswers();
            }
            // 连接到邮箱服务器
            for (int i = 0; i < result.length; i++) {
                host = result[i].getAdditionalName().toString();
                client.connect(host);
                if (!SMTPReply.isPositiveCompletion(client.getReplyCode())) {
                    client.disconnect();
                    continue;
                }
                else {
                    break;
                }
            }
            client.login("unissoft");
            client.setSender("x@x.com");
            client.addRecipient(email);
            if (250 == client.getReplyCode()) {
                return true;
            }
        }
        catch (Exception e) {
            LogUtil.logError(LOG, e);
        }
        finally {
            try {
                client.disconnect();
            }
            catch (IOException e) {
                LogUtil.logError(LOG, e);
            }
        }
        return false;
    }
    
    /**
     * <judge the send mail function whether can be used
     * 
     * @return [explain parameter]
     * @return boolean [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public boolean isMailAbleUse() {
        MailSenderInfo mailInfo = new MailSenderInfo();
        boolean flag = true;
        //
        if ("".equals(mailInfo.getFromAddress().trim())
                || "".equals(mailInfo.getMailServerHost())
                || "".equals(mailInfo.getUserName())
                || "".equals(mailInfo.getPassword())) {
            flag = false;
        }
        return flag;
    }
    // /**
    // * test Carl Liu 20170221
    // */
    // public static void main(String[] args) {
    // MailSenderInfo mailInfo = new MailSenderInfo();
    // mailInfo.setSubject("subject");
    // mailInfo.setContent("textContent");
    // mailInfo.setFromAddress("carl.liu@uniswdc.com");
    // mailInfo.setUserName("carl.liu@uniswdc.com");
    // mailInfo.setPassword("Liu19870420mail");
    // mailInfo.setMailServerHost("smtp.exmail.qq.com");
    // mailInfo.setMailServerPort("465");
    // mailInfo.setToAddress("altynai.xu@uniswdc.com");
    // mailInfo.setValidateSsl(true);
    // SimpleMailSender mailSender = new SimpleMailSender();
    // mailSender.sendTextMail(mailInfo);
    // }
}
