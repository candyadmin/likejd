 package com.shopping.manage.admin.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Complaint;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.ComplaintQueryObject;
 import com.shopping.foundation.service.IComplaintService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.nutz.json.Json;
 import org.nutz.json.JsonFormat;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class ComplaintManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IComplaintService complaintService;
 
   @SecurityMapping(display = false, rsequence = 0, title="投诉列表", value="/admin/complaint_list.htm*", rtype="admin", rname="投诉管理", rcode="complaint_manage", rgroup="交易")
   @RequestMapping({"/admin/complaint_list.htm"})
   public ModelAndView complaint_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String status, String from_user, String to_user, String title, String beginTime, String endTime)
   {
     ModelAndView mv = new JModelAndView("admin/blue/complaint_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("status", CommUtil.null2String(status).equals("") ? "new" : 
       status);
     if ((CommUtil.null2String(status).equals("")) || 
       (CommUtil.null2String(status).equals("new"))) {
       status = "0";
     }
     if (CommUtil.null2String(status).equals("complain")) {
       status = "1";
     }
     if (CommUtil.null2String(status).equals("talk")) {
       status = "2";
     }
     if (CommUtil.null2String(status).equals("arbitrate")) {
       status = "3";
     }
     if (CommUtil.null2String(status).equals("close")) {
       status = "4";
     }
     ComplaintQueryObject qo = new ComplaintQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.status", 
       new SysMap("status", 
       Integer.valueOf(CommUtil.null2Int(status))), "=");
     if (!CommUtil.null2String(from_user).equals("")) {
       qo.addQuery("obj.from_user.userName", 
         new SysMap("from_user", 
         from_user), "=");
     }
     if (!CommUtil.null2String(to_user).equals("")) {
       qo.addQuery("obj.to_user.userName", new SysMap("to_user", to_user), 
         "=");
     }
     if (!CommUtil.null2String(beginTime).equals("")) {
       qo.addQuery("obj.addTime", 
         new SysMap("beginTime", 
         CommUtil.formatDate(beginTime)), ">=");
     }
     if (!CommUtil.null2String(endTime).equals("")) {
       qo.addQuery("obj.addTime", 
         new SysMap("endTime", 
         CommUtil.formatDate(endTime)), "<=");
     }
     IPageList pList = this.complaintService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("from_user", from_user);
     mv.addObject("to_user", to_user);
     mv.addObject("title", title);
     mv.addObject("beginTime", beginTime);
     mv.addObject("endTime", endTime);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="投诉设置", value="/admin/complaint_set.htm*", rtype="admin", rname="投诉管理", rcode="complaint_manage", rgroup="交易")
   @RequestMapping({"/admin/complaint_set.htm"})
   public ModelAndView complaint_set(HttpServletRequest request, HttpServletResponse response, String currentPage) {
     ModelAndView mv = new JModelAndView("admin/blue/complaint_set.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("config", this.configService.getSysConfig());
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="投诉设置保存", value="/admin/complaint_set_save.htm*", rtype="admin", rname="投诉管理", rcode="complaint_manage", rgroup="交易")
   @RequestMapping({"/admin/complaint_set_save.htm"})
   public ModelAndView complaint_set_save(HttpServletRequest request, HttpServletResponse response, String id, String complaint_time) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (id.equals("")) {
       SysConfig config = this.configService.getSysConfig();
       config.setComplaint_time(CommUtil.null2Int(complaint_time));
       this.configService.save(config);
     } else {
       SysConfig config = this.configService.getSysConfig();
       config.setComplaint_time(CommUtil.null2Int(complaint_time));
       this.configService.update(config);
     }
     mv.addObject("op_title", "投诉设置成功");
     mv.addObject("url", CommUtil.getURL(request) + 
       "/admin/complaint_set.htm");
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="投诉详情", value="/admin/complaint_view.htm*", rtype="admin", rname="投诉管理", rcode="complaint_manage", rgroup="交易")
   @RequestMapping({"/admin/complaint_view.htm"})
   public ModelAndView complaint_view(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView("admin/blue/complaint_view.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     Complaint obj = this.complaintService
       .getObjById(CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="投诉图片", value="/admin/complaint_img.htm*", rtype="admin", rname="投诉管理", rcode="complaint_manage", rgroup="交易")
   @RequestMapping({"/admin/complaint_img.htm"})
   public ModelAndView complaint_img(HttpServletRequest request, HttpServletResponse response, String id, String type) {
     ModelAndView mv = new JModelAndView("admin/blue/complaint_img.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     Complaint obj = this.complaintService
       .getObjById(CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     mv.addObject("type", type);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="投诉审核", value="/admin/complaint_audit.htm*", rtype="admin", rname="投诉管理", rcode="complaint_manage", rgroup="交易")
   @RequestMapping({"/admin/complaint_audit.htm"})
   public ModelAndView complaint_audit(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     Complaint obj = this.complaintService
       .getObjById(CommUtil.null2Long(id));
     obj.setStatus(1);
     this.complaintService.update(obj);
     mv.addObject("op_title", "审核投诉成功");
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/complaint_list.htm");
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="投诉关闭", value="/admin/complaint_close.htm*", rtype="admin", rname="投诉管理", rcode="complaint_manage", rgroup="交易")
   @RequestMapping({"/admin/complaint_close.htm"})
   public ModelAndView complaint_close(HttpServletRequest request, HttpServletResponse response, String id, String handle_content) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     Complaint obj = this.complaintService
       .getObjById(CommUtil.null2Long(id));
     obj.setStatus(4);
     obj.setHandle_content(handle_content);
     obj.setHandle_time(new Date());
     this.complaintService.update(obj);
     mv.addObject("op_title", "关闭投诉成功");
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/complaint_list.htm");
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="发布投诉对话", value="/admin/complaint_talk.htm*", rtype="admin", rname="投诉管理", rcode="complaint_manage", rgroup="交易")
   @RequestMapping({"/admin/complaint_talk.htm"})
   public void complaint_talk(HttpServletRequest request, HttpServletResponse response, String id, String talk_content) throws IOException {
     Complaint obj = this.complaintService
       .getObjById(CommUtil.null2Long(id));
     if (!CommUtil.null2String(talk_content).equals("")) {
       String temp = "管理员[" + 
         SecurityUserHolder.getCurrentUser().getUsername() + "] " + 
         CommUtil.formatLongDate(new Date()) + "说: " + 
         talk_content;
       if (obj.getTalk_content() == null)
         obj.setTalk_content(temp);
       else {
         obj.setTalk_content(temp + "\n\r" + obj.getTalk_content());
       }
       this.complaintService.update(obj);
     }
     List maps = new ArrayList();
     for (String s : CommUtil.str2list(obj.getTalk_content())) {
       Map map = new HashMap();
       map.put("content", s);
       if (s.indexOf("管理员") == 0) {
         map.put("role", "admin");
       }
       if (s.indexOf("投诉") == 0) {
         map.put("role", "from_user");
       }
       if (s.indexOf("申诉") == 0) {
         map.put("role", "to_user");
       }
       maps.add(map);
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(Json.toJson(maps, JsonFormat.compact()));
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="投诉仲裁", value="/admin/complaint_handle_close.htm*", rtype="admin", rname="投诉管理", rcode="complaint_manage", rgroup="交易")
   @RequestMapping({"/admin/complaint_handle_close.htm"})
   public ModelAndView complaint_handle_close(HttpServletRequest request, HttpServletResponse response, String id, String handle_content) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     Complaint obj = this.complaintService
       .getObjById(CommUtil.null2Long(id));
     obj.setStatus(4);
     obj.setHandle_content(handle_content);
     obj.setHandle_time(new Date());
     obj.setHandle_user(SecurityUserHolder.getCurrentUser());
     this.complaintService.update(obj);
     mv.addObject("op_title", "投诉仲裁成功");
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/complaint_list.htm");
     return mv;
   }
 }


 
 
 