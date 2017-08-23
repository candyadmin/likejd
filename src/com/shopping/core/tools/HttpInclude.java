package com.shopping.core.tools;
 
 import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 
 public class HttpInclude
 {
   private static final Logger log = LoggerFactory.getLogger(HttpInclude.class);
 
   public static String sessionIdKey = "JSESSIONID";
   private HttpServletRequest request;
   private HttpServletResponse response;
   private static final String SET_COOKIE_SEPARATOR = "; ";
 
   public HttpInclude(HttpServletRequest request, HttpServletResponse response)
   {
     this.request = request;
     this.response = response;
   }
 
   public String include(String includePath) {
     StringWriter sw = new StringWriter(8192);
     include(includePath, sw);
     return sw.toString();
   }
 
   public void include(String includePath, Writer writer) {
     try {
       if (isRemoteHttpRequest(includePath))
         getRemoteContent(includePath, writer);
       else
         getLocalContent(includePath, writer);
     }
     catch (ServletException e) {
       throw new RuntimeException("include error,path:" + includePath + 
         " cause:" + e, e);
     } catch (IOException e) {
       throw new RuntimeException("include error,path:" + includePath + 
         " cause:" + e, e);
     }
   }
 
   private static boolean isRemoteHttpRequest(String includePath) {
     return (includePath != null) && ((includePath.toLowerCase().startsWith("http://")) || 
       (includePath.toLowerCase().startsWith("https://")));
   }
 
   private void getLocalContent(String includePath, Writer writer) throws ServletException, IOException
   {
     ByteArrayOutputStream outputStream = new ByteArrayOutputStream(8192);
     CustomOutputHttpServletResponseWrapper customResponse = new CustomOutputHttpServletResponseWrapper(this.response, writer, outputStream);
     String url_path = includePath.indexOf("?") > 0 ? includePath.substring(0, includePath.indexOf("?")) : includePath;
     String query = includePath.indexOf("?") > 0 ? includePath.substring(includePath.indexOf("?") + 1) : "";
     String[] params = query.split("&");
     for (String param : params) {
       if ((param != null) && (!param.equals(""))) {
         String[] list = param.split("=");
         if (list.length == 2)
           this.request.setAttribute(list[0], list[1]);
       }
     }
     this.request.getRequestDispatcher(url_path).include(this.request, customResponse);
     customResponse.flushBuffer();
     if (customResponse.useOutputStream) {
       writer.write(outputStream.toString(this.response.getCharacterEncoding()));
     }
     writer.flush();
   }
 
   private void getRemoteContent(String urlString, Writer writer)
     throws MalformedURLException, IOException
   {
     URL url = new URL(getWithSessionIdUrl(urlString));
     URLConnection conn = url.openConnection();
     setConnectionHeaders(urlString, conn);
     InputStream input = conn.getInputStream();
     try {
       Reader reader = new InputStreamReader(input, Utils.getContentEncoding(conn, this.response));
       Utils.copy(reader, writer);
     } finally {
       if (input != null)
         input.close();
     }
     writer.flush();
   }
 
   private void setConnectionHeaders(String urlString, URLConnection conn) {
     conn.setReadTimeout(6000);
     conn.setConnectTimeout(6000);
     String cookie = getCookieString();
     conn.setRequestProperty("Cookie", cookie);
 
     if (log.isDebugEnabled())
       log.debug("request properties:" + conn.getRequestProperties() + " for url:" + urlString);
   }
 
   private String getWithSessionIdUrl(String url)
   {
     return url;
   }
 
   private String getCookieString()
   {
     StringBuffer sb = new StringBuffer(64);
     Cookie[] cookies = this.request.getCookies();
     if (cookies != null) {
       for (Cookie c : cookies) {
         if (sessionIdKey.equals(c.getName())) continue;
         sb.append(c.getName()).append("=").append(c.getValue()).append("; ");
       }
 
     }
 
     String sessionId = Utils.getSessionId(this.request);
     if (sessionId != null) {
       sb.append(sessionIdKey).append("=").append(sessionId).append(
         "; ");
     }
     return sb.toString();
   }
 
   public static class CustomOutputHttpServletResponseWrapper extends HttpServletResponseWrapper {
     public boolean useWriter = false;
     public boolean useOutputStream = false;
     private PrintWriter printWriter;
     private ServletOutputStream servletOutputStream;
 
     public CustomOutputHttpServletResponseWrapper(HttpServletResponse response, Writer customWriter, final OutputStream customOutputStream) {
       super(response);
       this.printWriter = new PrintWriter(customWriter);
       this.servletOutputStream = new ServletOutputStream()
       {
         public void write(int b) throws IOException {
           customOutputStream.write(b);
         }
 
         public void write(byte[] b) throws IOException
         {
           customOutputStream.write(b);
         }
 
         public void write(byte[] b, int off, int len)
           throws IOException
         {
           customOutputStream.write(b, off, len);
         }
       };
     }
 
     public PrintWriter getWriter() throws IOException {
       if (this.useOutputStream)
         throw new IllegalStateException(
           "getOutputStream() has already been called for this response");
       this.useWriter = true;
       return this.printWriter;
     }
 
     public ServletOutputStream getOutputStream() throws IOException
     {
       if (this.useWriter)
         throw new IllegalStateException(
           "getWriter() has already been called for this response");
       this.useOutputStream = true;
       return this.servletOutputStream;
     }
 
     public void flushBuffer() throws IOException
     {
       if (this.useWriter)
         this.printWriter.flush();
       if (this.useOutputStream)
         this.servletOutputStream.flush();
     }
   }
 
   static class Utils
   {
     static Pattern p = Pattern.compile("(charset=)(.*)", 
       2);
 
     static String getContentEncoding(URLConnection conn, HttpServletResponse response)
     {
       String contentEncoding = conn.getContentEncoding();
       if (conn.getContentEncoding() == null) {
         contentEncoding = parseContentTypeForCharset(conn
           .getContentType());
         if (contentEncoding == null)
           contentEncoding = response.getCharacterEncoding();
       }
       else {
         contentEncoding = conn.getContentEncoding();
       }
       return contentEncoding;
     }
 
     private static String parseContentTypeForCharset(String contentType)
     {
       if (contentType == null)
         return null;
       Matcher m = p.matcher(contentType);
       if (m.find()) {
         return m.group(2).trim();
       }
       return null;
     }
 
     private static void copy(Reader in, Writer out) throws IOException {
       char[] buff = new char[8192];
       while (in.read(buff) >= 0)
         out.write(buff);
     }
 
     private static String getSessionId(HttpServletRequest request)
     {
       HttpSession session = request.getSession(false);
       if (session == null) {
         return null;
       }
       return session.getId();
     }
   }
 }
