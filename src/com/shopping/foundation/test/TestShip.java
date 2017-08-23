 package com.shopping.foundation.test;
 
 import java.io.PrintStream;
 
 public class TestShip
 {
   public static void main(String[] args)
   {
     double weight = 3.2D;
     int shipping_weight = 1;
     double shipping_fee = 12.0D;
     int shipping_add_weight = 2;
     double shipping_add_fee = 2.0D;
     System.out.println(Math.round(Math.ceil(weight - shipping_weight)));
     double price = shipping_fee + Math.round(Math.ceil(weight - shipping_weight)) * shipping_add_fee / shipping_add_weight;
     System.out.println("总价为:" + price);
   }
 }



 
 