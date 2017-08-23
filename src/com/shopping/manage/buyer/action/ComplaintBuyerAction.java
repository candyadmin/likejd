 package com.shopping.manage.buyer.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.domain.Complaint;
 import com.shopping.foundation.domain.ComplaintGoods;
 import com.shopping.foundation.domain.ComplaintSubject;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.OrderForm;
 import com.shopping.foundation.domain.Store;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.ComplaintQueryObject;
 import com.shopping.foundation.service.IAccessoryService;
 import com.shopping.foundation.service.IComplaintService;
 import com.shopping.foundation.service.IComplaintSubjectService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.IOrderFormService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.servlet.ServletContext;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import org.nutz.json.Json;
 import org.nutz.json.JsonFormat;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class ComplaintBuyerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IComplaintService complaintService;
 
   @Autowired
   private IComplaintSubjectService complaintSubjectService;
 
   @Autowired
   private IOrderFormService orderFormService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @Autowired
   private IUserService userService;
 
   @SecurityMapping(display = false, rsequence = 0, title="买家投诉列表", value="/buyer/complaint.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/complaint.htm"})
   public ModelAndView complaint(HttpServletRequest request, HttpServletResponse response, String currentPage, String status)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/buyer_complaint.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     ComplaintQueryObject qo = new ComplaintQueryObject(currentPage, mv, 
       "addTime", "desc");
     qo.addQuery("obj.from_user.id", 
       new SysMap("user_id", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     if (!CommUtil.null2String(status).equals("")) {
       qo.addQuery("obj.status", 
         new SysMap("status", 
         Integer.valueOf(CommUtil.null2Int(status))), "=");
     }
     IPageList pList = this.complaintService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("status", status);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="买家投诉发起", value="/buyer/complaint_handle.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/complaint_handle.htm"})
   public ModelAndView complaint_handle(HttpServletRequest request, HttpServletResponse response, String order_id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/complaint_handle.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm of = this.orderFormService.getObjById(
       CommUtil.null2Long(order_id));
     Calendar calendar = Calendar.getInstance();
     calendar.add(6, 
       -this.configService.getSysConfig()
       .getComplaint_time());
     boolean result = true;
     if ((of.getOrder_status() == 60) && 
       (of.getFinishTime().before(calendar.getTime()))) {
       result = false;
     }
 
     boolean result1 = true;
     if (of.getComplaints().size() > 0) {
       for (Complaint complaint : of.getComplaints())
       {
         if (complaint.getFrom_user().getId().equals(
           SecurityUserHolder.getCurrentUser().getId())) {
           result1 = false;
         }
       }
     }
     if (result) {
       if (result1) {
         Complaint obj = new Complaint();
         obj.setFrom_user(SecurityUserHolder.getCurrentUser());
         obj.setStatus(0);
         obj.setType("buyer");
         obj.setOf(of);
         obj.setTo_user(of.getStore().getUser());
         mv.addObject("obj", obj);
         Object params = new HashMap();
         ((Map)params).put("type", "buyer");
         List css = this.complaintSubjectService
           .query(
           "select obj from ComplaintSubject obj where obj.type=:type", 
           (Map)params, -1, -1);
         mv.addObject("css", css);
       } else {
         mv = new JModelAndView("error.html", this.configService
           .getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "该订单已经投诉，不允许重复投诉");
         mv.addObject("url", CommUtil.getURL(request) + 
           "/buyer/order.htm");
       }
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "该订单已经超过投诉有效期，不能投诉");
       mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
     }
     return (ModelAndView)mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="买家投诉列表", value="/buyer/complaint_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/complaint_save.htm"})
   public ModelAndView complaint_save(HttpServletRequest request, HttpServletResponse response, String order_id, String cs_id, String from_user_content, String goods_ids, String to_user_id, String type)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/success.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Complaint obj = new Complaint();
     obj.setAddTime(new Date());
     ComplaintSubject cs = this.complaintSubjectService.getObjById(
       CommUtil.null2Long(cs_id));
     OrderForm of = this.orderFormService.getObjById(
       CommUtil.null2Long(order_id));
     obj.setCs(cs);
     obj.setFrom_user_content(from_user_content);
     obj.setFrom_user(SecurityUserHolder.getCurrentUser());
     obj.setTo_user(this.userService.getObjById(
       CommUtil.null2Long(to_user_id)));
     obj.setType(type);
     obj.setOf(of);
     String[] goods_id_list = goods_ids.split(",");
     for (String goods_id : goods_id_list) {
       Goods goods = this.goodsService.getObjById(
         CommUtil.null2Long(goods_id));
       ComplaintGoods cg = new ComplaintGoods();
       cg.setAddTime(new Date());
       cg.setComplaint(obj);
       cg.setGoods(goods);
       cg.setContent(CommUtil.null2String(request.getParameter("content_" + 
         goods_id)));
       obj.getCgs().add(cg);
     }
     String uploadFilePath = this.configService.getSysConfig()
       .getUploadFilePath();
     String saveFilePathName = request.getSession().getServletContext()
       .getRealPath("/") + 
       uploadFilePath + File.separator + "complaint";
     Object map = new HashMap();
     try {
       map = CommUtil.saveFileToServer(request, "img1", saveFilePathName, 
         null, null);
       if (((Map)map).get("fileName") != "") {
         Accessory from_acc1 = new Accessory();
         from_acc1.setName(CommUtil.null2String(((Map)map).get("fileName")));
         from_acc1.setExt(CommUtil.null2String(((Map)map).get("mime")));
         from_acc1.setSize(CommUtil.null2Float(((Map)map).get("fileSize")));
         from_acc1.setPath(uploadFilePath + "/complaint");
         from_acc1.setWidth(CommUtil.null2Int(((Map)map).get("width")));
         from_acc1.setHeight(CommUtil.null2Int(((Map)map).get("height")));
         from_acc1.setAddTime(new Date());
         this.accessoryService.save(from_acc1);
         obj.setFrom_acc1(from_acc1);
       }
       ((Map)map).clear();
       map = CommUtil.saveFileToServer(request, "img2", saveFilePathName, 
         null, null);
       if (((Map)map).get("fileName") != "") {
         Accessory from_acc2 = new Accessory();
         from_acc2.setName(CommUtil.null2String(((Map)map).get("fileName")));
         from_acc2.setExt(CommUtil.null2String(((Map)map).get("mime")));
         from_acc2.setSize(CommUtil.null2Float(((Map)map).get("fileSize")));
         from_acc2.setPath(uploadFilePath + "/complaint");
         from_acc2.setWidth(CommUtil.null2Int(((Map)map).get("width")));
         from_acc2.setHeight(CommUtil.null2Int(((Map)map).get("height")));
         from_acc2.setAddTime(new Date());
         this.accessoryService.save(from_acc2);
         obj.setFrom_acc2(from_acc2);
       }
       ((Map)map).clear();
       map = CommUtil.saveFileToServer(request, "img3", saveFilePathName, 
         null, null);
       if (((Map)map).get("fileName") != "") {
         Accessory from_acc3 = new Accessory();
         from_acc3.setName(CommUtil.null2String(((Map)map).get("fileName")));
         from_acc3.setExt(CommUtil.null2String(((Map)map).get("mime")));
         from_acc3.setSize(CommUtil.null2Float(((Map)map).get("fileSize")));
         from_acc3.setPath(uploadFilePath + "/complaint");
         from_acc3.setWidth(CommUtil.null2Int(((Map)map).get("width")));
         from_acc3.setHeight(CommUtil.null2Int(((Map)map).get("height")));
         from_acc3.setAddTime(new Date());
         this.accessoryService.save(from_acc3);
         obj.setFrom_acc3(from_acc3);
       }
     }
     catch (IOException e) {
       e.printStackTrace();
     }
     this.complaintService.save(obj);
     mv.addObject("op_title", "投诉提交成功");
     mv.addObject("url", CommUtil.getURL(request) + "/buyer/complaint.htm");
     return (ModelAndView)mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="买家查看投诉详情", value="/buyer/complaint_view.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/complaint_view.htm"})
   public ModelAndView complaint_view(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/complaint_view.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Complaint obj = this.complaintService
       .getObjById(CommUtil.null2Long(id));
 
     if ((obj.getFrom_user().getId().equals(
       SecurityUserHolder.getCurrentUser().getId())) || 
       (obj.getTo_user().getId().equals(
       SecurityUserHolder.getCurrentUser().getId()))) {
       mv.addObject("obj", obj);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "参数错误，不存在该投诉");
       mv.addObject("url", CommUtil.getURL(request) + 
         "/buyer/complaint.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="买家取消投诉", value="/buyer/complaint_cancel.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/complaint_cancel.htm"})
   public String complaint_cancel(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/success.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     this.complaintService.delete(CommUtil.null2Long(id));
 
     return "redirect:complaint.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="投诉图片", value="/buyer/complaint_img.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/complaint_img.htm"})
   public ModelAndView complaint_img(HttpServletRequest request, HttpServletResponse response, String id, String type) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/complaint_img.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Complaint obj = this.complaintService
       .getObjById(CommUtil.null2Long(id));
     mv.addObject("type", type);
     mv.addObject("obj", obj);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="发布投诉对话", value="/buyer/complaint_talk.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/complaint_talk.htm"})
   public void complaint_talk(HttpServletRequest request, HttpServletResponse response, String id, String talk_content) throws IOException {
     Complaint obj = this.complaintService
       .getObjById(CommUtil.null2Long(id));
     if (!CommUtil.null2String(talk_content).equals("")) {
       String user_role = "";
 
       if (SecurityUserHolder.getCurrentUser().getId().equals(
         obj.getFrom_user().getId())) {
         user_role = "投诉人";
       }
 
       if (SecurityUserHolder.getCurrentUser().getId().equals(
         obj.getTo_user().getId())) {
         user_role = "申诉人";
       }
       String temp = user_role + "[" + 
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
 
   @SecurityMapping(display = false, rsequence = 0, title="申诉提交仲裁", value="/buyer/complaint_arbitrate.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/complaint_arbitrate.htm"})
   public ModelAndView complaint_arbitrate(HttpServletRequest request, HttpServletResponse response, String id, String to_user_content) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/success.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Complaint obj = this.complaintService
       .getObjById(CommUtil.null2Long(id));
     obj.setStatus(3);
     this.complaintService.update(obj);
     mv.addObject("op_title", "申诉提交仲裁成功");
     mv.addObject("url", CommUtil.getURL(request) + 
       "/buyer/complaint_seller.htm");
     return mv;
   }
 }


 
 
 