 package com.shopping.pay.bill.services;
 
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;

import com.shopping.pay.bill.config.BillConfig;
 
 public class BillService
 {
   private static final String BILL_GATEWAY_NEW = "https://www.99bill.com/gateway/recvMerchantInfoAction.htm";
 
   public static String buildForm(BillConfig config, Map<String, String> sParaTemp, String strMethod, String strButtonName)
   {
     List keys = new ArrayList(sParaTemp.keySet());
 
     StringBuffer sbHtml = new StringBuffer();
 
     sbHtml
       .append("<form id=\"99billsubmit\" name=\"99billsubmit\" action=\"https://www.99bill.com/gateway/recvMerchantInfoAction.htm\" method=\"" + 
       strMethod + "\">");
 
     for (int i = 0; i < keys.size(); i++) {
       String name = (String)keys.get(i);
       String value = (String)sParaTemp.get(name);
 
       sbHtml.append("<input type=\"hidden\" name=\"" + name + 
         "\" value=\"" + value + "\"/>");
     }
 
     sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + 
       "\" style=\"display:none;\"></form>");
     sbHtml
       .append("<script>document.forms['99billsubmit'].submit();</script>");
 
     return sbHtml.toString();
   }
 }


 
 
 