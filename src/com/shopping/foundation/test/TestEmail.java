 package com.shopping.foundation.test;
 
 import com.shopping.core.tools.PopupAuthenticator;

 import java.io.PrintStream;
 import java.util.Properties;

 import javax.mail.Authenticator;
 import javax.mail.BodyPart;
import javax.mail.Message;
 import javax.mail.Message.RecipientType;
 import javax.mail.MessagingException;
 import javax.mail.Session;
 import javax.mail.Transport;
 import javax.mail.internet.AddressException;
 import javax.mail.internet.InternetAddress;
 import javax.mail.internet.MimeBodyPart;
 import javax.mail.internet.MimeMessage;
 import javax.mail.internet.MimeMultipart;
 
 public class TestEmail
 {
   public static boolean sendEmail(String username, String password, String smtp_server, String from_mail_address, String email, String subject, String content)
   {
     boolean ret = true;
     String to_mail_address = email;
     if ((username != null) && (password != null) && (!username.equals("")) && 
       (!password.equals("")) && (!smtp_server.equals(""))) {
       Authenticator auth = new PopupAuthenticator(username, password);
       Properties mailProps = new Properties();
       mailProps.put("mail.smtp.auth", "true");
       mailProps.put("username", username);
       mailProps.put("password", password);
       mailProps.put("mail.smtp.host", smtp_server);
       Session mailSession = Session.getDefaultInstance(mailProps, auth);
       MimeMessage message = new MimeMessage(mailSession);
       try {
         message.setFrom(new InternetAddress(from_mail_address));
         message.setRecipient(Message.RecipientType.TO, 
           new InternetAddress(to_mail_address));
         message.setSubject(subject);
         MimeMultipart multi = new MimeMultipart();
         BodyPart textBodyPart = new MimeBodyPart();
         textBodyPart.setText(content);
         multi.addBodyPart(textBodyPart);
         message.setContent(multi);
         message.saveChanges();
         Transport.send(message);
         ret = true;
       }
       catch (AddressException e) {
         ret = false;
         e.printStackTrace();
       }
       catch (MessagingException e) {
         ret = false;
         e.printStackTrace();
       }
     } else {
       ret = false;
     }
     return ret;
   }
 
   public static void main(String[] args)
   {
     String username = "erikchang";
     String password = "erikchang@830729";
     String smtp_server = "smtp.qq.com";
     String from_mail_address = "erikchang@qq.com";
     String email = "erikchang@163.com";
     String subject = "测试邮箱";
     String content = "这是一个测试邮件！";
     boolean ret = sendEmail(username, password, smtp_server, 
       from_mail_address, email, subject, content);
     if (ret)
       System.out.println("发送邮件成功！");
     else
       System.out.println("发送邮件失败!");
   }
 }



 
 