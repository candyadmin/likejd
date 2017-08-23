 package com.shopping.manage.seller.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.domain.Complaint;
 import com.shopping.foundation.domain.OrderForm;
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
 import java.io.File;
 import java.io.IOException;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.servlet.ServletContext;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class ComplaintSellerAction
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
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家投诉发起", value="/seller/complaint_handle.htm*", rtype="seller", rname="投诉管理", rcode="complaint_seller", rgroup="客户服务")
   @RequestMapping({"/seller/complaint_handle.htm"})
   public ModelAndView complaint_handle(HttpServletRequest request, HttpServletResponse response, String order_id)
   {
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
         obj.setType("seller");
         obj.setOf(of);
         obj.setTo_user(of.getUser());
         mv.addObject("obj", obj);
         Object params = new HashMap();
         ((Map)params).put("type", "seller");
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
           "/seller/order.htm");
       }
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "该订单已经超过投诉有效期，不能投诉");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return (ModelAndView)mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家被投诉列表", value="/seller/complaint.htm*", rtype="seller", rname="投诉管理", rcode="complaint_seller", rgroup="客户服务")
   @RequestMapping({"/seller/complaint.htm"})
   public ModelAndView complaint_seller(HttpServletRequest request, HttpServletResponse response, String currentPage, String status) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/seller_complaint.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     ComplaintQueryObject qo = new ComplaintQueryObject(currentPage, mv, 
       "addTime", "desc");
     qo.addQuery("obj.to_user.id", 
       new SysMap("user_id", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     if (!CommUtil.null2String(status).equals(""))
       qo.addQuery("obj.status", 
         new SysMap("status", 
         Integer.valueOf(CommUtil.null2Int(status))), "=");
     else {
       qo.addQuery("obj.status", new SysMap("status", Integer.valueOf(0)), ">=");
     }
     IPageList pList = this.complaintService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("status", status);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家查看投诉详情", value="/seller/complaint_view.htm*", rtype="seller", rname="投诉管理", rcode="complaint_seller", rgroup="客户服务")
   @RequestMapping({"/seller/complaint_view.htm"})
   public ModelAndView complaint_view(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/seller_complaint_view.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
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
         "/seller/complaint.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家查看投诉详情", value="/seller/complaint_appeal.htm*", rtype="seller", rname="投诉管理", rcode="complaint_seller", rgroup="客户服务")
   @RequestMapping({"/seller/complaint_appeal.htm"})
   public ModelAndView complaint_appeal(HttpServletRequest request, HttpServletResponse response, String id, String to_user_content) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/success.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Complaint obj = this.complaintService
       .getObjById(CommUtil.null2Long(id));
     obj.setStatus(2);
     obj.setTo_user_content(to_user_content);
     obj.setAppeal_time(new Date());
     String uploadFilePath = this.configService.getSysConfig()
       .getUploadFilePath();
     String saveFilePathName = request.getSession().getServletContext()
       .getRealPath("/") + 
       uploadFilePath + File.separator + "complaint";
     Map map = new HashMap();
     try {
       map = CommUtil.saveFileToServer(request, "img1", saveFilePathName, 
         null, null);
       if (map.get("fileName") != "") {
         Accessory to_acc1 = new Accessory();
         to_acc1.setName(CommUtil.null2String(map.get("fileName")));
         to_acc1.setExt(CommUtil.null2String(map.get("mime")));
         to_acc1.setSize(CommUtil.null2Float(map.get("fileSize")));
         to_acc1.setPath(uploadFilePath + "/complaint");
         to_acc1.setWidth(CommUtil.null2Int(map.get("width")));
         to_acc1.setHeight(CommUtil.null2Int(map.get("height")));
         to_acc1.setAddTime(new Date());
         this.accessoryService.save(to_acc1);
         obj.setTo_acc1(to_acc1);
       }
       map.clear();
       map = CommUtil.saveFileToServer(request, "img2", saveFilePathName, 
         null, null);
       if (map.get("fileName") != "") {
         Accessory to_acc2 = new Accessory();
         to_acc2.setName(CommUtil.null2String(map.get("fileName")));
         to_acc2.setExt(CommUtil.null2String(map.get("mime")));
         to_acc2.setSize(CommUtil.null2Float(map.get("fileSize")));
         to_acc2.setPath(uploadFilePath + "/complaint");
         to_acc2.setWidth(CommUtil.null2Int(map.get("width")));
         to_acc2.setHeight(CommUtil.null2Int(map.get("height")));
         to_acc2.setAddTime(new Date());
         this.accessoryService.save(to_acc2);
         obj.setTo_acc2(to_acc2);
       }
       map.clear();
       map = CommUtil.saveFileToServer(request, "img3", saveFilePathName, 
         null, null);
       if (map.get("fileName") != "") {
         Accessory to_acc3 = new Accessory();
         to_acc3.setName(CommUtil.null2String(map.get("fileName")));
         to_acc3.setExt(CommUtil.null2String(map.get("mime")));
         to_acc3.setSize(CommUtil.null2Float(map.get("fileSize")));
         to_acc3.setPath(uploadFilePath + "/complaint");
         to_acc3.setWidth(CommUtil.null2Int(map.get("width")));
         to_acc3.setHeight(CommUtil.null2Int(map.get("height")));
         to_acc3.setAddTime(new Date());
         this.accessoryService.save(to_acc3);
         obj.setTo_acc3(to_acc3);
       }
     }
     catch (IOException e) {
       e.printStackTrace();
     }
     this.complaintService.update(obj);
     mv.addObject("op_title", "申诉提交成功");
     mv.addObject("url", CommUtil.getURL(request) + "/seller/complaint.htm");
     return mv;
   }
 }


 
 
 