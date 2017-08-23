 package com.shopping.foundation.test;
 
 import java.io.PrintStream;
 
 public class TestVar
 {
   public static void main(String[] args)
   {
     String suffix = "";
     String imageSuffix = "gif|jpg|jpeg|bmp|png";
     String[] list = imageSuffix.split("\\|");
     for (String l : list) {
       suffix = "*." + l + ";" + suffix;
     }
     System.out.println(suffix.substring(0, suffix.length() - 1));
   }
 }



 
 