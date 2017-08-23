 package com.shopping.manage.buyer.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.Payment;
 import com.shopping.foundation.domain.Predeposit;
 import com.shopping.foundation.domain.PredepositLog;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.PredepositLogQueryObject;
 import com.shopping.foundation.domain.query.PredepositQueryObject;
 import com.shopping.foundation.service.IPaymentService;
 import com.shopping.foundation.service.IPredepositLogService;
 import com.shopping.foundation.service.IPredepositService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import com.shopping.pay.tools.PayTools;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class PredepositBuyerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IPaymentService paymentService;
 
   @Autowired
   private IPredepositService predepositService;
 
   @Autowired
   private IPredepositLogService predepositLogService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private PayTools payTools;
 
   @SecurityMapping(display = false, rsequence = 0, title="会员充值", value="/buyer/predeposit.htm*", rtype="buyer", rname="预存款管理", rcode="predeposit_set", rgroup="用户中心")
   @RequestMapping({"/buyer/predeposit.htm"})
   public ModelAndView predeposit(HttpServletRequest request, HttpServletResponse response, String id)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/buyer_predeposit.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isDeposit()) {
       Map params = new HashMap();
       params.put("type", "admin");
       params.put("install", Boolean.valueOf(true));
       params.put("mark", "alipay_wap");
       params.put("mark2", "balance");
       List payments = this.paymentService
         .query(
         "select obj from Payment obj where obj.type=:type and obj.install=:install and obj.mark !=:mark and obj.mark !=:mark2", 
         params, -1, -1);
       mv.addObject("payments", payments);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "系统未开启预存款");
       mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="会员充值保存", value="/buyer/predeposit_save.htm*", rtype="buyer", rname="预存款管理", rcode="predeposit_set", rgroup="用户中心")
   @RequestMapping({"/buyer/predeposit_save.htm"})
   public ModelAndView predeposit_save(HttpServletRequest request, HttpServletResponse response, String id, String pd_payment, String pd_amount, String pd_remittance_user, String pd_remittance_bank, String pd_remittance_time, String pd_remittance_info)
   {
     ModelAndView mv = new JModelAndView("line_pay.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     if (this.configService.getSysConfig().isDeposit()) {
       WebForm wf = new WebForm();
       Predeposit obj = null;
       if (CommUtil.null2String(id).equals("")) {
         obj = (Predeposit)wf.toPo(request, Predeposit.class);
         obj.setAddTime(new Date());
         if (pd_payment.equals("outline"))
           obj.setPd_pay_status(1);
         else {
           obj.setPd_pay_status(0);
         }
         obj.setPd_sn("pd" + 
           CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + 
           SecurityUserHolder.getCurrentUser().getId());
         obj.setPd_user(SecurityUserHolder.getCurrentUser());
         this.predepositService.save(obj);
 
         PredepositLog log = new PredepositLog();
         log.setAddTime(new Date());
         log.setPd_log_amount(obj.getPd_amount());
         String pay_text = "";
         if (pd_payment.equals("outline")) {
           pay_text = "线下账户";
         }
         if (pd_payment.equals("alipay")) {
           pay_text = "支付宝";
         }
         if (pd_payment.equals("bill")) {
           pay_text = "快钱";
         }
         if (pd_payment.equals("tenpay")) {
           pay_text = "财付通";
         }
         if (pd_payment.equals("chinabank")) {
           pay_text = "网银在线";
         }
         log.setPd_log_info(pay_text + "充值");
         log.setPd_log_user(obj.getPd_user());
         log.setPd_op_type("充值");
         log.setPd_type("可用预存款");
         log.setPredeposit(obj);
         this.predepositLogService.save(log);
       } else {
         Predeposit pre = this.predepositService.getObjById(
           CommUtil.null2Long(id));
         obj = (Predeposit)wf.toPo(request, pre);
         this.predepositService.update(obj);
       }
       if (pd_payment.equals("outline")) {
         mv = new JModelAndView("success.html", this.configService
           .getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "线下支付提交成功，等待审核");
         mv.addObject("url", CommUtil.getURL(request) + 
           "/buyer/predeposit_list.htm");
       } else {
         mv.addObject("payType", pd_payment);
         mv.addObject("type", "cash");
         mv.addObject("url", CommUtil.getURL(request));
         mv.addObject("payTools", this.payTools);
         mv.addObject("cash_id", obj.getId());
         Map params = new HashMap();
         params.put("install", Boolean.valueOf(true));
         params.put("mark", obj.getPd_payment());
         params.put("type", "admin");
         List payments = this.paymentService
           .query(
           "select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type", 
           params, -1, -1);
         mv.addObject("payment_id", payments.size() > 0 ? 
           ((Payment)payments
           .get(0)).getId() : new Payment());
       }
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "系统未开启预存款");
       mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="会员充值列表", value="/buyer/predeposit_list.htm*", rtype="buyer", rname="预存款管理", rcode="predeposit_set", rgroup="用户中心")
   @RequestMapping({"/buyer/predeposit_list.htm"})
   public ModelAndView predeposit_list(HttpServletRequest request, HttpServletResponse response, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/predeposit_list.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isDeposit()) {
       PredepositQueryObject qo = new PredepositQueryObject(currentPage, 
         mv, "addTime", "desc");
       qo.addQuery("obj.pd_user.id", 
         new SysMap("user_id", 
         SecurityUserHolder.getCurrentUser().getId()), "=");
       IPageList pList = this.predepositService.list(qo);
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "系统未开启预存款");
       mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="会员充值详情", value="/buyer/predeposit_view.htm*", rtype="buyer", rname="预存款管理", rcode="predeposit_set", rgroup="用户中心")
   @RequestMapping({"/buyer/predeposit_view.htm"})
   public ModelAndView predeposit_view(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/predeposit_view.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isDeposit()) {
       Predeposit obj = this.predepositService.getObjById(
         CommUtil.null2Long(id));
 
       if (obj.getPd_user().getId().equals(
         SecurityUserHolder.getCurrentUser().getId())) {
         mv.addObject("obj", obj);
       } else {
         mv = new JModelAndView("error.html", this.configService
           .getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "参数错误，您没有该充值信息！");
         mv.addObject("url", CommUtil.getURL(request) + 
           "/buyer/predeposit_list.htm");
       }
     }
     else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "系统未开启预存款");
       mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="会员充值支付", value="/buyer/predeposit_pay.htm*", rtype="buyer", rname="预存款管理", rcode="predeposit_set", rgroup="用户中心")
   @RequestMapping({"/buyer/predeposit_pay.htm"})
   public ModelAndView predeposit_pay(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/predeposit_pay.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isDeposit()) {
       Predeposit obj = this.predepositService.getObjById(
         CommUtil.null2Long(id));
 
       if (obj.getPd_user().getId().equals(
         SecurityUserHolder.getCurrentUser().getId())) {
         mv.addObject("obj", obj);
       } else {
         mv = new JModelAndView("error.html", this.configService
           .getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "参数错误，您没有该充值信息！");
         mv.addObject("url", CommUtil.getURL(request) + 
           "/buyer/predeposit_list.htm");
       }
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "系统未开启预存款");
       mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="会员收入明细", value="/buyer/predeposit_log.htm*", rtype="buyer", rname="预存款管理", rcode="predeposit_set", rgroup="用户中心")
   @RequestMapping({"/buyer/predeposit_log.htm"})
   public ModelAndView predeposit_log(HttpServletRequest request, HttpServletResponse response, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/buyer_predeposit_log.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isDeposit()) {
       PredepositLogQueryObject qo = new PredepositLogQueryObject(
         currentPage, mv, "addTime", "desc");
       qo.addQuery("obj.pd_log_user.id", 
         new SysMap("user_id", 
         SecurityUserHolder.getCurrentUser().getId()), "=");
       IPageList pList = this.predepositLogService.list(qo);
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
       mv.addObject("user", this.userService.getObjById(
         SecurityUserHolder.getCurrentUser().getId()));
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "系统未开启预存款");
       mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
     }
     return mv;
   }
 }


 
 
 