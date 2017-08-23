 package com.shopping.uc.api;
 
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.util.LinkedList;
 
 public class Test
 {
   public static void main(String[] args)
     throws UnsupportedEncodingException
   {
     test_get_user("cccc");
   }
 
   public static void testLogin()
     throws UnsupportedEncodingException
   {
     UCClient e = new UCClient();
     String result = e.uc_user_login("木子", "123456");
     System.out.println(result);
     LinkedList rs = XMLHelper.uc_unserialize(result);
     if (rs.size() > 0) {
       int $uid = Integer.parseInt((String)rs.get(0));
       String $username = (String)rs.get(1);
       String $password = (String)rs.get(2);
       String $email = (String)rs.get(3);
       if ($uid > 0) {
         System.out.println("登录成功");
         System.out.println($username);
         System.out.println($password);
         System.out.println($email);
         String $ucsynlogin = e.uc_user_synlogin($uid);
         System.out.println("登录成功" + $ucsynlogin);
       }
       else if ($uid == -1) {
         System.out.println("用户不存在,或者被删除");
       } else if ($uid == -2) {
         System.out.println("密码错");
       } else {
         System.out.println("未定义");
       }
     } else {
       System.out.println("Login failed");
       System.out.println(result);
     }
   }
 
   public static void testLogout() {
     UCClient uc = new UCClient();
 
     String $ucsynlogout = uc.uc_user_synlogout();
     System.out.println("退出成功" + $ucsynlogout);
   }
 
   public static void test_update_pws() {
   }
 
   public static void testRegister() {
     UCClient uc = new UCClient();
 
     String $returns = uc.uc_user_register("cccc", "ccccc", "ccc@abc.com");
     int $uid = Integer.parseInt($returns);
     if ($uid <= 0) {
       if ($uid == -1)
         System.out.print("用户名不合法");
       else if ($uid == -2)
         System.out.print("包含要允许注册的词语");
       else if ($uid == -3)
         System.out.print("用户名已经存在");
       else if ($uid == -4)
         System.out.print("Email 格式有误");
       else if ($uid == -5)
         System.out.print("Email 不允许注册");
       else if ($uid == -6)
         System.out.print("该 Email 已经被注册");
       else
         System.out.print("未定义");
     }
     else
       System.out.println("OK:" + $returns);
   }
 
   private static String test_get_user(String username)
   {
     UCClient uc = new UCClient();
     String ret = uc.uc_get_user(username, 0);
     System.out.println(ret);
     return ret;
   }
 }


 
 
 