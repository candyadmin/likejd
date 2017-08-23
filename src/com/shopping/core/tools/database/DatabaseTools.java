 package com.shopping.core.tools.database;
 
 import com.shopping.core.tools.CommUtil;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.OutputStreamWriter;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.ResultSet;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.Vector;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpSession;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Repository;
 
 @Repository
 public class DatabaseTools
   implements IBackup
 {
 
   @Autowired
   private PublicMethod publicMethod;
 
   public boolean createSqlScript(HttpServletRequest request, String path, String name, String size, String tables)
     throws Exception
   {
     int count = 1;
     boolean ret = true;
     float psize = CommUtil.null2Float(size);
     List tablelists = this.publicMethod.getAllTableName("show tables");
     List<String> backup_list = new ArrayList<String>();
     if ((tables != null) && (!tables.equals("")))
       backup_list = Arrays.asList(tables.split(","));
     else {
       backup_list = tablelists;
     }
     try
     {
       File file = new File(path + File.separator + name + "_" + count + 
         ".sql");
 
       PrintWriter pwrite = new PrintWriter(
         new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"), true);
 
       pwrite.println(AppendMessage.headerMessage());
       pwrite.println("SET FOREIGN_KEY_CHECKS=0;\n");
       for (String table : backup_list) {
         StringBuilder strBuilder = new StringBuilder();
         strBuilder.append("show create table ").append(table);
         List<String>  list = this.publicMethod.getAllColumns(strBuilder
           .toString());
         for (String line : list)
         {
           double fsize = CommUtil.div(Long.valueOf(file.length()), Integer.valueOf(1024));
           if (fsize > psize) {
             pwrite.flush();
 
             count++;
             file = new File(path + File.separator + name + "_" + 
               count + ".sql");
             pwrite = new PrintWriter(
               new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"), 
               true);
 
             pwrite.println(AppendMessage.headerMessage());
           }
           request.getSession(false).setAttribute("db_mode", "backup");
           request.getSession(false).setAttribute("db_bound", Integer.valueOf(count));
           request.getSession(false).setAttribute("db_error", Integer.valueOf(0));
           request.getSession(false).setAttribute("db_result", Integer.valueOf(0));
 
           pwrite.println(AppendMessage.tableHeaderMessage(table));
 
           pwrite.println("DROP TABLE IF EXISTS  `" + table + 
             "`;");
           pwrite.println(line + ";" + "\n");
         }
       }
       pwrite.flush();
       pwrite.close();
     } catch (Exception e) {
       ret = false;
       e.printStackTrace();
       throw new Exception("出现错误,创建备份文件失败!");
     }
 
     count++;
     try {
       File file = new File(path + File.separator + name + "_" + count + 
         ".sql");
 
       PrintWriter pwrite = new PrintWriter(
         new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"), true);
 
       pwrite.println(AppendMessage.headerMessage());
       pwrite.println(AppendMessage.insertHeaderMessage());
       for (String table : backup_list) {
         if (CommUtil.null2String(table).equals(""))
           continue;
         List insertList = getAllDatas(table.toString());
         for (int i = 0; i < insertList.size(); i++) {
           double fsize = CommUtil.div(Long.valueOf(file.length()), Integer.valueOf(1024));
           if (fsize > psize) {
             pwrite.flush();
 
             count++;
             file = new File(path + File.separator + name + "_" + 
               count + ".sql");
             pwrite = new PrintWriter(
               new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"), 
               true);
 
             pwrite.println(AppendMessage.headerMessage());
           }
           request.getSession(false).setAttribute("db_mode", 
             "backup");
           request.getSession(false).setAttribute("db_bound", 
             Integer.valueOf(count));
           request.getSession(false).setAttribute("db_error", Integer.valueOf(0));
           request.getSession(false).setAttribute("db_result", Integer.valueOf(0));
           pwrite.flush();
           pwrite.println((String)insertList.get(i));
         }
       }
 
       pwrite.flush();
       pwrite.close();
 
       request.getSession(false).setAttribute("db_result", Integer.valueOf(1));
     } catch (Exception e) {
       ret = false;
       e.printStackTrace();
       throw new Exception("出现错误,创建备份文件失败!");
     }
     return ret;
   }
 
   public boolean executSqlScript(String filePath)
     throws Exception
   {
     Connection conn = null;
     Statement stmt = null;
     List<String> sqlStrList = null;
     boolean ret = true;
     try {
       sqlStrList = this.publicMethod.loadSqlScript(filePath);
       conn = this.publicMethod.getConnection();
       stmt = conn.createStatement();
 
       conn.setAutoCommit(false);
       for (String sqlStr : sqlStrList) {
         int index = sqlStr.indexOf("INSERT");
         if (-1 == index) {
           stmt.addBatch(sqlStr);
         }
 
       }
 
       stmt.executeBatch();
 
       for (String sqlStr : sqlStrList) {
         int index = sqlStr.indexOf("INSERT");
         if (-1 != index) {
           System.out.println(sqlStr);
           int i = stmt.executeUpdate(sqlStr);
         }
       }
 
       stmt.executeBatch();
       conn.commit();
     } catch (Exception ex) {
       ret = false;
       System.out.println(ex.getMessage());
       ex.printStackTrace();
       conn.rollback();
       ex.printStackTrace();
     }
     return ret;
   }
 
   public List<String> getAllDatas(String tableName)
     throws Exception
   {
     List list = new ArrayList();
     StringBuilder typeStr = null;
 
     ResultSet rs = null;
     try
     {
       typeStr = new StringBuilder();
       StringBuilder sqlStr = new StringBuilder();
       StringBuilder columnsStr = new StringBuilder().append("describe ").append(
         tableName);
       List<TableColumn> columnList = this.publicMethod.getDescribe(columnsStr.toString());
       sqlStr.append("SELECT ");
       for (TableColumn bColumn : columnList)
       {
         String columnsType = bColumn.getColumnsType();
         if (("longblob".equals(columnsType)) || 
           ("blob".equals(columnsType)) || 
           ("tinyblob".equals(columnsType)) || 
           ("mediumblob".equals(columnsType)))
           typeStr.append("hex(`" + bColumn.getColumnsFiled() + 
             "`" + ") as " + "`" + bColumn.getColumnsFiled() + 
             "`" + " ,");
         else {
           typeStr
             .append("`" + bColumn.getColumnsFiled() + "`" + 
             " ,");
         }
       }
       sqlStr.append(typeStr.substring(0, typeStr.length() - 1));
       sqlStr.append(" FROM ").append("`" + tableName + "`;");
       rs = this.publicMethod.queryResult(sqlStr.toString());
       while (rs.next())
       {
         StringBuffer insert_sql = new StringBuffer();
         insert_sql.append("INSERT INTO " + tableName + " (" + 
           typeStr.substring(0, typeStr.length() - 1) + 
           ") VALUES (");
         Vector vector = new Vector();
         for (TableColumn dbColumn : columnList) {
           String columnsType = dbColumn.getColumnsType();
           String columnsFile = dbColumn.getColumnsFiled();
           if (rs.getString(columnsFile) == null) {
             vector.add(rs.getString(columnsFile));
           }
           else if ("bit".equals(columnsType.substring(0, 3))) {
             vector.add(
               Integer.valueOf(Integer.valueOf(rs.getString(columnsFile))
               .intValue()));
           } else if (("bit".equals(columnsType.substring(0, 3))) && 
             (Integer.valueOf(rs.getString(columnsFile))
             .intValue() == 0)) {
             vector.add("''");
           } else if (("longblob".equals(columnsType)) || 
             ("blob".equals(columnsType)) || 
             ("tinyblob".equals(columnsType)) || 
             ("mediumblob".equals(columnsType))) {
             vector.add("0x" + rs.getString(columnsFile));
           }
           else if (("text".equals(columnsType)) || 
             ("longtext".equals(columnsType)) || 
             ("tinytext".equals(columnsType)) || 
             ("mediumtext".equals(columnsType))) {
             String tempStr = rs.getString(columnsFile);
             tempStr = tempStr.replaceAll("'", "\\'");
             tempStr = tempStr.replaceAll("\"", "\\\"").replaceAll(
               "\r", "\\\\r").replaceAll("\n", "\\\\n")
               .replaceAll("<!--[\\w\\W\\r\\n]*?-->", "");
             vector.add("'" + tempStr + "'");
           } else {
             vector.add("'" + rs.getString(columnsFile) + "'");
           }
         }
         String tempStr = vector.toString();
         tempStr = tempStr.substring(1, tempStr.length() - 1) + ");";
         insert_sql.append(tempStr);
         list.add(insert_sql.toString());
       }
     } catch (Exception e) {
       throw e;
     }
     StringBuilder columnsStr;
     StringBuilder sqlStr;
     List columnList;
     return list;
   }
 
   public List<Vector<Object>> getAllDatas1(String tableName)
     throws Exception
   {
     StringBuilder typeStr = null;
 		List list = new ArrayList();
     ResultSet rs = null;
     try
     {
       typeStr = new StringBuilder();
       StringBuilder sqlStr = new StringBuilder();
       StringBuilder columnsStr = new StringBuilder().append("describe ").append(
         tableName);
       List<TableColumn> columnList = this.publicMethod.getDescribe(columnsStr.toString());
       sqlStr.append("SELECT ");
       for (TableColumn bColumn : columnList)
       {
         String columnsType = bColumn.getColumnsType();
         if (("longblob".equals(columnsType)) || 
           ("blob".equals(columnsType)) || 
           ("tinyblob".equals(columnsType)) || 
           ("mediumblob".equals(columnsType)))
           typeStr.append("hex(`" + bColumn.getColumnsFiled() + 
             "`" + ") as " + "`" + bColumn.getColumnsFiled() + 
             "`" + " ,");
         else {
           typeStr
             .append("`" + bColumn.getColumnsFiled() + "`" + 
             " ,");
         }
       }
       sqlStr.append(typeStr.substring(0, typeStr.length() - 1));
       sqlStr.append(" FROM ").append("`" + tableName + "`;");
 
       
       rs = this.publicMethod.queryResult(sqlStr.toString());
       while (rs.next()) {
         Vector vector = new Vector();
         for (TableColumn dbColumn : columnList) {
           String columnsType = dbColumn.getColumnsType();
           String columnsFile = dbColumn.getColumnsFiled();
           if (rs.getString(columnsFile) == null) {
             vector.add(rs.getString(columnsFile));
           }
           else if ("bit".equals(columnsType.substring(0, 3))) {
             vector.add(
               Integer.valueOf(Integer.valueOf(rs.getString(columnsFile))
               .intValue()));
           } else if (("bit".equals(columnsType.substring(0, 3))) && 
             (Integer.valueOf(rs.getString(columnsFile))
             .intValue() == 0)) {
             vector.add("''");
           } else if (("longblob".equals(columnsType)) || 
             ("blob".equals(columnsType)) || 
             ("tinyblob".equals(columnsType)) || 
             ("mediumblob".equals(columnsType))) {
             vector.add("0x" + rs.getString(columnsFile));
           }
           else if (("text".equals(columnsType)) || 
             ("longtext".equals(columnsType)) || 
             ("tinytext".equals(columnsType)) || 
             ("mediumtext".equals(columnsType))) {
             String tempStr = rs.getString(columnsFile);
             tempStr = tempStr.replace("'", "\\'");
             tempStr = tempStr.replace("\"", "\\\"");
             vector.add("'" + tempStr + "'");
           } else {
             vector.add("'" + rs.getString(columnsFile) + "'");
           }
         }
         list.add(vector);
       }
     } catch (Exception e) {
       throw e;
     }
//     List list;
     return list;
   }
 
   public List<String> getTables() throws Exception {
     List tables = new ArrayList();
     Connection conn = null;
     try {
       conn = this.publicMethod.getConnection();
       ResultSet rs = conn.getMetaData().getTables("", "", "", null);
       while (rs.next())
         tables.add(rs.getString("TABLE_NAME"));
     }
     catch (Exception e) {
       e.printStackTrace();
     } finally {
       this.publicMethod.closeConn();
     }
     return tables;
   }
 
   public String queryDatabaseVersion()
   {
     Connection conn = null;
     String version = "未知版本号";
     try {
       conn = this.publicMethod.getConnection();
       DatabaseMetaData md = conn.getMetaData();
       String str1 = md.getDatabaseProductName() + " " + 
         md.getDatabaseProductVersion().toUpperCase();
       return str1;
     }
     catch (Exception e) {
       e.printStackTrace();
     }
     finally {
       this.publicMethod.closeConn();
     }
     return version;
   }
 
   public boolean execute(String sql)
   {
     Connection conn = null;
     boolean ret = true;
     try {
       conn = this.publicMethod.getConnection();
       Statement stmt = conn.createStatement();
       stmt.execute(sql);
     } catch (Exception e) {
       ret = false;
       e.printStackTrace();
     } finally {
       this.publicMethod.closeConn();
     }
     return ret;
   }
 
   public boolean export(String tables, String path)
   {
     boolean ret = true;
     try {
       File file = new File(path);
 
       PrintWriter pwrite = new PrintWriter(
         new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"), true);
 
       pwrite.println(AppendMessage.headerMessage());
       pwrite.println(AppendMessage.insertHeaderMessage());
       List<String> list = Arrays.asList(tables.split(","));
       for (String table : list)
       {
         List insertList = getAllDatas(table.toString());
         for (int i = 0; i < insertList.size(); i++) {
           pwrite.flush();
           pwrite.println((String)insertList.get(i));
         }
       }
       pwrite.flush();
       pwrite.close();
     } catch (Exception e) {
       ret = false;
       e.printStackTrace();
     }
     return ret;
   }
 
   public ResultSet query(String sql)
   {
     Connection conn = null;
     ResultSet rs = null;
     boolean ret = true;
     try {
       conn = this.publicMethod.getConnection();
       Statement stmt = conn.createStatement();
       rs = stmt.executeQuery(sql);
     } catch (Exception e) {
       ret = false;
       e.printStackTrace();
     } finally {
       this.publicMethod.closeConn();
     }
     return rs;
   }
 }

