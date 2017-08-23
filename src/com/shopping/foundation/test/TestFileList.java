 package com.shopping.foundation.test;
 
 import java.io.File;
 import java.io.PrintStream;
 
 public class TestFileList
 {
   public static void main(String[] args)
   {
     String strPath = "F:\\JAVA_PRO\\shopping\\data\\20120829_1";
     File dir = new File(strPath);
     File[] files = dir.listFiles();
     for (File f : files)
       System.out.println(f.getName());
   }
 }



 
 