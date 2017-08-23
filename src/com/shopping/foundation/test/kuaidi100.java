 package com.shopping.foundation.test;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.net.URLConnection;
 
 public class kuaidi100
 {
   public static void main(String[] agrs)
   {
     try
     {
       URL url = new URL(
         "http://api.kuaidi100.com/api?id=4c68365adbe58d72&com=shunfeng&nu=024410226400&show=0&muti=1&order=desc");
       URLConnection con = url.openConnection();
       con.setAllowUserInteraction(false);
       InputStream urlStream = url.openStream();
       String type = URLConnection.guessContentTypeFromStream(urlStream);
       String charSet = null;
       if (type == null) {
         type = con.getContentType();
       }
       if ((type == null) || (type.trim().length() == 0) || 
         (type.trim().indexOf("text/html") < 0)) {
         return;
       }
       if (type.indexOf("charset=") > 0)
         charSet = type.substring(type.indexOf("charset=") + 8);
       System.out.println(charSet);
       byte[] b = new byte[10000];
       int numRead = urlStream.read(b);
       String content = new String(b, 0, numRead, charSet);
       while (numRead != -1) {
         numRead = urlStream.read(b);
         if (numRead == -1)
           continue;
         String newContent = new String(b, 0, numRead, charSet);
         content = content + newContent;
       }
 
       System.out.println("content:" + content);
       urlStream.close();
     } catch (MalformedURLException e) {
       e.printStackTrace();
     } catch (IOException e) {
       e.printStackTrace();
     }
   }
 }



 
 