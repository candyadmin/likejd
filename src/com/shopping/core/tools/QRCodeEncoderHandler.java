 package com.shopping.core.tools;
 
 import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;

import com.swetake.util.Qrcode;
 
 public class QRCodeEncoderHandler
 {
   public void encoderQRCode(String content, String imgPath)
   {
     try
     {
       Qrcode qrcodeHandler = new Qrcode();
       qrcodeHandler.setQrcodeErrorCorrect('M');
       qrcodeHandler.setQrcodeEncodeMode('B');
       qrcodeHandler.setQrcodeVersion(7);
 
       System.out.println(content);
       byte[] contentBytes = content.getBytes("gb2312");
 
       BufferedImage bufImg = new BufferedImage(140, 140, 
         1);
 
       Graphics2D gs = bufImg.createGraphics();
 
       gs.setBackground(Color.WHITE);
       gs.clearRect(0, 0, 140, 140);
 
       gs.setColor(Color.BLACK);
 
       int pixoff = 2;
 
       if ((contentBytes.length > 0) && (contentBytes.length < 120)) {
         boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
         for (int i = 0; i < codeOut.length; i++) {
           for (int j = 0; j < codeOut.length; j++)
             if (codeOut[j][i])
               gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
         }
       }
       else
       {
         System.err.println("QRCode content bytes length = " + 
           contentBytes.length + " not in [ 0,120 ]. ");
       }
 
       gs.dispose();
       bufImg.flush();
 
       File imgFile = new File(imgPath);
 
       ImageIO.write(bufImg, "png", imgFile);
     }
     catch (Exception e) {
       e.printStackTrace();
     }
   }
   
   /**
    * 生成二维码图片的二进制
    * @param content
    * @return
    */
   public static byte[] createQRCode(String content) {
       byte[] result = null;
       try {
           Qrcode qrcodeHandler = new Qrcode();
           qrcodeHandler.setQrcodeErrorCorrect('M');
           qrcodeHandler.setQrcodeEncodeMode('B');
           qrcodeHandler.setQrcodeVersion(7);
           
           byte[] contentBytes = content.getBytes("utf-8");

           BufferedImage bufferImgage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
           
           Graphics2D graphics2D = bufferImgage.createGraphics();
           graphics2D.setBackground(Color.WHITE);
           graphics2D.clearRect(0, 0, 200, 200);
           graphics2D.setColor(Color.BLACK);
           int pixoff = 10;
           if (contentBytes.length > 0 && contentBytes.length < 120) {
               boolean[][] matrix = qrcodeHandler.calQrcode(contentBytes);
               for (int i = 0; i < matrix.length; i++) {
                   for (int j = 0; j < matrix.length; j++) {
                       if (matrix[j][i]) {
                           graphics2D.fillRect(j * 4 + pixoff, i * 4 + pixoff, 4, 4);
                       }
                   }
               }
           } else {
               //
           }
           graphics2D.dispose();

           bufferImgage.flush();

           ByteArrayOutputStream output = new ByteArrayOutputStream();
           ImageIO.write(bufferImgage, "png", output);
           result = output.toByteArray();
           output.close();
           
       } catch (Exception e) {
           e.printStackTrace();
       }
       
       return result;
   }
   /**
    * 根据图片的二进制生成一个图片
    * @param data
    * @param fileName
    * @param type
    */
   public static void saveImage(byte[] data, String fileName,String type) {   
 	  
       BufferedImage image = new BufferedImage(300, 300,BufferedImage.TYPE_BYTE_BINARY);   
       ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();   
       try {   
           ImageIO.write(image, type, byteOutputStream);   
           // byte[] date = byteOutputStream.toByteArray();   
           byte[] bytes =  data;   
           //System.out.println("path:" + fileName);   
           RandomAccessFile file = new RandomAccessFile(fileName, "rw");   
           file.write(bytes);   
           file.close();   
       } catch (IOException e) {   
           e.printStackTrace();   
       }   
   }  
 
   public static void main(String[] args)
   {
     String imgPath = "D:/code.png";
 
     String content = "http://localhost/store_1.htm";
 
     QRCodeEncoderHandler handler = new QRCodeEncoderHandler();
     handler.encoderQRCode(content, imgPath);
 
     System.out.println("encoder QRcode success");
   }
 }

