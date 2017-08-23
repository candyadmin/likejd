 package com.shopping.manage.admin.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.ShopData;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.FileSorter;
 import com.shopping.core.tools.database.DatabaseTools;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Date;
 import java.util.List;
 import javax.servlet.ServletContext;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import net.sf.ehcache.CacheManager;
 import org.json.simple.JSONObject;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class DatabaseManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private DatabaseTools databaseTools;
 
   @SecurityMapping(display = false, rsequence = 0, title="数据库备份", value="/admin/database_add.htm*", rtype="admin", rname="数据库管理", rcode="data_manage", rgroup="工具")
   @RequestMapping({"/admin/database_add.htm"})
   public ModelAndView database_add(HttpServletRequest request, HttpServletResponse response)
     throws Exception
   {
     ModelAndView mv = new JModelAndView("admin/blue/database_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String path = request.getSession().getServletContext().getRealPath("/") + 
       "data";
     int count = 1;
     File dir = new File(path);
     File[] files = dir.listFiles();
     if (files == null) {
       files = new File[0];
     }
     Arrays.sort(files, new FileSorter(1));
     if (files.length > 0) {
       for (File file : files) {
         if (file.getName().lastIndexOf("_") < 0) {
           continue;
         }
         if (file.getName().substring(0, 
           file.getName().lastIndexOf("_")).equals(
           CommUtil.formatTime("yyyyMMdd", new Date()))) {
           count = CommUtil.null2Int(file.getName().substring(
             file.getName().lastIndexOf("_") + 1)) + 1;
           break;
         }
       }
     }
     mv.addObject("tables", this.databaseTools.getTables());
     mv.addObject("name", CommUtil.formatTime("yyyyMMdd_" + count, 
       new Date()));
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="数据库备份保存", value="/admin/database_backup.htm*", rtype="admin", rname="数据库管理", rcode="data_manage", rgroup="工具")
   @RequestMapping({"/admin/database_backup.htm"})
   public ModelAndView database_backup(HttpServletRequest request, HttpServletResponse response, String type, String name, String tables, String preBoundSize) throws Exception {
     ModelAndView mv = new JModelAndView("admin/blue/db.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
       request, response);
     if (CommUtil.null2Float(preBoundSize) >= 20.0F) {
       if (type.equals("all")) {
         request.getSession(false).setAttribute("backup_type", type);
         request.getSession(false).setAttribute("backup_name", name);
         request.getSession(false).setAttribute("backup_size", 
           preBoundSize);
         mv.addObject("mode", "backup");
         mv.addObject("name", name);
       }
       else if (!tables.trim().equals("")) {
         request.getSession(false).setAttribute("backup_type", type);
         request.getSession(false).setAttribute("backup_tables", 
           tables);
         request.getSession(false).setAttribute("backup_name", name);
         request.getSession(false).setAttribute("backup_size", 
           preBoundSize);
         mv.addObject("name", name);
         mv.addObject("mode", "backup");
       } else {
         mv = new JModelAndView("admin/blue/error.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 0, request, 
           response);
         mv.addObject("op_title", "没有选择数据表，数据备份失败");
       }
     }
     else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "分卷小于20k,数据备份失败");
     }
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/database_add.htm");
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="数据库分卷备份", value="/admin/database_bund_backup.htm*", rtype="admin", rname="数据库管理", rcode="data_manage", rgroup="工具")
   @RequestMapping({"/admin/database_bund_backup.htm"})
   public void database_bund_backup(HttpServletRequest request, HttpServletResponse response) throws Exception {
     String name = CommUtil.null2String(request.getSession(false)
       .getAttribute("backup_name"));
     String preBoundSize = CommUtil.null2String(request.getSession(false)
       .getAttribute("backup_size"));
     String type = CommUtil.null2String(request.getSession(false)
       .getAttribute("backup_type"));
     String tables = CommUtil.null2String(request.getSession(false)
       .getAttribute("backup_tables"));
     String path = request.getSession().getServletContext().getRealPath("/") + 
       "data" + File.separator + name;
     if (type.equals("all")) {
       boolean ret = CommUtil.createFolder(path);
       if (ret)
         this.databaseTools.createSqlScript(request, path, name, 
           preBoundSize, "");
     }
     else {
       boolean ret = CommUtil.createFolder(path);
       if (ret)
         this.databaseTools.createSqlScript(request, path, name, 
           preBoundSize, tables);
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="数据库备份列表", value="/admin/database_list.htm*", rtype="admin", rname="数据库管理", rcode="data_manage", rgroup="工具")
   @RequestMapping({"/admin/database_list.htm"})
   public ModelAndView database_list(HttpServletRequest request, HttpServletResponse response, String type, String name, String preBoundSize)
   {
     ModelAndView mv = new JModelAndView("admin/blue/database_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     List objs = new ArrayList();
     String path = request.getSession().getServletContext().getRealPath("/") + 
       "data";
     File dir = new File(path);
     File[] files = dir.listFiles();
     if (files == null) {
       files = new File[0];
     }
     Arrays.sort(files, new FileSorter(1));
     for (File f : files) {
       if (f.isDirectory()) {
         ShopData obj = new ShopData();
         obj.setAddTime(new Date(f.lastModified()));
         obj.setSize(CommUtil.fileSize(f));
         obj.setName(f.getName());
         obj.setBoundSize(CommUtil.fileCount(f));
         objs.add(obj);
       }
     }
     mv.addObject("objs", objs);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="数据库备份删除", value="/admin/database_del.htm*", rtype="admin", rname="数据库管理", rcode="data_manage", rgroup="工具")
   @RequestMapping({"/admin/database_del.htm"})
   public ModelAndView database_del(HttpServletRequest request, HttpServletResponse response, String names) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String[] list = names.split(",");
     for (String name : list) {
       String path = request.getSession().getServletContext()
         .getRealPath("/") + 
         "data" + File.separator + name;
       CommUtil.deleteFolder(path);
     }
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/database_list.htm");
     mv.addObject("op_title", "删除备份数据成功");
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="数据库导入引导", value="/admin/database_store.htm*", rtype="admin", rname="数据库管理", rcode="data_manage", rgroup="工具")
   @RequestMapping({"/admin/database_store.htm"})
   public ModelAndView database_store(HttpServletRequest request, HttpServletResponse response, String name) {
     ModelAndView mv = new JModelAndView("admin/blue/db.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
       request, response);
     String path = request.getSession().getServletContext().getRealPath("/") + 
       "data" + File.separator + name;
     File dir = new File(path);
     File[] files = dir.listFiles();
     request.getSession(false).setAttribute("db_store_tables", files);
     mv.addObject("name", name);
     mv.addObject("mode", "store");
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="数据库分卷导入", value="/admin/database_bund_store.htm*", rtype="admin", rname="数据库管理", rcode="data_manage", rgroup="工具")
   @RequestMapping({"/admin/database_bund_store.htm"})
   public void database_bund_store(HttpServletRequest request, HttpServletResponse response)
     throws Exception
   {
     File[] files = (File[])request.getSession(false).getAttribute(
       "db_store_tables");
     if ((files != null) && (files.length > 0))
       for (int i = 0; i < files.length; i++) {
         request.getSession(false).setAttribute("db_mode", "store");
         request.getSession(false).setAttribute("db_bound", Integer.valueOf(i + 1));
         request.getSession(false).setAttribute("db_error", Integer.valueOf(0));
         request.getSession(false).setAttribute("db_result", Integer.valueOf(0));
         boolean ret = this.databaseTools.executSqlScript(files[i]
           .getPath());
         if (ret) {
           if (i == files.length - 1)
             request.getSession(false).setAttribute("db_result", Integer.valueOf(1));
         }
         else
           request.getSession(false).setAttribute("db_result", Integer.valueOf(-1));
       }
   }
 
   @RequestMapping({"/admin/database_deal_info.htm"})
   public void database_deal_info(HttpServletRequest request, HttpServletResponse response)
   {
     JSONObject obj = new JSONObject();
     obj.put("error", request.getSession(false).getAttribute("db_error"));
     obj.put("bound", request.getSession(false).getAttribute("db_bound"));
     obj.put("result", request.getSession(false).getAttribute("db_result"));
     obj.put("mode", request.getSession(false).getAttribute("db_mode"));
     response.setContentType("text/html");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
 
       writer.print(obj.toJSONString());
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @RequestMapping({"/admin/database_success.htm"})
   public ModelAndView database_success(HttpServletRequest request, HttpServletResponse response, String mode) throws Exception
   {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String op_title = "数据备份成功";
     String list_url = CommUtil.getURL(request) + "/admin/database_add.htm";
     if (mode.equals("store")) {
       CacheManager manager = CacheManager.create();
       manager.clearAll();
       op_title = "数据恢复成功";
       list_url = CommUtil.getURL(request) + "/admin/database_list.htm";
     }
     request.getSession(false).removeAttribute("db_error");
     request.getSession(false).removeAttribute("db_bound");
     request.getSession(false).removeAttribute("db_result");
     request.getSession(false).removeAttribute("db_mode");
     request.getSession(false).removeAttribute("db_store_tables");
     request.getSession(false).removeAttribute("backup_type");
     request.getSession(false).removeAttribute("backup_tables");
     request.getSession(false).removeAttribute("backup_name");
     request.getSession(false).removeAttribute("backup_size");
     mv.addObject("op_title", op_title);
     mv.addObject("list_url", list_url);
     return mv;
   }
 
   @RequestMapping({"/admin/database_error.htm"})
   public ModelAndView database_error(HttpServletRequest request, HttpServletResponse response, String mode) throws Exception {
     ModelAndView mv = new JModelAndView("admin/blue/error.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String op_title = "数据备份失败";
     String list_url = CommUtil.getURL(request) + "/admin/database_add.htm";
     if (mode.equals("store")) {
       op_title = "数据恢复失败";
       list_url = CommUtil.getURL(request) + "/admin/database_list.htm";
     }
     request.getSession(false).removeAttribute("db_error");
     request.getSession(false).removeAttribute("db_bound");
     request.getSession(false).removeAttribute("db_result");
     request.getSession(false).removeAttribute("db_mode");
     request.getSession(false).removeAttribute("db_store_tables");
     request.getSession(false).removeAttribute("backup_type");
     request.getSession(false).removeAttribute("backup_tables");
     request.getSession(false).removeAttribute("backup_name");
     request.getSession(false).removeAttribute("backup_size");
     mv.addObject("op_title", op_title);
     mv.addObject("list_url", list_url);
     return mv;
   }
 }


 
 
 