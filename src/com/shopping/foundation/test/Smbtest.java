 package com.shopping.foundation.test;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import jcifs.smb.SmbFile;
 import jcifs.smb.SmbFileInputStream;
 import jcifs.smb.SmbFileOutputStream;
 
 public class Smbtest
 {
   public static void smbGet(String remoteUrl, String localDir)
   {
     InputStream in = null;
     OutputStream out = null;
     try {
       SmbFile smbFile = new SmbFile(remoteUrl);
       String fileName = smbFile.getName();
       File localFile = new File(localDir + File.separator + fileName);
       in = new BufferedInputStream(new SmbFileInputStream(smbFile));
       out = new BufferedOutputStream(new FileOutputStream(localFile));
       byte[] buffer = new byte[1024];
       while (in.read(buffer) != -1) {
         out.write(buffer);
         buffer = new byte[1024];
       }
     } catch (Exception e) {
       e.printStackTrace();
       try
       {
         out.close();
         in.close();
       } catch (IOException e1) {
         e1.printStackTrace();
       }
     }
     finally
     {
       try
       {
         out.close();
         in.close();
       } catch (IOException e) {
         e.printStackTrace();
       }
     }
   }
 
   public static void smbPut(String remoteUrl, String localFilePath)
   {
     InputStream in = null;
     OutputStream out = null;
     try {
       File localFile = new File(localFilePath);
       String fileName = localFile.getName();
       SmbFile remoteFile = new SmbFile(remoteUrl + "/" + fileName);
       in = new BufferedInputStream(new FileInputStream(localFile));
       out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
       byte[] buffer = new byte[1024];
       while (in.read(buffer) != -1) {
         out.write(buffer);
         buffer = new byte[1024];
       }
     } catch (Exception e) {
       e.printStackTrace();
       try
       {
         out.close();
         in.close();
       } catch (IOException e1) {
         e1.printStackTrace();
       }
     }
     finally
     {
       try
       {
         out.close();
         in.close();
       } catch (IOException e) {
         e.printStackTrace();
       }
     }
   }
 
   public static void main(String[] args) {
     smbPut("smb://administrator:123456@192.168.1.102/smb", "E:/公司资料/Com.zip");
   }
 }



 
 