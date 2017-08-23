 package com.shopping.pay;
 
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

import com.shopping.core.tools.Md5Encrypt;
 
 public class Kuaiqian
 {
   public static String calSignMsg(String inputCharset, String pageUrl, String bgUrl, String version, String language, String signType, String merchantAcctId, String payerName, String payerContactType, String payerContact, String orderId, String orderAmount, String orderTime, String productName, String productNum, String productId, String productDesc, String ext1, String ext2, String payType, String bankId, String pid, String key)
   {
     Map params = new HashMap();
     params.put("inputCharset", inputCharset);
     params.put("pageUrl", pageUrl);
     params.put("bgUrl", bgUrl);
     params.put("version", version);
     params.put("language", language);
     params.put("merchantAcctId", merchantAcctId);
     params.put("signType", signType);
     params.put("payerName", payerName);
     params.put("payerContactType", payerContactType);
     params.put("payerContact", payerContact);
     params.put("orderId", orderId);
     params.put("orderAmount", orderAmount);
     params.put("orderTime", orderTime);
     params.put("productName", productName);
     params.put("productNum", productNum);
     params.put("productId", productId);
     params.put("productDesc", productDesc);
     params.put("ext1", ext1);
     params.put("ext2", ext2);
     params.put("payType", payType);
     params.put("bankId", bankId);
     params.put("pid", pid);
     params.put("key", key);
     String sign = Md5Encrypt.md5(getContent(params));
 
     return sign;
   }
 
   private static String getContent(Map params)
   {
     List keys = new ArrayList(params.keySet());
     String prestr = "";
     for (int i = 0; i < keys.size(); i++) {
       System.out.println(keys.get(i));
     }
     for (int i = 0; i < keys.size(); i++) {
       String key = (String)keys.get(i);
       String value = (String)params.get(key);
 
       if (!value.equals("")) {
         if (i == keys.size() - 1)
           prestr = prestr + key + "={" + value + "}";
         else {
           prestr = prestr + key + "={" + value + "}&";
         }
       }
     }
 
     return prestr;
   }
 }


 
 
 