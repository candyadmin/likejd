 package com.shopping.manage.seller.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.OrderForm;
 import com.shopping.foundation.domain.Payment;
 import com.shopping.foundation.domain.Store;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.service.IOrderFormService;
 import com.shopping.foundation.service.IPaymentService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import com.shopping.manage.admin.tools.PaymentTools;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.nutz.json.Json;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class PaymentSellerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IPaymentService paymentService;
 
   @Autowired
   private IOrderFormService orderFormService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private PaymentTools paymentTools;
 
   @SecurityMapping(display = false, rsequence = 0, title="支付方式列表", value="/seller/payment.htm*", rtype="seller", rname="支付方式", rcode="payment_seller", rgroup="交易管理")
   @RequestMapping({"/seller/payment.htm"})
   public ModelAndView payment(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = mv = new JModelAndView(
       "user/default/usercenter/payment.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String store_payment = this.configService.getSysConfig()
       .getStore_payment();
     if ((store_payment != null) && (!store_payment.equals(""))) {
       Map map = (Map)Json.fromJson(HashMap.class, store_payment);
       mv.addObject("map", map);
       mv.addObject("paymentTools", this.paymentTools);
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="支付方式安装", value="/seller/payment_install.htm*", rtype="seller", rname="支付方式", rcode="payment_seller", rgroup="交易管理")
   @RequestMapping({"/seller/payment_install.htm"})
   public ModelAndView payment_install(HttpServletRequest request, HttpServletResponse response, String mark) {
     ModelAndView mv = mv = new JModelAndView(
       "user/default/usercenter/payment/" + mark + ".html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Map map = new HashMap();
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     if ((mark != null) && (!mark.equals(""))) {
       map.put("store_id", user.getStore().getId());
       map.put("mark", mark);
       List objs = this.paymentService
         .query("select obj from Payment obj where obj.store.id=:store_id and obj.mark=:mark", 
         map, -1, -1);
       if (objs.size() > 0) {
         mv.addObject("obj", objs.get(0));
       }
     }
 
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="支付方式卸载", value="/seller/payment_uninstall.htm*", rtype="seller", rname="支付方式", rcode="payment_seller", rgroup="交易管理")
   @RequestMapping({"/seller/payment_uninstall.htm"})
   public ModelAndView payment_uninstall(HttpServletRequest request, HttpServletResponse response, String mark) {
     ModelAndView mv = mv = new JModelAndView(
       "user/default/usercenter/success.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Map params = new HashMap();
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     params.put("mark", mark);
     params.put("store_id", user.getStore().getId());
     List objs = this.paymentService
       .query("select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", 
       params, -1, -1);
     if (objs.size() > 0) {
       for (OrderForm of : ((Payment)objs.get(0)).getOfs()) {
         of.setPayment(null);
         this.orderFormService.update(of);
       }
       this.paymentService.delete(((Payment)objs.get(0)).getId());
     }
     mv.addObject("op_title", "支付方式卸载成功");
     mv.addObject("url", CommUtil.getURL(request) + "/seller/payment.htm");
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="支付方式编辑", value="/seller/payment_edit.htm*", rtype="seller", rname="支付方式", rcode="payment_seller", rgroup="交易管理")
   @RequestMapping({"/seller/payment_edit.htm"})
   public ModelAndView payment_edit(HttpServletRequest request, HttpServletResponse response, String mark) {
     ModelAndView mv = mv = new JModelAndView(
       "user/default/usercenter/payment/" + mark + ".html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Map params = new HashMap();
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     params.put("mark", mark);
     params.put("store_id", user.getStore().getId());
     List objs = this.paymentService
       .query("select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", 
       params, -1, -1);
     if (objs.size() > 0)
       mv.addObject("obj", objs.get(0));
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="支付方式保存", value="/seller/payment_save.htm*", rtype="seller", rname="支付方式", rcode="payment_seller", rgroup="交易管理")
   @RequestMapping({"/seller/payment_save.htm"})
   public ModelAndView payment_save(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = mv = new JModelAndView(
       "user/default/usercenter/success.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     WebForm wf = new WebForm();
     if (!id.equals("")) {
       Payment obj = this.paymentService
         .getObjById(CommUtil.null2Long(id));
       Payment payment = (Payment)wf.toPo(request, obj);
       this.paymentService.update(payment);
     } else {
       Payment payment = (Payment)wf.toPo(request, Payment.class);
       payment.setAddTime(new Date());
       payment.setType("user");
       User user = this.userService.getObjById(
         SecurityUserHolder.getCurrentUser().getId());
       payment.setStore(user.getStore());
       this.paymentService.save(payment);
     }
     mv.addObject("op_title", "支付方式保存成功");
     mv.addObject("url", CommUtil.getURL(request) + "/seller/payment.htm");
     return mv;
   }
 }


 
 
 