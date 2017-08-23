 package com.shopping.plug.login.action;
 
 import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.Md5Encrypt;
import com.shopping.foundation.domain.Album;
import com.shopping.foundation.domain.IntegralLog;
import com.shopping.foundation.domain.SysConfig;
import com.shopping.foundation.domain.User;
import com.shopping.foundation.service.IAlbumService;
import com.shopping.foundation.service.IIntegralLogService;
import com.shopping.foundation.service.IRoleService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.IUserConfigService;
import com.shopping.foundation.service.IUserService;
 
 @Controller
 public class QQLoginPlug
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IRoleService roleService;
 
   @Autowired
   private IAlbumService albumService;
 
   @Autowired
   private IIntegralLogService integralLogService;
   private String qq_login_url = "https://graph.qq.com/oauth2.0/authorize";
   private String qq_access_token = "https://graph.qq.com/oauth2.0/authorize";
 
   @RequestMapping({"/qq_login_api.htm"})
   public void qq_login_api(HttpServletRequest request, HttpServletResponse response)
     throws IOException
   {
     String redirect_uri = CommUtil.encode(CommUtil.getURL(request) + "/qq_login_bind.htm");
     String auth_url = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=" + 
       this.configService.getSysConfig().getQq_login_id() + 
       "&redirect_uri=" + redirect_uri + "&state=shopping&scope=get_user_info";
     response.sendRedirect(auth_url);
   }
 
   @RequestMapping({"/qq_login_bind.htm"})
   public String qq_login_bind(HttpServletRequest request, HttpServletResponse response, String code)
   {
     String redirect_uri = CommUtil.encode(CommUtil.getURL(request) + "/qq_login_bind.htm");
     String token_url = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=" + 
       this.configService.getSysConfig().getQq_login_id() + 
       "&client_secret=" + this.configService.getSysConfig().getQq_login_key() + 
       "&code=" + code + "&redirect_uri=" + redirect_uri;
 
     String[] access_token_callback = CommUtil.null2String(
       getHttpContent(token_url, "UTF-8", "GET")).split("&");
     String access_token = access_token_callback[0].split("=")[1];
     String me_url = "https://graph.qq.com/oauth2.0/me?access_token=" + access_token;
 
     String me_callback = CommUtil.null2String(
       getHttpContent(me_url, "UTF-8", "GET")).replaceAll("callback\\(", "").replaceAll("\\);", "");
     Map me_map = (Map)Json.fromJson(HashMap.class, me_callback);
     String qq_openid = CommUtil.null2String(me_map.get("openid"));
     String user_info_url = "https://graph.qq.com/user/get_user_info?access_token=" + 
       access_token + "&oauth_consumer_key=" + me_map.get("client_id") + "&openid=" + qq_openid;
     String user_info_callback = getHttpContent(user_info_url, "UTF-8", "GET");
     Map user_map = (Map)Json.fromJson(HashMap.class, user_info_callback);
     System.out.println("用户名：" + user_map.get("nickname"));
     if (SecurityUserHolder.getCurrentUser() == null) {
       String userName = generic_username(CommUtil.null2String(user_map.get("nickname")));
       User user = this.userService.getObjByProperty("qq_openid", qq_openid);
       if (user == null) {
         user = new User();
         user.setUserName(userName);
         user.setUserRole("BUYER");
         user.setQq_openid(qq_openid);
         user.setAddTime(new Date());
         user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
         Map params = new HashMap();
         params.put("type", "BUYER");
         List roles = this.roleService.query(
           "select obj from Role obj where obj.type=:type", params, -1, -1);
         user.getRoles().addAll(roles);
         if (this.configService.getSysConfig().isIntegral()) {
           user.setIntegral(this.configService.getSysConfig().getMemberRegister());
           this.userService.save(user);
           IntegralLog log = new IntegralLog();
           log.setAddTime(new Date());
           log.setContent("注册赠送积分:" + this.configService.getSysConfig().getMemberRegister());
           log.setIntegral(this.configService.getSysConfig().getMemberRegister());
           log.setIntegral_user(user);
           log.setType("reg");
           this.integralLogService.save(log);
         } else {
           this.userService.save(user);
         }
 
         Album album = new Album();
         album.setAddTime(new Date());
         album.setAlbum_default(true);
         album.setAlbum_name("默认相册");
         album.setAlbum_sequence(-10000);
         album.setUser(user);
         this.albumService.save(album);
         request.getSession(false).removeAttribute("verify_code");
         request.getSession(false).setAttribute("bind", "qq");
         return "redirect:" + CommUtil.getURL(request) + "/shopping_login.htm?username=" + 
           CommUtil.encode(user.getUsername()) + "&password=123456";
       }
       request.getSession(false).removeAttribute("verify_code");
       return "redirect:" + CommUtil.getURL(request) + "/shopping_login.htm?username=" + 
         CommUtil.encode(user.getUsername()) + "&password=" + "shopping_thid_login_" + user.getPassword();
     }
 
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     user.setQq_openid(qq_openid);
     this.userService.update(user);
     return "redirect:" + CommUtil.getURL(request) + "/buyer/account_bind.htm";
   }
 
   @RequestMapping({"/qq_login_bind_finish.htm"})
   public String qq_login_bind_finish(HttpServletRequest request, HttpServletResponse response, String userName, String password, String bind_already)
   {
     String url = "redirect:" + CommUtil.getURL(request) + "/index.htm";
     if (!CommUtil.null2String(bind_already).equals("")) {
       User user = this.userService.getObjByProperty("userName", userName);
       if (user == null) {
         request.getSession(false).setAttribute("op_title", "用户绑定失败");
         request.getSession(false).setAttribute("url", url);
         url = "redirect:" + CommUtil.getURL(request) + "/error.htm";
       }
       else if (Md5Encrypt.md5(password).toLowerCase().equals(
         user.getPassword())) {
         user.setQq_openid(SecurityUserHolder.getCurrentUser().getQq_openid());
         request.getSession(false).removeAttribute("verify_code");
 
         this.userService.delete(SecurityUserHolder.getCurrentUser().getId());
         url = "redirect:" + CommUtil.getURL(request) + "/shopping_login.htm?username=" + 
           CommUtil.encode(user.getUsername()) + "&password=" + password;
       } else {
         request.getSession(false).setAttribute("op_title", "用户绑定失败");
         request.getSession(false).setAttribute("url", CommUtil.getURL(request) + "/index.htm");
         url = "redirect:" + CommUtil.getURL(request) + "/error.htm";
       }
     }
     else
     {
       User user = SecurityUserHolder.getCurrentUser();
       user.setUserName(userName);
       user.setPassword(Md5Encrypt.md5(password).toLowerCase());
       this.userService.update(user);
     }
     request.getSession(false).removeAttribute("verify_code");
     request.getSession(false).removeAttribute("bind");
     return url;
   }
 
   public static String getHttpContent(String url, String charSet, String method)
   {
     HttpURLConnection connection = null;
     String content = "";
     try {
       URL address_url = new URL(url);
       connection = (HttpURLConnection)address_url.openConnection();
       connection.setRequestMethod("GET");
 
       connection.setConnectTimeout(1000000);
       connection.setReadTimeout(1000000);
 
       int response_code = connection.getResponseCode();
       if (response_code == 200) {
         InputStream in = connection.getInputStream();
         BufferedReader reader = new BufferedReader(
           new InputStreamReader(in, charSet));
         String line = null;
         while ((line = reader.readLine()) != null) {
           content = content + line;
         }
         String str1 = content;
         return str1;
       }
     } catch (MalformedURLException e) {
       e.printStackTrace();
     } catch (IOException e) {
       e.printStackTrace();
     } finally {
       if (connection != null)
         connection.disconnect();
     }
     if (connection != null) {
       connection.disconnect();
     }
 
     return "";
   }
 
   private String generic_username(String userName)
   {
     String name = userName;
     User user = this.userService.getObjByProperty("userName", name);
     if (user != null) {
       for (int i = 1; i < 1000000; i++) {
         name = name + i;
         user = this.userService.getObjByProperty("userName", name);
         if (user == null) {
           break;
         }
       }
     }
     return name;
   }
 
   public static void main(String[] args)
   {
     SysConfig config = new SysConfig();
     config.setQq_login_id("100359491");
     config.setQq_login_key("a34bcaef0487e650238983abc0fbae7c");
     String redirect_uri = 
       CommUtil.encode("http://shopping.eicp.net/qq_login_bind.htm");
     String auth_url = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=" + 
       config.getQq_login_id() + "&redirect_uri=" + redirect_uri + "&state=shopping&scope=get_user_info";
     System.out.println(auth_url);
 
     String token_url = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=" + 
       config.getQq_login_id() + "&client_secret=" + 
       config.getQq_login_key() + "&code=9873676D49030659CF025A0B9FF9F0B8&redirect_uri=" + redirect_uri;
 
     String me_url = "https://graph.qq.com/oauth2.0/me?access_token=1CA359B424836978AAA1424B83C1B5A3";
 
     String user_info_url = "https://graph.qq.com/user/get_user_info?access_token=1CA359B424836978AAA1424B83C1B5A3&oauth_consumer_key=100359491&openid=9A6383AD4B58E8B1ACF65DC68E0B3B68";
 
     System.out.println("返回值为：" + getHttpContent(user_info_url, "UTF-8", "GET"));
   }
 }

