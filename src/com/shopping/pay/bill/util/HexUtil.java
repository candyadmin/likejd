 package com.shopping.pay.bill.util;
 
 public class HexUtil
 {
   private static final String HEX_CHARS = "0123456789abcdef";
 
   public static String toHexString(byte[] b)
   {
     StringBuffer sb = new StringBuffer();
     for (int i = 0; i < b.length; i++) {
       sb.append("0123456789abcdef".charAt(b[i] >>> 4 & 0xF));
       sb.append("0123456789abcdef".charAt(b[i] & 0xF));
     }
     return sb.toString();
   }
 
   public static byte[] toByteArray(String s)
   {
     byte[] buf = new byte[s.length() / 2];
     int j = 0;
     for (int i = 0; i < buf.length; i++) {
       buf[i] = 
         (byte)(Character.digit(s.charAt(j++), 16) << 4 | 
         Character.digit(s.charAt(j++), 16));
     }
     return buf;
   }
 }


 
 
 