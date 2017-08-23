 package com.shopping.manage.buyer.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.Report;
 import com.shopping.foundation.domain.ReportSubject;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.ReportQueryObject;
 import com.shopping.foundation.service.IAccessoryService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.IReportService;
 import com.shopping.foundation.service.IReportSubjectService;
 import com.shopping.foundation.service.IReportTypeService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.util.ArrayList;
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
 public class ReportBuyerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IReportTypeService reportTypeService;
 
   @Autowired
   private IReportSubjectService reportSubjectService;
 
   @Autowired
   private IReportService reportService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @Autowired
   private IUserService userService;
 
   @SecurityMapping(display = false, rsequence = 0, title="买家举报列表", value="/buyer/report.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/report.htm"})
   public ModelAndView report(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/report.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     ReportQueryObject rqo = new ReportQueryObject(currentPage, mv, null, 
       null);
     rqo.addQuery("obj.user.id", 
       new SysMap("user_id", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     IPageList pList = this.reportService.list(rqo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="买家举报商品", value="/buyer/report_add.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/report_add.htm"})
   public ModelAndView report_add(HttpServletRequest request, HttpServletResponse response, String goods_id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/report_add.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     if (user.getReport() == -1) {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您因为恶意举报已被禁止举报，请与商城管理员联系");
       mv.addObject("url", CommUtil.getURL(request) + "/goods_" + goods_id + 
         ".htm");
     } else {
       Map params = new HashMap();
       params.put("goods_id", CommUtil.null2Long(goods_id));
       params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
       params.put("status", Integer.valueOf(0));
       List reports = this.reportService
         .query(
         "select obj from Report obj where obj.goods.id=:goods_id and obj.user.id=:user_id and obj.status=:status", 
         params, -1, -1);
       if (reports.size() == 0) {
         Goods goods = this.goodsService.getObjById(
           CommUtil.null2Long(goods_id));
         mv.addObject("goods", goods);
         List types = this.reportTypeService
           .query(
           "select obj from ReportType obj order by obj.addTime desc", 
           null, -1, -1);
         mv.addObject("types", types);
       } else {
         mv = new JModelAndView("error.html", this.configService
           .getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "您已经举报该商品，且尚未得到商城处理");
         mv.addObject("url", CommUtil.getURL(request) + "/goods_" + 
           goods_id + ".htm");
       }
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="保存买家举报商品", value="/buyer/report_save.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/report_save.htm"})
   public ModelAndView report_save(HttpServletRequest request, HttpServletResponse response, String goods_id, String subject_id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/success.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     WebForm wf = new WebForm();
     Report report = (Report)wf.toPo(request, Report.class);
     report.setAddTime(new Date());
     report.setUser(SecurityUserHolder.getCurrentUser());
     Goods goods = this.goodsService
       .getObjById(CommUtil.null2Long(goods_id));
     report.setGoods(goods);
     ReportSubject subject = this.reportSubjectService.getObjById(
       CommUtil.null2Long(subject_id));
     report.setSubject(subject);
 
     String uploadFilePath = this.configService.getSysConfig()
       .getUploadFilePath();
     String saveFilePathName = request.getSession().getServletContext()
       .getRealPath("/") + 
       uploadFilePath + File.separator + "report";
     Map map = new HashMap();
     try {
       map = CommUtil.saveFileToServer(request, "img1", saveFilePathName, 
         null, null);
       if (map.get("fileName") != "") {
         Accessory acc1 = new Accessory();
         acc1.setName(CommUtil.null2String(map.get("fileName")));
         acc1.setExt(CommUtil.null2String(map.get("mime")));
         acc1.setSize(CommUtil.null2Float(map.get("fileSize")));
         acc1.setPath(uploadFilePath + "/report");
         acc1.setWidth(CommUtil.null2Int(map.get("width")));
         acc1.setHeight(CommUtil.null2Int(map.get("height")));
         acc1.setAddTime(new Date());
         this.accessoryService.save(acc1);
         report.setAcc1(acc1);
       }
       map.clear();
       map = CommUtil.saveFileToServer(request, "img2", saveFilePathName, 
         null, null);
       if (map.get("fileName") != "") {
         Accessory acc2 = new Accessory();
         acc2.setName(CommUtil.null2String(map.get("fileName")));
         acc2.setExt(CommUtil.null2String(map.get("mime")));
         acc2.setSize(CommUtil.null2Float(map.get("fileSize")));
         acc2.setPath(uploadFilePath + "/report");
         acc2.setWidth(CommUtil.null2Int(map.get("width")));
         acc2.setHeight(CommUtil.null2Int(map.get("height")));
         acc2.setAddTime(new Date());
         this.accessoryService.save(acc2);
         report.setAcc2(acc2);
       }
       map.clear();
       map = CommUtil.saveFileToServer(request, "img3", saveFilePathName, 
         null, null);
       if (map.get("fileName") != "") {
         Accessory acc3 = new Accessory();
         acc3.setName(CommUtil.null2String(map.get("fileName")));
         acc3.setExt(CommUtil.null2String(map.get("mime")));
         acc3.setSize(CommUtil.null2Float(map.get("fileSize")));
         acc3.setPath(uploadFilePath + "/report");
         acc3.setWidth(CommUtil.null2Int(map.get("width")));
         acc3.setHeight(CommUtil.null2Int(map.get("height")));
         acc3.setAddTime(new Date());
         this.accessoryService.save(acc3);
         report.setAcc3(acc3);
       }
     }
     catch (IOException e) {
       e.printStackTrace();
     }
     this.reportService.save(report);
     mv.addObject("op_title", "举报商品成功");
     mv.addObject("url", CommUtil.getURL(request) + "/buyer/report.htm");
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="买家举报详情", value="/buyer/report_view.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/report_view.htm"})
   public ModelAndView report_view(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/report_view.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Report obj = this.reportService.getObjById(CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="买家取消举报", value="/buyer/report_cancel.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/report_cancel.htm"})
   public String report_cancel(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     Report obj = this.reportService.getObjById(CommUtil.null2Long(id));
     obj.setStatus(-1);
     this.reportService.update(obj);
     return "redirect:report.htm?currentPage=" + currentPage;
   }
 
   @RequestMapping({"/buyer/report_subject_load.htm"})
   public void report_subject_load(HttpServletRequest request, HttpServletResponse response, String type_id) {
     Map params = new HashMap();
     params.put("id", CommUtil.null2Long(type_id));
     List<ReportSubject> rss = this.reportSubjectService.query(
       "select obj from ReportSubject obj where obj.type.id=:id", 
       params, -1, -1);
     List<Map> list = new ArrayList<Map>();
     for (ReportSubject rs : rss) {
       Map map = new HashMap();
       map.put("id", rs.getId());
       map.put("title", rs.getTitle());
       list.add(map);
     }
     String temp = Json.toJson(list, JsonFormat.compact());
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(temp);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 }


 
 
 