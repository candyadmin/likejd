 package com.shopping.uc.api;
 
 import com.shopping.core.tools.CommUtil;
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.OutputStream;
 import java.net.MalformedURLException;
 import java.net.Socket;
 import java.net.URL;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Properties;
 
 public class UCClient extends PHPFunctions
 {
   public static boolean IN_UC = true;
   public static String UC_IP = "127.0.0.1";
   public static String UC_API = "http://localhost/bbs/uc_server";
   public static String UC_CONNECT = "";
   public static String UC_KEY = "123456";
   public static String UC_APPID = "2";
   public static String UC_CLIENT_VERSION = "1.0";
   public static String UC_CLIENT_RELEASE = "20090212";
   public static String UC_ROOT = "";
   public static String UC_DATADIR = UC_ROOT + "./data/";
   public static String UC_DATAURL = "UC_API/data";
   public static String UC_API_FUNC = UC_CONNECT.equals("mysql") ? "uc_api_mysql" : 
     "uc_api_post";
 
   public static String[] uc_controls = new String[0];
 
   static {
     InputStream in = UCClient.class.getClassLoader().getResourceAsStream(
       "config.properties");
     Properties properties = new Properties();
     try {
       properties.load(in);
       UC_API = properties.getProperty("UC_API");
       UC_IP = properties.getProperty("UC_IP");
       UC_KEY = properties.getProperty("UC_KEY");
       UC_APPID = properties.getProperty("UC_APPID");
       UC_CONNECT = properties.getProperty("UC_CONNECT");
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
 
   protected String uc_serialize(String arr, int htmlon)
   {
     return arr;
   }
 
   protected String uc_unserialize(String s)
   {
     return s;
   }
 
   protected String uc_addslashes(String string, int force, boolean strip)
   {
     return string;
   }
 
   protected String daddslashes(String string, int force) {
     return uc_addslashes(string, force, false);
   }
 
   protected String uc_stripslashes(String string)
   {
     return string;
   }
 
   public String uc_api_post(String module, String action, Map<String, Object> arg)
   {
     StringBuffer s = new StringBuffer();
     String sep = "";
 
     for (String k : arg.keySet())
     {
       Object v = arg.get(k);
       k = urlencode(k);
 
       if (v.getClass().isAssignableFrom(Map.class)) {
         String s2 = "";
         String sep2 = "";
 
         for (Object k2 : ((Map)v).keySet()) {
           Object v2 = ((Map)v).get(k2);
           k2 = urlencode(k2.toString());
           s2 = s2 + sep2 + "{" + k + "}[" + k2 + "]=" + 
             urlencode(uc_stripslashes(String.valueOf(v2)));
           sep2 = "&";
         }
         s.append(sep).append(s2);
       } else {
         s.append(sep).append(k).append("=").append(
           urlencode(uc_stripslashes(String.valueOf(v))));
       }
       sep = "&";
     }
     String postdata = uc_api_requestdata(module, action, s.toString(), "");
     return uc_fopen2(UC_API + "/index.php", 500000, postdata, "", true, 
       UC_IP, 20, true);
   }
 
   protected String uc_api_requestdata(String module, String action, String arg, String extra)
   {
     String input = uc_api_input(arg);
     String post = "m=" + module + "&a=" + action + "&inajax=2&release=" + 
       UC_CLIENT_RELEASE + "&input=" + input + "&appid=" + UC_APPID + 
       extra;
     return post;
   }
 
   protected String uc_api_url(String module, String action, String arg, String extra)
   {
     String url = UC_API + "/index.php?" + 
       uc_api_requestdata(module, action, arg, extra);
     return url;
   }
 
   public String uc_api_input(String data)
   {
     String s = urlencode(uc_authcode(data + "&agent=" + md5("") + "&time=" + 
       time(), "ENCODE", UC_KEY));
     return s;
   }
 
   public String uc_api_mysql(String model, String action, Map args)
   {
     if (action.charAt(0) != '_') {
       return null;
     }
     return "";
   }
 
   public String uc_authcode(String string, String operation)
   {
     return uc_authcode(string, operation, null);
   }
 
   public String uc_authcode(String string, String operation, String key) {
     return uc_authcode(string, operation, key, 0);
   }
 
   public String uc_authcode(String string, String operation, String key, int expiry)
   {
     int ckey_length = 4;
 
     key = md5(key != null ? key : UC_KEY);
     String keya = md5(substr(key, 0, 16));
     String keyb = md5(substr(key, 16, 16));
     String keyc = ckey_length > 0 ? 
       substr(md5(microtime()), -ckey_length) : operation.equals("DECODE") ? substr(
       string, 0, ckey_length) : 
       "";
 
     String cryptkey = keya + md5(new StringBuilder(String.valueOf(keya)).append(keyc).toString());
     int key_length = cryptkey.length();
 
     string = 
       sprintf("%010d", expiry > 0 ? expiry + time() : 
       0L) + 
       substr(md5(new StringBuilder(String.valueOf(string)).append(keyb).toString()), 0, 16) + string;
     int string_length = string.length();
 
     StringBuffer result1 = new StringBuffer();
 
     int[] box = new int[256];
     for (int i = 0; i < 256; i++) {
       box[i] = i;
     }
 
     int[] rndkey = new int[256];
     for (int i = 0; i <= 255; i++) {
       rndkey[i] = cryptkey.charAt(i % key_length);
     }
 
     int j = 0;
     for (int i = 0; i < 256; i++) {
       j = (j + box[i] + rndkey[i]) % 256;
       int tmp = box[i];
       box[i] = box[j];
       box[j] = tmp;
     }
 
     j = 0;
     int a = 0;
     for (int i = 0; i < string_length; i++) {
       a = (a + 1) % 256;
       j = (j + box[a]) % 256;
       int tmp = box[a];
       box[a] = box[j];
       box[j] = tmp;
 
       result1
         .append((char)(string.charAt(i) ^ box[((box[a] + box[j]) % 256)]));
     }
 
     if (operation.equals("DECODE")) {
       String result = result1.substring(0, result1.length());
       if ((CommUtil.null2Int(substr(result.toString(), 0, 10)) == 0) || 
         (CommUtil.null2Long(substr(result.toString(), 0, 10)).longValue() - 
         time() > 0L))
       {
         if (substr(result.toString(), 10, 16).equals(
           substr(md5(substr(result.toString(), 26) + keyb), 
           0, 16)))
           return substr(result.toString(), 26);
       }
       return "";
     }
 
     return keyc + base64_encode(result1.toString()).replaceAll("=", "");
   }
 
   protected String uc_fopen2(String url, int limit, String post, String cookie, boolean bysocket, String ip, int timeout, boolean block)
   {
     url = url + (url.indexOf("?") > 0 ? "&" : "?__times__=1");
     return uc_fopen(url, limit, post, cookie, bysocket, ip, timeout, block);
   }
 
   protected String uc_fopen(String url, int limit, String post, String cookie, boolean bysocket, String ip, int timeout, boolean block)
   {
     String ret = "";
 
     String host = "";
     String path = "";
     int port = 80;
     try {
       URL matches = new URL(url);
       host = matches.getHost();
       path = matches.getPath() != null ? matches.getPath() + (
         matches.getQuery() != null ? "?" + matches.getQuery() : 
         "") : 
         "/";
       if (matches.getPort() > 0)
         port = matches.getPort();
     }
     catch (MalformedURLException localMalformedURLException) {
     }
     StringBuffer out = new StringBuffer();
     if ((post != null) && (post.length() > 0)) {
       out.append("POST ").append(path).append(" HTTP/1.0\r\n");
       out.append("Accept: *\r\n");
       out.append("Accept-Language: zh-cn\r\n");
       out.append("Content-Type: application/x-www-form-urlencoded\r\n");
       out.append("User-Agent: \r\n");
       out.append("Host: ").append(host).append("\r\n");
       out.append("Content-Length: ").append(post.length()).append("\r\n");
       out.append("Connection: Close\r\n");
       out.append("Cache-Control: no-cache\r\n");
       out.append("Cookie: \r\n\r\n");
       out.append(post);
     } else {
       out.append("GET path HTTP/1.0\r\n");
       out.append("Accept: *\r\n");
 
       out.append("Accept-Language: zh-cn\r\n");
       out.append("User-Agent: Java/1.5.0_01\r\n");
       out.append("Host: host\r\n");
       out.append("Connection: Close\r\n");
       out.append("Cookie: cookie\r\n\r\n");
     }
     try
     {
       Socket fp = new Socket((ip != null) && (ip.length() > 10) ? ip : host, 
         port);
       if (!fp.isConnected()) {
         return ""; } 
 OutputStream os = fp.getOutputStream();
       os.write(out.toString().getBytes());
 
       InputStream ins = fp.getInputStream();
 
       BufferedReader reader = new BufferedReader(
         new InputStreamReader(ins));
       String header;
       do header = reader.readLine();
       while ((header != null) && (!header.equals("")) && (header != "\r\n") && 
         (header != "\n"));
       while (true)
       {
         String data = reader.readLine();
         if ((data == null) || (data.equals(""))) {
           break;
         }
         ret = ret + data;
       }
 
       fp.close();
     }
     catch (IOException localIOException)
     {
     }
     return ret;
   }
 
   public String uc_app_ls() {
     String ret = call_user_func(UC_API_FUNC, "app", "ls", null);
     return UC_CONNECT.equals("mysql") ? ret : uc_unserialize(ret);
   }
 
   public String uc_user_register(String username, String password, String email)
   {
     return uc_user_register(username, password, email, "", "");
   }
 
   public String uc_user_register(String username, String password, String email, String questionid, String answer)
   {
     Map args = new HashMap();
     args.put("username", username);
     args.put("password", password);
     args.put("email", email);
     args.put("questionid", questionid);
     args.put("answer", answer);
     return call_user_func(UC_API_FUNC, "user", "register", args);
   }
 
   public String uc_user_login(String username, String password)
   {
     return uc_user_login(username, password, 0, 0);
   }
 
   public String uc_user_login(String username, String password, int isuid, int checkques)
   {
     return uc_user_login(username, password, isuid, checkques, 0, "");
   }
 
   public String uc_user_login(String username, String password, int isuid, int checkques, int questionid, String answer)
   {
     Map args = new HashMap();
     args.put("username", username);
     args.put("password", password);
     args.put("isuid", Integer.valueOf(isuid));
     args.put("checkques", Integer.valueOf(checkques));
     args.put("questionid", Integer.valueOf(questionid));
     args.put("answer", answer);
     String ret = call_user_func(UC_API_FUNC, "user", "login", args);
     return UC_CONNECT.equals("mysql") ? ret : uc_unserialize(ret);
   }
 
   public String uc_user_synlogin(int uid)
   {
     Map args = new HashMap();
     args.put("uid", Integer.valueOf(uid));
     String ret = uc_api_post("user", "synlogin", args);
     return ret;
   }
 
   public String uc_user_synlogout()
   {
     String ret = uc_api_post("user", "synlogout", 
       new HashMap());
     return ret;
   }
 
   public String uc_get_user(String username, int isuid)
   {
     Map args = new HashMap();
     args.put("username", username);
     args.put("isuid", Integer.valueOf(isuid));
     String ret = call_user_func(UC_API_FUNC, "user", "get_user", args);
     return UC_CONNECT.equals("mysql") ? ret : uc_unserialize(ret);
   }
 
   public String uc_user_edit(String username, String oldpw, String newpw, String email, int ignoreoldpw, int questionid, int answer)
   {
     Map args = new HashMap();
     args.put("username", username);
     args.put("oldpw", oldpw);
     args.put("newpw", newpw);
     args.put("email", email);
     args.put("ignoreoldpw", Integer.valueOf(ignoreoldpw));
     args.put("questionid", Integer.valueOf(questionid));
     args.put("answer", Integer.valueOf(answer));
     return call_user_func(UC_API_FUNC, "user", "edit", args);
   }
 
   public String uc_user_delete(String uid)
   {
     Map args = new HashMap();
     args.put("uid", uid);
     return call_user_func(UC_API_FUNC, "user", "delete", args);
   }
 
   public String uc_user_deleteavatar(String uid)
   {
     Map args = new HashMap();
     args.put("uid", uid);
     return uc_api_post("user", "deleteavatar", args);
   }
 }


 
 
 