 package com.shopping.uc.api;
 
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.service.ISysConfigService;
 import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.SQLException;
 import java.sql.Statement;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 
 @Component
 public class UCTools
 {
 
   @Autowired
   private ISysConfigService configService;
   public static final ThreadLocal<Connection> thread = new ThreadLocal();
 
   public boolean active_user(String userName, String pws, String email)
   {
     boolean ret = true;
     Connection conn = getConnection();
     try
     {
       Statement stmt = conn.createStatement();
       String sql = "insert into " + 
         this.configService.getSysConfig().getUc_table_preffix() + 
         "common_member (`email`,`username`,`password`,`regdate`,`groupid`) value ('" + 
         email + "','" + userName + "','" + pws + "','" + 
         System.currentTimeMillis() / 1000L + "','10')";
       ret = stmt.execute(sql);
     }
     catch (SQLException e) {
       e.printStackTrace();
     } finally {
       closeAll();
     }
     return ret;
   }
 
   public Connection getConnection()
   {
     Connection conn = (Connection)thread.get();
     if (conn == null) {
       SysConfig config = this.configService.getSysConfig();
       String UC_DATABASE = config.getUc_database();
       String UC_TABLE_PREFFIX = config.getUc_table_preffix();
       String UC_DATABASE_URL = config.getUc_database_url();
       String UC_DATABASE_PORT = config.getUc_database_port();
       String UC_DATABASE_USERNAME = config.getUc_database_username();
       String UC_DATABASE_PWS = config.getUc_database_pws();
       try {
         Class.forName("com.mysql.jdbc.Driver");
         conn = DriverManager.getConnection("jdbc:mysql://" + 
           UC_DATABASE_URL + ":" + UC_DATABASE_PORT + "/" + 
           UC_DATABASE, UC_DATABASE_USERNAME, UC_DATABASE_PWS);
       }
       catch (ClassNotFoundException e) {
         e.printStackTrace();
       }
       catch (SQLException e) {
         e.printStackTrace();
       }
       thread.set(conn);
     }
     return conn;
   }
 
   public void closeAll()
   {
     try
     {
       Connection conn = (Connection)thread.get();
       if (conn != null) {
         conn.close();
         thread.set(null);
       }
     } catch (Exception e) {
       try {
         throw e;
       }
       catch (Exception e1) {
         e1.printStackTrace();
       }
     }
   }
 
   public static void main(String[] args) {
     UCTools tools = new UCTools();
     tools.active_user("test", "122", "333@test.com");
   }
 }


 
 
 