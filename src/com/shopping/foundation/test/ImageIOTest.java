 package com.shopping.foundation.test;
 
 import com.shopping.core.tools.CommUtil;
 import java.io.IOException;
 
 public class ImageIOTest
 {
   public static void main(String[] args)
     throws IOException
   {
     String source = "F://fea37b8c-6813-40c6-ad8b-95c47b1ba680.jpg";
     String target = "F://fea37b8c-6813-40c6-ad8b-95c47b1ba680.jpg_small.jpg";
     String target1 = "F://fea37b8c-6813-40c6-ad8b-95c47b1ba680.jpg_small1.jpg";
     CommUtil.createSmall(source, target, 160, 160);
   }
 }



 
 