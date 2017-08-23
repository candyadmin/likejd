 package com.shopping.core.tools;
 
 import javax.mail.Authenticator;
 import javax.mail.PasswordAuthentication;
 
 public class PopupAuthenticator extends Authenticator
 {
   private String username;
   private String password;
 
   public PopupAuthenticator(String username, String password)
   {
     this.username = username;
     this.password = password;
   }
 
   public PasswordAuthentication getPasswordAuthentication() {
     return new PasswordAuthentication(this.username, this.password);
   }
 }

