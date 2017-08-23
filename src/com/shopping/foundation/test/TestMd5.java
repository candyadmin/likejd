 package com.shopping.foundation.test;
 
 import com.shopping.uc.api.UCClient;
 import java.io.PrintStream;
 
 public class TestMd5
 {
   public static void main(String[] args)
   {
     String s = "admin1";
 
     UCClient client = new UCClient();
     System.out.println(client.uc_authcode("admin1", "ECODE"));
     String s1 = client.uc_authcode("9d11637dd94f7290e9d2d5fa7350f82b", "DECODE", "123456");
     System.out.println(s1);
   }
 }



 
 