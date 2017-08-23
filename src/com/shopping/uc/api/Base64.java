 package com.shopping.uc.api;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.ByteArrayOutputStream;
 import java.io.CharArrayWriter;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.io.Reader;
 import java.io.Writer;
 
 public class Base64
 {
   private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
 
   private static byte[] codes = new byte[256];
 
   static {
     for (int i = 0; i < 256; i++)
       codes[i] = -1;
     for (int i = 65; i <= 90; i++)
       codes[i] = (byte)(i - 65);
     for (int i = 97; i <= 122; i++)
       codes[i] = (byte)(26 + i - 97);
     for (int i = 48; i <= 57; i++)
       codes[i] = (byte)(52 + i - 48);
     codes[43] = 62;
     codes[47] = 63;
   }
 
   public static char[] encode(byte[] data)
   {
     char[] out = new char[(data.length + 2) / 3 * 4];
 
     int i = 0; for (int index = 0; i < data.length; index += 4) {
       boolean quad = false;
       boolean trip = false;
 
       int val = 0xFF & data[i];
       val <<= 8;
       if (i + 1 < data.length) {
         val |= 0xFF & data[(i + 1)];
         trip = true;
       }
       val <<= 8;
       if (i + 2 < data.length) {
         val |= 0xFF & data[(i + 2)];
         quad = true;
       }
       out[(index + 3)] = alphabet[64];
       val >>= 6;
       out[(index + 2)] = alphabet[64];
       val >>= 6;
       out[(index + 1)] = alphabet[(val & 0x3F)];
       val >>= 6;
       out[(index + 0)] = alphabet[(val & 0x3F)];
 
       i += 3;
     }
 
     return out;
   }
 
   public static byte[] decode(char[] data)
   {
     int tempLen = data.length;
     for (int ix = 0; ix < data.length; ix++) {
       if ((data[ix] > 'ÿ') || (codes[data[ix]] < 0)) {
         tempLen--;
       }
 
     }
 
     int len = tempLen / 4 * 3;
     if (tempLen % 4 == 3)
       len += 2;
     if (tempLen % 4 == 2) {
       len++;
     }
     byte[] out = new byte[len];
 
     int shift = 0;
     int accum = 0;
     int index = 0;
 
     for (int ix = 0; ix < data.length; ix++) {
       int value = data[ix] > 'ÿ' ? -1 : codes[data[ix]];
 
       if (value < 0)
         continue;
       accum <<= 6;
       shift += 6;
       accum |= value;
       if (shift < 8)
         continue;
       shift -= 8;
       out[(index++)] = 
         (byte)(accum >> shift & 0xFF);
     }
 
     if (index != out.length) {
       throw new Error("Miscalculated data length (wrote " + 
         index + " instead of " + out.length + ")");
     }
 
     return out;
   }
 
   public static void main(String[] args)
   {
     boolean decode = false;
 
     if (args.length == 0) {
       System.out.println("usage:  java Base64 [-d[ecode]] filename");
       System.exit(0);
     }
     for (int i = 0; i < args.length; i++) {
       if ("-decode".equalsIgnoreCase(args[i]))
         decode = true;
       else if ("-d".equalsIgnoreCase(args[i])) {
         decode = true;
       }
     }
     String filename = args[(args.length - 1)];
     File file = new File(filename);
     if (!file.exists()) {
       System.out.println("Error:  file '" + filename + "' doesn't exist!");
       System.exit(0);
     }
 
     if (decode) {
       char[] encoded = readChars(file);
       byte[] decoded = decode(encoded);
       writeBytes(file, decoded);
     }
     else {
       byte[] decoded = readBytes(file);
       char[] encoded = encode(decoded);
       writeChars(file, encoded);
     }
   }
 
   private static byte[] readBytes(File file) {
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     try {
       InputStream fis = new FileInputStream(file);
       InputStream is = new BufferedInputStream(fis);
       int count = 0;
       byte[] buf = new byte[16384];
       while ((count = is.read(buf)) != -1) {
         if (count > 0)
           baos.write(buf, 0, count);
       }
       is.close();
     }
     catch (Exception e) {
       e.printStackTrace();
     }
 
     return baos.toByteArray();
   }
 
   private static char[] readChars(File file) {
     CharArrayWriter caw = new CharArrayWriter();
     try {
       Reader fr = new FileReader(file);
       Reader in = new BufferedReader(fr);
       int count = 0;
       char[] buf = new char[16384];
       while ((count = in.read(buf)) != -1) {
         if (count > 0)
           caw.write(buf, 0, count);
       }
       in.close();
     }
     catch (Exception e) {
       e.printStackTrace();
     }
 
     return caw.toCharArray();
   }
 
   private static void writeBytes(File file, byte[] data) {
     try {
       OutputStream fos = new FileOutputStream(file);
       OutputStream os = new BufferedOutputStream(fos);
       os.write(data);
       os.close();
     }
     catch (Exception e) {
       e.printStackTrace();
     }
   }
 
   private static void writeChars(File file, char[] data) {
     try {
       Writer fos = new FileWriter(file);
       Writer os = new BufferedWriter(fos);
       os.write(data);
       os.close();
     }
     catch (Exception e) {
       e.printStackTrace();
     }
   }
 }


 
 
 