 package com.shopping.pay.alipay.services;
 
 import java.io.IOException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.List;
 import java.util.Map;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Node;
 import org.dom4j.io.SAXReader;

import com.shopping.pay.alipay.config.AlipayConfig;
import com.shopping.pay.alipay.util.AlipaySubmit;
 
 public class AlipayService
 {
   private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
 
   public static String create_direct_pay_by_user(AlipayConfig config, Map<String, String> sParaTemp)
   {
     sParaTemp.put("service", "create_direct_pay_by_user");
     sParaTemp.put("partner", config.getPartner());
     sParaTemp.put("return_url", config.getReturn_url());
     sParaTemp.put("notify_url", config.getNotify_url());
     sParaTemp.put("seller_email", config.getSeller_email());
     sParaTemp.put("_input_charset", config.getInput_charset());
 
     String strButtonName = "确认";
 
     return AlipaySubmit.buildForm(config, sParaTemp, "https://mapi.alipay.com/gateway.do?", 
       "get", strButtonName);
   }
 
   public static String query_timestamp(AlipayConfig config)
     throws MalformedURLException, DocumentException, IOException
   {
     String strUrl = "https://mapi.alipay.com/gateway.do?service=query_timestamp&partner=" + 
       config.getPartner();
     StringBuffer result = new StringBuffer();
 
     SAXReader reader = new SAXReader();
     Document doc = reader.read(new URL(strUrl).openStream());
 
     List<Node> nodeList = doc.selectNodes("//alipay/*");
 
     for (Node node : nodeList)
     {
       if ((!node.getName().equals("is_success")) || 
         (!node.getText().equals("T")))
         continue;
       List<Node> nodeList1 = doc
         .selectNodes("//response/timestamp/*");
       for (Node node1 : nodeList1) {
         result.append(node1.getText());
       }
 
     }
 
     return result.toString();
   }
 
   public static String create_partner_trade_by_buyer(AlipayConfig config, Map<String, String> sParaTemp)
   {
     sParaTemp.put("service", "create_partner_trade_by_buyer");
     sParaTemp.put("partner", config.getPartner());
     sParaTemp.put("return_url", config.getReturn_url());
     sParaTemp.put("notify_url", config.getNotify_url());
     sParaTemp.put("seller_email", config.getSeller_email());
     sParaTemp.put("_input_charset", config.getInput_charset());
 
     String strButtonName = "确认";
 
     return AlipaySubmit.buildForm(config, sParaTemp, "https://mapi.alipay.com/gateway.do?", 
       "get", strButtonName);
   }
 
   public static String trade_create_by_buyer(AlipayConfig config, Map<String, String> sParaTemp)
   {
     sParaTemp.put("service", "trade_create_by_buyer");
     sParaTemp.put("partner", config.getPartner());
     sParaTemp.put("return_url", config.getReturn_url());
     sParaTemp.put("notify_url", config.getNotify_url());
     sParaTemp.put("seller_email", config.getSeller_email());
     sParaTemp.put("_input_charset", config.getInput_charset());
 
     String strButtonName = "确认";
 
     return AlipaySubmit.buildForm(config, sParaTemp, "https://mapi.alipay.com/gateway.do?", 
       "get", strButtonName);
   }
 }


 
 
 