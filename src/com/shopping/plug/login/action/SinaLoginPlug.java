 package com.shopping.plug.login.action;
 
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
 import java.io.IOException;
 import java.io.PrintStream;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import org.apache.commons.httpclient.HttpClient;
 import org.apache.commons.httpclient.HttpException;
 import org.apache.commons.httpclient.methods.GetMethod;
 import org.apache.commons.httpclient.methods.PostMethod;
 import org.apache.commons.httpclient.protocol.Protocol;
 import org.nutz.json.Json;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 
 @Controller
 public class SinaLoginPlug
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
   private String sina_login_url = "https://api.weibo.com/oauth2/authorize";
   private String sina_token_url = "https://api.weibo.com/oauth2/access_token";
   private String sina_token_info_url = "https://api.weibo.com/oauth2/get_token_info";
 
   @RequestMapping({"/sina_login_api.htm"})
   public void sina_login_api(HttpServletRequest request, HttpServletResponse response)
     throws IOException
   {
     SysConfig config = this.configService.getSysConfig();
     String url = this.sina_login_url + "?client_id=" + 
       config.getSina_login_id() + "&redirect_uri=" + 
       CommUtil.getURL(request) + "/sina_login_bind.htm";
     response.sendRedirect(url);
   }
 
   @RequestMapping({"/sina_login_bind.htm"})
   public String sina_login_bind(HttpServletRequest request, HttpServletResponse response, String code)
     throws HttpException, IOException
   {
     String sina_openid = "-1";
     String userName = "";
     String redirect_uri = CommUtil.encode(CommUtil.getURL(request) + 
       "/sina_login_bind.htm");
     String auth_url = "https://api.weibo.com/oauth2/authorize?client_id=" + 
       this.configService.getSysConfig().getSina_login_id() + 
       "&response_type=code&redirect_uri=" + redirect_uri;
     String token_url = "https://api.weibo.com/oauth2/access_token?client_id=" + 
       this.configService.getSysConfig().getSina_login_id() + 
       "&client_secret=" + 
       this.configService.getSysConfig().getSina_login_key() + 
       "&grant_type=authorization_code&redirect_uri=" + 
       redirect_uri + 
       "&code=" + code;
     HttpClient client = new HttpClient();
     Protocol myhttps = new Protocol("https", 
       new MySecureProtocolSocketFactory(), 443);
     Protocol.registerProtocol("https", myhttps);
     PostMethod method = new PostMethod(token_url);
     int status = client.executeMethod(method);
     if (status == 200) {
       Map map = (Map)Json.fromJson(HashMap.class, method
         .getResponseBodyAsString());
       String access_token = CommUtil.null2String(map.get("access_token"));
       String token_info_url = "https://api.weibo.com/oauth2/get_token_info";
       method = new PostMethod(token_info_url);
       method.addParameter("access_token", access_token);
       status = client.executeMethod(method);
       if (status == 200) {
         map = (Map)Json.fromJson(HashMap.class, method
           .getResponseBodyAsString());
         sina_openid = CommUtil.null2String(map.get("uid"));
         String user_info_url = "https://api.weibo.com/2/users/show.json?access_token=" + 
           access_token + "&uid=" + sina_openid;
         GetMethod get = new GetMethod(user_info_url);
         status = client.executeMethod(get);
         if (status == 200) {
           map = (Map)Json.fromJson(HashMap.class, get
             .getResponseBodyAsString());
           userName = CommUtil.null2String(map.get("name"));
           userName = generic_username(userName);
         }
       }
     }
     if (SecurityUserHolder.getCurrentUser() == null) {
       User user = this.userService.getObjByProperty("sina_openid", 
         sina_openid);
       if (user == null) {
         user = new User();
         user.setUserName(userName);
         user.setUserRole("BUYER");
         user.setSina_openid(sina_openid);
         user.setAddTime(new Date());
         user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
         Map params = new HashMap();
         params.put("type", "BUYER");
         List roles = this.roleService.query(
           "select obj from Role obj where obj.type=:type", 
           params, -1, -1);
         user.getRoles().addAll(roles);
         if (this.configService.getSysConfig().isIntegral()) {
           user.setIntegral(this.configService.getSysConfig()
             .getMemberRegister());
           this.userService.save(user);
           IntegralLog log = new IntegralLog();
           log.setAddTime(new Date());
           log.setContent("注册赠送积分:" + 
             this.configService.getSysConfig()
             .getMemberRegister());
           log.setIntegral(this.configService.getSysConfig()
             .getMemberRegister());
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
         request.getSession(false).setAttribute("bind", "sina");
         return "redirect:" + CommUtil.getURL(request) + 
           "/shopping_login.htm?username=" + 
           CommUtil.encode(user.getUsername()) + 
           "&password=123456";
       }
       request.getSession(false).removeAttribute("verify_code");
       return "redirect:" + CommUtil.getURL(request) + 
         "/shopping_login.htm?username=" + 
         CommUtil.encode(user.getUsername()) + "&password=" + 
         "shopping_thid_login_" + user.getPassword();
     }
 
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     user.setSina_openid(sina_openid);
     this.userService.update(user);
     return "redirect:" + CommUtil.getURL(request) + 
       "/buyer/account_bind.htm";
   }
 
   @RequestMapping({"/sina_login_bind_finish.htm"})
   public String sina_login_bind_finish(HttpServletRequest request, HttpServletResponse response, String userName, String password, String bind_already)
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
         user.setQq_openid(SecurityUserHolder.getCurrentUser()
           .getQq_openid());
         request.getSession(false).removeAttribute("verify_code");
 
         this.userService.delete(SecurityUserHolder.getCurrentUser()
           .getId());
         url = "redirect:" + CommUtil.getURL(request) + 
           "/shopping_login.htm?username=" + 
           CommUtil.encode(user.getUsername()) + 
           "&password=" + password;
       } else {
         request.getSession(false)
           .setAttribute("op_title", "用户绑定失败");
         request.getSession(false).setAttribute("url", 
           CommUtil.getURL(request) + "/index.htm");
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
     throws Exception
   {
     SysConfig config = new SysConfig();
     config.setSina_login_id("3863193702");
     config.setSina_login_key("16b62bbfc99c0d9028c199566429c798");
     String redirect_uri = 
       CommUtil.encode("http://shopping.eicp.net/sina_login_bind.htm");
     String auth_url = "https://api.weibo.com/oauth2/authorize?client_id=" + 
       config.getSina_login_id() + 
       "&response_type=code&redirect_uri=" + redirect_uri;
     System.out.println(auth_url);
     String token_url = "https://api.weibo.com/oauth2/access_token?client_id=" + 
       config.getSina_login_id() + 
       "&client_secret=" + 
       config.getSina_login_key() + 
       "&grant_type=authorization_code&redirect_uri=" + 
       redirect_uri + 
       "&code=d729149f1c0db4a07a4b04fd45a5741d";
     System.out.println(token_url);
     HttpClient client = new HttpClient();
     Protocol myhttps = new Protocol("https", 
       new MySecureProtocolSocketFactory(), 443);
     Protocol.registerProtocol("https", myhttps);
     PostMethod method = new PostMethod(token_url);
     int status = client.executeMethod(method);
     if (status == 200) {
       Map map = (Map)Json.fromJson(HashMap.class, method
         .getResponseBodyAsString());
       String access_token = CommUtil.null2String(map.get("access_token"));
       System.out.println("access_token:" + access_token);
       String token_info_url = "https://api.weibo.com/oauth2/get_token_info";
       method = new PostMethod(token_info_url);
       method.addParameter("access_token", access_token);
       status = client.executeMethod(method);
       if (status == 200) {
         map = (Map)Json.fromJson(HashMap.class, method
           .getResponseBodyAsString());
         String uid = CommUtil.null2String(map.get("uid"));
         System.out.println("uid:" + uid);
         String user_info_url = "https://api.weibo.com/2/users/show.json?access_token=" + 
           access_token + "&uid=" + uid;
         GetMethod get = new GetMethod(user_info_url);
         status = client.executeMethod(get);
         if (status == 200) {
           map = (Map)Json.fromJson(HashMap.class, get
             .getResponseBodyAsString());
           System.out.println(method.getResponseBodyAsString());
           String userName = CommUtil.null2String(map.get("name"));
           System.out.println("userName:" + userName);
         }
       }
     }
   }
 }

