 package com.shopping.core.tools.database;
 
 import java.text.SimpleDateFormat;
 import java.util.Date;
 
 public class AppendMessage
 {
   public static String headerMessage()
     throws Exception
   {
     StringBuilder strBuilder = null;
     try {
       SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       strBuilder = new StringBuilder();
       strBuilder.append("/*\n").append("Data Transfer\n")
         .append("Author: shopping\n").append(
         "company:xxx\n").append(
         "Date: " + smf.format(new Date()) + "\n").append("\n");
     } catch (Exception e) {
       throw e;
     }
     return strBuilder.toString();
   }
 
   public static String tableHeaderMessage(String tableName)
     throws Exception
   {
     StringBuilder strBuilder = null;
     try {
       strBuilder = new StringBuilder();
       strBuilder.append("-- ----------------------------\n").append(
         "-- Create Table " + tableName + "\n").append(
         "-- ----------------------------");
     } catch (Exception e) {
       throw e;
     }
     return strBuilder.toString();
   }
 
   public static String insertHeaderMessage()
     throws Exception
   {
     StringBuilder strBuilder = null;
     try {
       strBuilder = new StringBuilder();
       strBuilder.append("-- ----------------------------\n").append(
         "-- Create Datas  \n").append(
         "-- ----------------------------");
     } catch (Exception e) {
       throw e;
     }
     return strBuilder.toString();
   }
 }

