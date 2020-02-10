package com.zsh.teach_oa.utils;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * 邮件工具类
 */
public class MailUtils {

    //发送邮件的方法
    public static void sendMail(JavaMailSenderImpl mailSender, String addresseeQQ, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setSubject("作业未提交");
        mail.setText(message);
        //发送人邮箱
        mail.setFrom("1393602726@qq.com");
        //收件人邮箱
        mail.setTo(addresseeQQ + "@qq.com");
        mailSender.send(mail);
    }
}
