 package com.shopping.pay.tenpay;
 
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.io.UnsupportedEncodingException;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.Set;
 import java.util.SortedMap;
 import java.util.TreeMap;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;

import com.shopping.pay.tenpay.util.MD5Util;
import com.shopping.pay.tenpay.util.TenpayUtil;
 
 public class ResponseHandler
 {
   private String key;
   private SortedMap parameters;
   private String debugInfo;
   private HttpServletRequest request;
   private HttpServletResponse response;
   private String uriEncoding;
 
   public ResponseHandler(HttpServletRequest request, HttpServletResponse response)
   {
     this.request = request;
     this.response = response;
 
     this.key = "";
     this.parameters = new TreeMap();
     this.debugInfo = "";
 
     this.uriEncoding = "";
 
     Map m = this.request.getParameterMap();
     Iterator it = m.keySet().iterator();
     while (it.hasNext()) {
       String k = (String)it.next();
       String v = ((String[])m.get(k))[0];
       setParameter(k, v);
     }
   }
 
   public String getKey()
   {
     return this.key;
   }
 
   public void setKey(String key)
   {
     this.key = key;
   }
 
   public String getParameter(String parameter)
   {
     String s = (String)this.parameters.get(parameter);
     return s == null ? "" : s;
   }
 
   public void setParameter(String parameter, String parameterValue)
   {
     String v = "";
     if (parameterValue != null) {
       v = parameterValue.trim();
     }
     this.parameters.put(parameter, v);
   }
 
   public SortedMap getAllParameters()
   {
     return this.parameters;
   }
 
   public boolean isTenpaySign()
   {
     StringBuffer sb = new StringBuffer();
     Set es = this.parameters.entrySet();
     Iterator it = es.iterator();
     while (it.hasNext()) {
       Map.Entry entry = (Map.Entry)it.next();
       String k = (String)entry.getKey();
       String v = (String)entry.getValue();
       if (("sign".equals(k)) || (v == null) || ("".equals(v)))
         continue;
       sb.append(k + "=" + v + "&");
     }
 
     sb.append("key=" + getKey());
 
     String enc = TenpayUtil.getCharacterEncoding(this.request, 
       this.response);
     String sign = MD5Util.MD5Encode(sb.toString(), enc).toLowerCase();
 
     String tenpaySign = getParameter("sign").toLowerCase();
 
     setDebugInfo(sb.toString() + " => sign:" + sign + " tenpaySign:" + 
       tenpaySign);
 
     return tenpaySign.equals(sign);
   }
 
   public void sendToCFT(String msg)
     throws IOException
   {
     String strHtml = msg;
     PrintWriter out = getHttpServletResponse().getWriter();
     out.println(strHtml);
     out.flush();
     out.close();
   }
 
   public String getUriEncoding()
   {
     return this.uriEncoding;
   }
 
   public void setUriEncoding(String uriEncoding)
     throws UnsupportedEncodingException
   {
     if (!"".equals(uriEncoding.trim())) {
       this.uriEncoding = uriEncoding;
 
       String enc = TenpayUtil.getCharacterEncoding(this.request, this.response);
       Iterator it = this.parameters.keySet().iterator();
       while (it.hasNext()) {
         String k = (String)it.next();
         String v = getParameter(k);
         v = new String(v.getBytes(uriEncoding.trim()), enc);
         setParameter(k, v);
       }
     }
   }
 
   public String getDebugInfo()
   {
     return this.debugInfo;
   }
 
   protected void setDebugInfo(String debugInfo)
   {
     this.debugInfo = debugInfo;
   }
 
   protected HttpServletRequest getHttpServletRequest() {
     return this.request;
   }
 
   protected HttpServletResponse getHttpServletResponse() {
     return this.response;
   }
 }


 
 
 