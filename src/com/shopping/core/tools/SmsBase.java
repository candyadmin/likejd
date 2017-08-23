 package com.shopping.core.tools;
 
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.UnsupportedEncodingException;
 import java.net.HttpURLConnection;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.net.URLEncoder;
 
 public class SmsBase
 {
   private String url;
   private String id;
   private String pwd;
 
   public SmsBase(String url, String id, String pwd)
   {
     this.url = url;
     this.id = id;
     this.pwd = pwd;
   }
 
   public String SendSms(String mobile, String content) throws UnsupportedEncodingException
   {
     Integer x_ac = Integer.valueOf(10);
     HttpURLConnection httpconn = null;
     String result = "-20";
     String memo = content.length() < 70 ? content.trim() : content.trim()
       .substring(0, 70);
     StringBuilder sb = new StringBuilder();
     sb.append(this.url);
     sb.append("?id=").append(this.id);
     sb.append("&pwd=").append(this.pwd);
     sb.append("&to=").append(mobile);
     sb.append("&content=").append(URLEncoder.encode(content, "gb2312"));
     try {
       URL url = new URL(sb.toString());
       httpconn = (HttpURLConnection)url.openConnection();
       BufferedReader rd = new BufferedReader(
         new InputStreamReader(httpconn.getInputStream()));
       result = rd.readLine();
       rd.close();
     }
     catch (MalformedURLException e) {
       e.printStackTrace();
     }
     catch (IOException e) {
       e.printStackTrace();
     } finally {
       if (httpconn != null) {
         httpconn.disconnect();
         httpconn = null;
       }
     }
 
     return result;
   }
 }

