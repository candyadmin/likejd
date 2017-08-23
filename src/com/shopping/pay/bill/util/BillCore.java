 package com.shopping.pay.bill.util;
 
 public class BillCore
 {
   public static String appendParam(String returnStr, String paramId, String paramValue)
   {
     if (!returnStr.equals("")) {
       if (!paramValue.equals("")) {
         returnStr = returnStr + "&" + paramId + "=" + paramValue;
       }
     }
     else if (!paramValue.equals("")) {
       returnStr = paramId + "=" + paramValue;
     }
 
     return returnStr;
   }
 }


 
 
 