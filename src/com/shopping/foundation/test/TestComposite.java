 package com.shopping.foundation.test;
 
 import java.io.PrintStream;
 
 public class TestComposite
 {
   public static void main(String[] args)
   {
     String[] ele = { "红色，黑色，黄色，紫色", "XL，XXL，XXXL", "35，36，37" };
 
     StringHandler(ele);
   }
 
   public static String StringHandler(String[] ele)
   {
     String res = "";
     int len = ele.length;
     String[] ts = ele[1].split("，");
     String[][] abc = new String[len][ts.length];
     for (int f = 0; f < ele.length; f++) {
       String[] sub = ele[f].split("，");
       abc[f] = sub;
     }
     String[][] ret = doExchange(abc);
     String[] result = ret[0];
     System.out.println("共有：" + result.length + "种组合！");
     for (int i = 0; i < result.length; i++) {
       System.out.println(result[i]);
     }
     return res;
   }
 
   private static String[][] doExchange(String[][] doubleArrays)
   {
     int len = doubleArrays.length;
     if (len >= 2) {
       int len1 = doubleArrays[0].length;
       int len2 = doubleArrays[1].length;
       int newlen = len1 * len2;
       String[] temp = new String[newlen];
       int index = 0;
       for (int i = 0; i < len1; i++) {
         for (int j = 0; j < len2; j++) {
           temp[index] = 
             (doubleArrays[0][i] + "   |    " + 
             doubleArrays[1][j]);
           index++;
         }
       }
       String[][] newArray = new String[len - 1][];
       for (int i = 2; i < len; i++) {
         newArray[(i - 1)] = doubleArrays[i];
       }
       newArray[0] = temp;
       return doExchange(newArray);
     }
     return doubleArrays;
   }
 }



 
 