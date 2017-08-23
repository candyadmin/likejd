 package com.shopping.pay;
 
 import java.io.BufferedReader;
 import java.io.InputStreamReader;
 import java.net.HttpURLConnection;
 import java.net.URL;
 
 public class CheckURL
 {
   public static String check(String urlvalue)
   {
     String inputLine = "";
     try
     {
       URL url = new URL(urlvalue);
 
       HttpURLConnection urlConnection = (HttpURLConnection)url
         .openConnection();
 
       BufferedReader in = new BufferedReader(
         new InputStreamReader(urlConnection.getInputStream()));
 
       inputLine = in.readLine().toString();
     } catch (Exception e) {
       e.printStackTrace();
     }
 
     return inputLine;
   }
 }


 
 
 