 package com.shopping.pay.alipay.util.httpClient;
 
 import org.apache.commons.httpclient.NameValuePair;
 
 public class HttpRequest
 {
   public static final String METHOD_GET = "GET";
   public static final String METHOD_POST = "POST";
   private String url = null;
 
   private String method = "POST";
 
   private int timeout = 0;
 
   private int connectionTimeout = 0;
 
   private NameValuePair[] parameters = null;
 
   private String queryString = null;
 
   private String charset = "GBK";
   private String clientIp;
   private HttpResultType resultType = HttpResultType.BYTES;
 
   public HttpRequest(HttpResultType resultType)
   {
     this.resultType = resultType;
   }
 
   public String getClientIp()
   {
     return this.clientIp;
   }
 
   public void setClientIp(String clientIp)
   {
     this.clientIp = clientIp;
   }
 
   public NameValuePair[] getParameters() {
     return this.parameters;
   }
 
   public void setParameters(NameValuePair[] parameters) {
     this.parameters = parameters;
   }
 
   public String getQueryString() {
     return this.queryString;
   }
 
   public void setQueryString(String queryString) {
     this.queryString = queryString;
   }
 
   public String getUrl() {
     return this.url;
   }
 
   public void setUrl(String url) {
     this.url = url;
   }
 
   public String getMethod() {
     return this.method;
   }
 
   public void setMethod(String method) {
     this.method = method;
   }
 
   public int getConnectionTimeout() {
     return this.connectionTimeout;
   }
 
   public void setConnectionTimeout(int connectionTimeout) {
     this.connectionTimeout = connectionTimeout;
   }
 
   public int getTimeout() {
     return this.timeout;
   }
 
   public void setTimeout(int timeout) {
     this.timeout = timeout;
   }
 
   public String getCharset()
   {
     return this.charset;
   }
 
   public void setCharset(String charset)
   {
     this.charset = charset;
   }
 
   public HttpResultType getResultType() {
     return this.resultType;
   }
 
   public void setResultType(HttpResultType resultType) {
     this.resultType = resultType;
   }
 }


 
 
 