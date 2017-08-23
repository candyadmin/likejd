 package com.shopping.core.tools.database;
 
 import com.shopping.core.tools.UnicodeReader;
 import java.io.FileInputStream;
 import java.sql.Connection;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Repository;
 
 @Repository
 public class PublicMethod
 {
 
   @Autowired
   private DbConnection dbConnectoin;
   private static Connection conn = null;
   private static Statement stmt = null;
   private static ResultSet rs = null;
 
   public Connection getConnection()
     throws Exception
   {
     try
     {
       conn = this.dbConnectoin.getConnection();
     } catch (Exception e) {
       throw new Exception("数据链接错误,请检查用户输入的信息!");
     }
     return conn;
   }
 
   public void closeConn()
   {
     this.dbConnectoin.closeAll();
   }
 
   public ResultSet queryResult(String sqlStr)
     throws Exception
   {
     try
     {
       conn = getConnection();
       stmt = conn.createStatement();
       rs = stmt.executeQuery(sqlStr);
     } catch (Exception e) {
       e.printStackTrace();
     }
     return rs;
   }
 
   public List<String> getAllTableName(String sqlStr)
     throws Exception
   {
     List list = null;
     try {
       list = new ArrayList();
       rs = queryResult(sqlStr);
       while (rs.next())
         list.add(rs.getString(1));
     }
     catch (Exception e) {
       throw e;
     } finally {
       this.dbConnectoin.closeAll();
     }
     return list;
   }
 
   public List<String> getAllColumns(String sqlStr)
     throws Exception
   {
     List list = null;
     try {
       list = new ArrayList();
       rs = queryResult(sqlStr);
       while (rs.next())
         list.add(rs.getString(2));
     }
     catch (Exception e) {
       e.printStackTrace();
     } finally {
       this.dbConnectoin.closeAll();
     }
     return list;
   }
 
   public List<TableColumn> getDescribe(String sqlStr)
     throws Exception
   {
     List list = null;
     try {
       list = new ArrayList();
       rs = queryResult(sqlStr);
       while (rs.next()) {
         TableColumn dbColumns = new TableColumn();
         dbColumns.setColumnsFiled(rs.getString(1));
         dbColumns.setColumnsType(rs.getString(2));
         dbColumns.setColumnsNull(rs.getString(3));
         dbColumns.setColumnsKey(rs.getString(4));
         dbColumns.setColumnsDefault(rs.getString(5));
         dbColumns.setColumnsExtra(rs.getString(6));
         list.add(dbColumns);
       }
     } catch (Exception e) {
       e.printStackTrace();
     } finally {
       this.dbConnectoin.closeAll();
     }
     return list;
   }
 
   public List<String> loadSqlScript(String filePath)
     throws Exception
   {
     List sqlList = null;
     UnicodeReader inReader = null;
     StringBuffer sqlStr = null;
     try {
       sqlList = new ArrayList();
       sqlStr = new StringBuffer();
 
       inReader = new UnicodeReader(new FileInputStream(filePath), "UTF-8");
       char[] buff = new char[1024];
       int byteRead = 0;
       while ((byteRead = inReader.read(buff)) != -1) {
         sqlStr.append(new String(buff, 0, byteRead));
       }
 
       String[] sqlStrArr = sqlStr.toString().split(
         "(;\\s*\\r\\n)|(;\\s*\\n)");
       for (String str : sqlStrArr) {
         String sql = str.replaceAll("--.*", "").trim();
         if (!sql.equals(""))
           sqlList.add(sql);
       }
     }
     catch (Exception e) {
       e.printStackTrace();
     }
     return sqlList;
   }
 
   public String trim(String obj)
     throws Exception
   {
     Matcher matcher = null;
     Pattern pattern = null;
     try {
       pattern = Pattern.compile("\\s*\n");
       matcher = pattern.matcher(obj.toString());
     } catch (Exception e) {
       throw e;
     }
     return matcher.replaceAll("");
   }
 
   public String genericQueryField(String table) {
     String query_sql = "";
     try {
       conn = getConnection();
       stmt = conn.createStatement();
 
       String sql = "select * from " + table;
       ResultSetMetaData rsmds = stmt.executeQuery(sql).getMetaData();
       for (int j = 1; j < rsmds.getColumnCount(); j++) {
         query_sql = query_sql + rsmds.getColumnName(j) + ",";
       }
       query_sql = query_sql + rsmds.getColumnName(rsmds.getColumnCount());
     } catch (Exception e) {
       e.printStackTrace();
     } finally {
       this.dbConnectoin.closeAll();
     }
     return query_sql;
   }
 }

