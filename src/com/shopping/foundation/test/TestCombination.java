 package com.shopping.foundation.test;
 
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 
 public class TestCombination
 {
   public static void main(String[] arg)
   {
     List lists = new ArrayList();
     String[] list1 = { "A", "B", "C", "D" };
     String[] list2 = { "A1", "B1", "C1", "D1", "E1" };
     String[] list3 = { "A2", "B2", "C3" };
     lists.add(Arrays.asList(list1));
     lists.add(Arrays.asList(list2));
     lists.add(Arrays.asList(list3));
     String[][] str = { { "A", "B", "C", "D" }, 
       { "A1", "B1", "C1", "D1", "E1" }, { "A2", "B2", "C3" }, { "A3", "B3" } };
     int max = 1;
     for (int i = 0; i < str.length; i++) {
       max *= str[i].length;
     }
 
     for (int i = 0; i < max; i++) {
       String s = "";
       int temp = 1;
       for (int j = 0; j < str.length; j++) {
         temp *= str[j].length;
         s = s + str[j][(i / (max / temp) % str[j].length)];
       }
       System.out.println("第 " + (i + 1) + " 个： " + s);
     }
 
     System.out.println(max);
   }
 }



 
 