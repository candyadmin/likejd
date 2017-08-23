 package com.shopping.pay.alipay.util.httpClient;
 
 import java.io.UnsupportedEncodingException;
 import org.apache.commons.httpclient.Header;
 
 public class HttpResponse
 {
   private Header[] responseHeaders;
   private String stringResult;
   private byte[] byteResult;
 
   public Header[] getResponseHeaders()
   {
     return this.responseHeaders;
   }
 
   public void setResponseHeaders(Header[] responseHeaders) {
     this.responseHeaders = responseHeaders;
   }
 
   public byte[] getByteResult() {
     if (this.byteResult != null) {
       return this.byteResult;
     }
     if (this.stringResult != null) {
       return this.stringResult.getBytes();
     }
     return null;
   }
 
   public void setByteResult(byte[] byteResult) {
     this.byteResult = byteResult;
   }
 
   public String getStringResult() throws UnsupportedEncodingException {
     if (this.stringResult != null) {
       return this.stringResult;
     }
     if (this.byteResult != null) {
       return new String(this.byteResult, "UTF-8");
     }
     return null;
   }
 
   public void setStringResult(String stringResult) {
     this.stringResult = stringResult;
   }
 }


 
 
 