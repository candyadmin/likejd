 package com.shopping.foundation.test;
 
 import com.shopping.core.tools.CommUtil;
 import com.shopping.plug.login.action.MySecureProtocolSocketFactory;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.util.HashMap;
 import java.util.Map;
 import org.apache.commons.httpclient.HttpClient;
 import org.apache.commons.httpclient.HttpException;
 import org.apache.commons.httpclient.HttpMethod;
 import org.apache.commons.httpclient.methods.PostMethod;
 import org.apache.commons.httpclient.params.HttpMethodParams;
 import org.apache.commons.httpclient.protocol.Protocol;
 import org.nutz.json.Json;
 
 public class HttpClientTest
 {
   private static String sina_login_url = "https://api.weibo.com/oauth2/authorize";
   private static String sina_token_url = "https://api.weibo.com/oauth2/access_token";
   private static String sina_token_info_url = "https://api.weibo.com/oauth2/get_token_info";
 
   public static void main(String[] args)
     throws HttpException, IOException
   {
     String access_token = "";
     String sina_openid = "";
     HttpClient client = new HttpClient();
     Protocol myhttps = new Protocol("https", 
       new MySecureProtocolSocketFactory(), 443);
     Protocol.registerProtocol("https ", myhttps);
     HttpMethod method = new PostMethod(sina_token_url);
     HttpMethodParams params = new HttpMethodParams();
     params.setParameter("client_id", "3863193702");
     params.setParameter("client_secret ", "16b62bbfc99c0d9028c199566429c798");
     params.setParameter("grant_type", "authorization_code");
     params.setParameter("code", "9cc3989375e8205df543dfebf17268b9");
     params.setParameter("redirect_uri", 
       "http://shopping.eicp.net/sina_login_bind.htm");
     method.setParams(params);
     int status = client.executeMethod(method);
     System.out.println("状态值：" + status);
     if (status == 200) {
       Map map = (Map)Json.fromJson(HashMap.class, method
         .getResponseBodyAsString());
       access_token = CommUtil.null2String(map.get("access_token"));
       System.out.println("输出token:");
       System.out.println(method.getResponseBodyAsString());
       if (!access_token.equals("")) {
         method = new PostMethod(sina_token_info_url);
         params.clear();
         params.setParameter("access_token", access_token);
         client.executeMethod(method);
         map = (Map)Json.fromJson(HashMap.class, method
           .getResponseBodyAsString());
         System.out.println("输出uid:");
         System.out.println(method.getResponseBodyAsString());
         sina_openid = CommUtil.null2String(map.get("uid"));
       }
     }
   }
 }



 
 