 package com.shopping.pay.alipay.util.httpClient;
 
 import java.io.File;
 import java.io.IOException;
 import java.net.UnknownHostException;
 import java.util.ArrayList;
 import java.util.List;
 import org.apache.commons.httpclient.HttpClient;
 import org.apache.commons.httpclient.HttpConnectionManager;
 import org.apache.commons.httpclient.HttpException;
 import org.apache.commons.httpclient.HttpMethod;
 import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
 import org.apache.commons.httpclient.NameValuePair;
 import org.apache.commons.httpclient.methods.GetMethod;
 import org.apache.commons.httpclient.methods.PostMethod;
 import org.apache.commons.httpclient.methods.multipart.FilePart;
 import org.apache.commons.httpclient.methods.multipart.FilePartSource;
 import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
 import org.apache.commons.httpclient.methods.multipart.Part;
 import org.apache.commons.httpclient.methods.multipart.StringPart;
 import org.apache.commons.httpclient.params.HttpClientParams;
 import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
 import org.apache.commons.httpclient.params.HttpMethodParams;
 import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
 
 public class HttpProtocolHandler
 {
   private static String DEFAULT_CHARSET = "GBK";
 
   private int defaultConnectionTimeout = 8000;
 
   private int defaultSoTimeout = 30000;
 
   private int defaultIdleConnTimeout = 60000;
 
   private int defaultMaxConnPerHost = 30;
 
   private int defaultMaxTotalConn = 80;
   private static final long defaultHttpConnectionManagerTimeout = 3000L;
   private HttpConnectionManager connectionManager;
   private static HttpProtocolHandler httpProtocolHandler = new HttpProtocolHandler();
 
   public static HttpProtocolHandler getInstance()
   {
     return httpProtocolHandler;
   }
 
   private HttpProtocolHandler()
   {
     this.connectionManager = new MultiThreadedHttpConnectionManager();
     this.connectionManager.getParams().setDefaultMaxConnectionsPerHost(
       this.defaultMaxConnPerHost);
     this.connectionManager.getParams().setMaxTotalConnections(
       this.defaultMaxTotalConn);
 
     IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
     ict.addConnectionManager(this.connectionManager);
     ict.setConnectionTimeout(this.defaultIdleConnTimeout);
 
     ict.start();
   }
 
   public HttpResponse execute(HttpRequest request)
   {
     HttpClient httpclient = new HttpClient(this.connectionManager);
 
     int connectionTimeout = this.defaultConnectionTimeout;
     if (request.getConnectionTimeout() > 0) {
       connectionTimeout = request.getConnectionTimeout();
     }
     httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(
       connectionTimeout);
 
     int soTimeout = this.defaultSoTimeout;
     if (request.getTimeout() > 0) {
       soTimeout = request.getTimeout();
     }
     httpclient.getHttpConnectionManager().getParams().setSoTimeout(
       soTimeout);
 
     httpclient.getParams().setConnectionManagerTimeout(
       3000L);
 
     String charset = request.getCharset();
     charset = charset == null ? DEFAULT_CHARSET : charset;
     HttpMethod method = null;
 
     if (request.getMethod().equals("GET")) {
       method = new GetMethod(request.getUrl());
       method.getParams().setCredentialCharset(charset);
 
       method.setQueryString(request.getQueryString());
     } else {
       method = new PostMethod(request.getUrl());
       ((PostMethod)method).addParameters(request.getParameters());
       method.addRequestHeader("Content-Type", 
         "application/x-www-form-urlencoded; text/html; charset=" + 
         charset);
     }
 
     method.addRequestHeader("User-Agent", "Mozilla/4.0");
     HttpResponse response = new HttpResponse();
     try
     {
       httpclient.executeMethod(method);
       if (request.getResultType().equals(HttpResultType.STRING))
         response.setStringResult(method.getResponseBodyAsString());
       else if (request.getResultType().equals(HttpResultType.BYTES)) {
         response.setByteResult(method.getResponseBody());
       }
       response.setResponseHeaders(method.getResponseHeaders());
     }
     catch (UnknownHostException ex)
     {
       return null;
     }
     catch (IOException ex)
     {
       return null;
     }
     catch (Exception ex)
     {
       return null;
     } finally {
       method.releaseConnection(); } method.releaseConnection();
 
     return response;
   }
 
   public HttpResponse execute(HttpRequest request, String strParaFileName, String strFilePath)
     throws HttpException, IOException
   {
     HttpClient httpclient = new HttpClient(this.connectionManager);
 
     int connectionTimeout = this.defaultConnectionTimeout;
     if (request.getConnectionTimeout() > 0) {
       connectionTimeout = request.getConnectionTimeout();
     }
     httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(
       connectionTimeout);
 
     int soTimeout = this.defaultSoTimeout;
     if (request.getTimeout() > 0) {
       soTimeout = request.getTimeout();
     }
     httpclient.getHttpConnectionManager().getParams().setSoTimeout(
       soTimeout);
 
     httpclient.getParams().setConnectionManagerTimeout(
       3000L);
 
     String charset = request.getCharset();
     charset = charset == null ? DEFAULT_CHARSET : charset;
     HttpMethod method = null;
 
     if (request.getMethod().equals("GET")) {
       method = new GetMethod(request.getUrl());
       method.getParams().setCredentialCharset(charset);
 
       method.setQueryString(request.getQueryString());
     } else if ((strParaFileName.equals("")) && (strFilePath.equals("")))
     {
       method = new PostMethod(request.getUrl());
       ((PostMethod)method).addParameters(request.getParameters());
       method.addRequestHeader("Content-Type", 
         "application/x-www-form-urlencoded; text/html; charset=" + 
         charset);
     }
     else {
       method = new PostMethod(request.getUrl());
       List parts = new ArrayList();
       for (int i = 0; i < request.getParameters().length; i++) {
         parts.add(
           new StringPart(request.getParameters()[i].getName(), 
           request.getParameters()[i].getValue(), charset));
       }
 
       parts.add(
         new FilePart(strParaFileName, 
         new FilePartSource(new File(strFilePath))));
 
       ((PostMethod)method).setRequestEntity(
         new MultipartRequestEntity((Part[])parts.toArray(new Part[0]), new HttpMethodParams()));
     }
 
     method.addRequestHeader("User-Agent", "Mozilla/4.0");
     HttpResponse response = new HttpResponse();
     try
     {
       httpclient.executeMethod(method);
       if (request.getResultType().equals(HttpResultType.STRING))
         response.setStringResult(method.getResponseBodyAsString());
       else if (request.getResultType().equals(HttpResultType.BYTES)) {
         response.setByteResult(method.getResponseBody());
       }
       response.setResponseHeaders(method.getResponseHeaders());
     }
     catch (UnknownHostException ex)
     {
       return null;
     }
     catch (IOException ex)
     {
       return null;
     }
     catch (Exception ex)
     {
       return null;
     } finally {
       method.releaseConnection(); } method.releaseConnection();
 
     return response;
   }
 
   protected String toString(NameValuePair[] nameValues)
   {
     if ((nameValues == null) || (nameValues.length == 0)) {
       return "null";
     }
 
     StringBuffer buffer = new StringBuffer();
 
     for (int i = 0; i < nameValues.length; i++) {
       NameValuePair nameValue = nameValues[i];
 
       if (i == 0)
         buffer.append(nameValue.getName() + "=" + nameValue.getValue());
       else {
         buffer.append("&" + nameValue.getName() + "=" + 
           nameValue.getValue());
       }
     }
 
     return buffer.toString();
   }
 }


 
 
 