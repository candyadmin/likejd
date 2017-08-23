 package com.shopping.view.web.action;
 
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
 import com.shopping.foundation.service.IUserService;
 import com.shopping.uc.api.UCClient;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.servlet.ServletException;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 
 @Controller
 public class UCViewAction
 {
   private static final long serialVersionUID = -7377364931916922413L;
   public static boolean IN_DISCUZ = true;
   public static String UC_CLIENT_VERSION = "1.5.0";
   public static String UC_CLIENT_RELEASE = "20081031";
   public static boolean API_DELETEUSER = true;
 
   public static boolean API_RENAMEUSER = true;
 
   public static boolean API_GETTAG = true;
   public static boolean API_SYNLOGIN = true;
 
   public static boolean API_SYNLOGOUT = true;
 
   public static boolean API_UPDATEPW = true;
   public static boolean API_UPDATEBADWORDS = true;
   public static boolean API_UPDATEHOSTS = true;
   public static boolean API_UPDATEAPPS = true;
   public static boolean API_UPDATECLIENT = true;
   public static boolean API_UPDATECREDIT = true;
   public static boolean API_GETCREDITSETTINGS = true;
 
   public static boolean API_GETCREDIT = true;
   public static boolean API_UPDATECREDITSETTINGS = true;
 
   public static String API_RETURN_SUCCEED = "1";
   public static String API_RETURN_FAILED = "-1";
   public static String API_RETURN_FORBIDDEN = "-2";
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IRoleService roleService;
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IIntegralLogService integralLogService;
 
   @Autowired
   private IAlbumService albumService;
 
   @RequestMapping({"/api/uc_login.htm"})
   public void uc_login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { String result = uc_answer(request, response);
     response.getWriter().print(result);
   }
 
   private String uc_answer(HttpServletRequest request, HttpServletResponse response)
   {
     String code = request.getParameter("code");
     if (code == null)
       return API_RETURN_FAILED;
     Map get = new HashMap();
     code = new UCClient().uc_authcode(code, "DECODE");
     parse_str(code, get);
     if (get.isEmpty()) {
       return "Invalid Request";
     }
     if (time() - tolong(get.get("time")) > 3600L) {
       return "Authracation has expiried";
     }
     String action = (String)get.get("action");
     if (action == null)
       return API_RETURN_FAILED;
     if (action.equals("test")) {
       return API_RETURN_SUCCEED;
     }
     if (action.equals("deleteuser"))
       return API_RETURN_SUCCEED;
     if (action.equals("renameuser"))
       return API_RETURN_SUCCEED;
     if (action.equals("gettag")) {
       if (!API_GETTAG) {
         return API_RETURN_FORBIDDEN;
       }
       return API_RETURN_SUCCEED;
     }if (action.equals("synlogin")) {
       if (!API_SYNLOGIN)
         return API_RETURN_FORBIDDEN;
       shopping_login(request, response, get);
     } else if (action.equals("synlogout")) {
       if (!API_SYNLOGOUT)
         return API_RETURN_FORBIDDEN;
       shopping_logout(request, response, get); } else {
       if (action.equals("updateclient")) {
         if (!API_UPDATECLIENT) {
           return API_RETURN_FORBIDDEN;
         }
         return API_RETURN_SUCCEED;
       }if (action.equals("updatepw")) {
         if (!API_UPDATEPW)
           return API_RETURN_FORBIDDEN;
         shopping_update_pws(request, response, get);
         return API_RETURN_SUCCEED;
       }if (action.equals("updatebadwords")) {
         if (!API_UPDATEBADWORDS) {
           return API_RETURN_FORBIDDEN;
         }
         return API_RETURN_SUCCEED;
       }
       if (action.equals("updatehosts")) {
         if (!API_UPDATEHOSTS)
           return API_RETURN_FORBIDDEN;
         return API_RETURN_SUCCEED;
       }if (action.equals("updateapps")) {
         if (!API_UPDATEAPPS)
           return API_RETURN_FORBIDDEN;
         return API_RETURN_SUCCEED;
       }if (action.equals("updatecredit"))
       {
         return API_RETURN_SUCCEED;
       }if (action.equals("getcreditsettings"))
       {
         return "";
       }if (action.equals("updatecreditsettings")) {
         if (!API_UPDATECREDITSETTINGS) {
           return API_RETURN_FORBIDDEN;
         }
         return API_RETURN_SUCCEED;
       }
       return API_RETURN_FORBIDDEN;
     }
     return "";
   }
 
   private void parse_str(String str, Map<String, String> sets) {
     if ((str == null) || (str.length() < 1))
       return;
     String[] ps = str.split("&");
     for (int i = 0; i < ps.length; i++) {
       String[] items = ps[i].split("=");
       if (items.length == 2)
         sets.put(items[0], items[1]);
       else if (items.length == 1)
         sets.put(items[0], "");
     }
   }
 
   protected long time()
   {
     return System.currentTimeMillis() / 1000L;
   }
 
   private static long tolong(Object s) {
     if (s != null) {
       String ss = s.toString().trim();
       if (ss.length() == 0) {
         return 0L;
       }
       return Long.parseLong(ss);
     }
 
     return 0L;
   }
 
   protected void shopping_login(HttpServletRequest request, HttpServletResponse response, Map<String, String> args)
   {
     boolean admin_login = CommUtil.null2Boolean(request.getSession(false)
       .getAttribute("admin_login"));
     if (!admin_login) {
       String userName = (String)args.get("username");
       String password = "";
       User user = this.userService.getObjByProperty("userName", userName);
       if (user != null) {
         password = user.getPassword();
       } else {
         user = new User();
         user.setUserName(userName);
         user.setUserRole("BUYER");
         user.setAddTime(new Date());
 
         user.setPassword(Md5Encrypt.md5(password).toLowerCase());
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
           log.setContent("用户注册增加" + 
             this.configService.getSysConfig()
             .getMemberRegister() + "分");
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
       }
       String url = CommUtil.getURL(request) + 
         "/shopping_login.htm?username=" + 
         CommUtil.encode(userName) + "&password=" + 
         "shopping_thid_login_" + password + "&encode=true";
       try {
         response.sendRedirect(url);
       }
       catch (IOException e) {
         e.printStackTrace();
       }
     }
   }
 
   protected void shopping_logout(HttpServletRequest request, HttpServletResponse response, Map<String, String> args)
   {
     String url = CommUtil.getURL(request) + "/shopping_logout.htm";
     try {
       response.sendRedirect(url);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   protected void shopping_update_pws(HttpServletRequest request, HttpServletResponse response, Map<String, String> args)
   {
     User user = SecurityUserHolder.getCurrentUser();
   }
 }


 
 
 