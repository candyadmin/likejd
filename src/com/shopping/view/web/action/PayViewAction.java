 package com.shopping.view.web.action;
 
 import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jdom.JDOMException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.annotation.SecurityMapping;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.Md5Encrypt;
import com.shopping.core.tools.WxCommonUtil;
import com.shopping.foundation.domain.GoldLog;
import com.shopping.foundation.domain.GoldRecord;
import com.shopping.foundation.domain.Goods;
import com.shopping.foundation.domain.GoodsCart;
import com.shopping.foundation.domain.GoodsSpecProperty;
import com.shopping.foundation.domain.GroupGoods;
import com.shopping.foundation.domain.IntegralGoods;
import com.shopping.foundation.domain.IntegralGoodsCart;
import com.shopping.foundation.domain.IntegralGoodsOrder;
import com.shopping.foundation.domain.OrderForm;
import com.shopping.foundation.domain.OrderFormLog;
import com.shopping.foundation.domain.Payment;
import com.shopping.foundation.domain.Predeposit;
import com.shopping.foundation.domain.PredepositLog;
import com.shopping.foundation.domain.User;
import com.shopping.foundation.service.IGoldLogService;
import com.shopping.foundation.service.IGoldRecordService;
import com.shopping.foundation.service.IGoodsService;
import com.shopping.foundation.service.IGroupGoodsService;
import com.shopping.foundation.service.IIntegralGoodsOrderService;
import com.shopping.foundation.service.IIntegralGoodsService;
import com.shopping.foundation.service.IOrderFormLogService;
import com.shopping.foundation.service.IOrderFormService;
import com.shopping.foundation.service.IPaymentService;
import com.shopping.foundation.service.IPredepositLogService;
import com.shopping.foundation.service.IPredepositService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.ITemplateService;
import com.shopping.foundation.service.IUserConfigService;
import com.shopping.foundation.service.IUserService;
import com.shopping.manage.admin.tools.MsgTools;
import com.shopping.pay.alipay.config.AlipayConfig;
import com.shopping.pay.alipay.util.AlipayNotify;
import com.shopping.pay.bill.util.MD5Util;
import com.shopping.pay.tenpay.RequestHandler;
import com.shopping.pay.tenpay.ResponseHandler;
import com.shopping.pay.tenpay.util.TenpayUtil;
 
 @Controller
 public class PayViewAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IOrderFormService orderFormService;
 
   @Autowired
   private IOrderFormLogService orderFormLogService;
 
   @Autowired
   private IPredepositService predepositService;
 
   @Autowired
   private IPredepositLogService predepositLogService;
 
   @Autowired
   private IGoldRecordService goldRecordService;
 
   @Autowired
   private IGoldLogService goldLogService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IPaymentService paymentService;
 
   @Autowired
   private IIntegralGoodsOrderService integralGoodsOrderService;
 
   @Autowired
   private IIntegralGoodsService integralGoodsService;
 
   @Autowired
   private IGroupGoodsService groupGoodsService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private ITemplateService templateService;
 
   @Autowired
   private MsgTools msgTools;
   
   private static Logger logger = LoggerFactory.getLogger(PayViewAction.class);
 
   @RequestMapping({"/aplipay_return.htm"})
   public ModelAndView aplipay_return(HttpServletRequest request, HttpServletResponse response)
     throws Exception
   {
     ModelAndView mv = new JModelAndView("order_finish.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     String trade_no = request.getParameter("trade_no");
     String order_no = request.getParameter("out_trade_no");
     String total_fee = request.getParameter("price");
     String subject = request.getParameter("subject");
 
     String type = CommUtil.null2String(request.getParameter("body")).trim();
     String trade_status = request.getParameter("trade_status");
     OrderForm order = null;
     Predeposit obj = null;
     GoldRecord gold = null;
     IntegralGoodsOrder ig_order = null;
     if (type.equals("goods")) {
       order = this.orderFormService.getObjById(
         CommUtil.null2Long(order_no));
     }
     if (type.equals("cash")) {
       obj = this.predepositService.getObjById(
         CommUtil.null2Long(order_no));
     }
     if (type.equals("gold")) {
       gold = this.goldRecordService.getObjById(
         CommUtil.null2Long(order_no));
     }
     if (type.equals("integral")) {
       ig_order = this.integralGoodsOrderService.getObjById(
         CommUtil.null2Long(order_no));
     }
 
     Map params = new HashMap();
     Map requestParams = request.getParameterMap();
     for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
       String name = (String)iter.next();
       String[] values = (String[])requestParams.get(name);
       String valueStr = "";
       for (int i = 0; i < values.length; i++) {
         //valueStr = valueStr + values[i] + ",";
    	   valueStr = valueStr + values[i];
       }
 
       valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
       params.put(name, valueStr);
     }
     AlipayConfig config = new AlipayConfig();
     if (type.equals("goods")) {
       config.setKey(order.getPayment().getSafeKey());
       config.setPartner(order.getPayment().getPartner());
       config.setSeller_email(order.getPayment().getSeller_email());
     }
     if ((type.equals("cash")) || (type.equals("gold")) || 
       (type.equals("integral"))) {
       Map q_params = new HashMap();
       q_params.put("install", Boolean.valueOf(true));
       if (type.equals("cash")) {
         q_params.put("mark", obj.getPd_payment());
       }
       if (type.equals("gold")) {
         q_params.put("mark", gold.getGold_payment());
       }
       if (type.equals("integral")) {
         q_params.put("mark", ig_order.getIgo_payment());
       }
       q_params.put("type", "admin");
       List payments = this.paymentService
         .query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type", 
         q_params, -1, -1);
       config.setKey(((Payment)payments.get(0)).getSafeKey());
       config.setPartner(((Payment)payments.get(0)).getPartner());
       config.setSeller_email(((Payment)payments.get(0)).getSeller_email());
     }
     config.setNotify_url(CommUtil.getURL(request) + "/alipay_notify.htm");
     config.setReturn_url(CommUtil.getURL(request) + "/aplipay_return.htm");
     boolean verify_result = AlipayNotify.verify(config, params);
     if (verify_result) {
       if ((type.equals("goods")) && (
         (trade_status.equals("WAIT_SELLER_SEND_GOODS")) || 
         (trade_status.equals("TRADE_FINISHED")) || 
         (trade_status.equals("TRADE_SUCCESS")))) {
         if (order.getOrder_status() != 20) {
           order.setOrder_status(20);
           order.setOut_order_id(trade_no);
           order.setPayTime(new Date());
           this.orderFormService.update(order);
 
           update_goods_inventory(order);
           OrderFormLog ofl = new OrderFormLog();
           ofl.setAddTime(new Date());
           ofl.setLog_info("支付宝在线支付");
           ofl.setLog_user(order.getUser());
           ofl.setOf(order);
           this.orderFormLogService.save(ofl);
 
           if (this.configService.getSysConfig().isEmailEnable()) {
             send_order_email(request, order, order
               .getUser().getEmail(), 
               "email_tobuyer_online_pay_ok_notify");
             send_order_email(request, order, order
               .getStore().getUser().getEmail(), 
               "email_toseller_online_pay_ok_notify");
           }
 
           if (this.configService.getSysConfig().isSmsEnbale()) {
             send_order_sms(request, order, order.getUser()
               .getMobile(), 
               "sms_tobuyer_online_pay_ok_notify");
             send_order_sms(request, order, order
               .getStore().getUser().getMobile(), 
               "sms_toseller_online_pay_ok_notify");
           }
         }
         mv.addObject("obj", order);
       }
 
       if ((type.equals("cash")) && (
         (trade_status.equals("WAIT_SELLER_SEND_GOODS")) || 
         (trade_status.equals("TRADE_FINISHED")) || 
         (trade_status.equals("TRADE_SUCCESS")))) {
         if (obj.getPd_pay_status() != 2) {
           obj.setPd_status(1);
           obj.setPd_pay_status(2);
           this.predepositService.update(obj);
           User user = this.userService.getObjById(obj
             .getPd_user().getId());
           user.setAvailableBalance(BigDecimal.valueOf(
             CommUtil.add(user.getAvailableBalance(), 
             obj.getPd_amount())));
           this.userService.update(user);
           PredepositLog log = new PredepositLog();
           log.setAddTime(new Date());
           log.setPd_log_amount(obj.getPd_amount());
           log.setPd_log_user(obj.getPd_user());
           log.setPd_op_type("充值");
           log.setPd_type("可用预存款");
           log.setPd_log_info("支付宝在线支付");
           this.predepositLogService.save(log);
         }
         mv = new JModelAndView("success.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
         mv.addObject("url", CommUtil.getURL(request) + 
           "/buyer/predeposit_list.htm");
       }
       GoldLog log;
       if ((type.equals("gold")) && (
         (trade_status.equals("WAIT_SELLER_SEND_GOODS")) || 
         (trade_status.equals("TRADE_FINISHED")) || 
         (trade_status.equals("TRADE_SUCCESS")))) {
         if (gold.getGold_pay_status() != 2) {
           gold.setGold_status(1);
           gold.setGold_pay_status(2);
           this.goldRecordService.update(gold);
           User user = this.userService.getObjById(gold
             .getGold_user().getId());
           user.setGold(user.getGold() + gold.getGold_count());
           this.userService.update(user);
           log = new GoldLog();
           log.setAddTime(new Date());
           log.setGl_payment(gold.getGold_payment());
           log.setGl_content("支付宝在线支付");
           log.setGl_money(gold.getGold_money());
           log.setGl_count(gold.getGold_count());
           log.setGl_type(0);
           log.setGl_user(gold.getGold_user());
           log.setGr(gold);
           this.goldLogService.save(log);
         }
         mv = new JModelAndView("success.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "兑换" + gold.getGold_count() + 
           "金币成功");
         mv.addObject("url", CommUtil.getURL(request) + 
           "/seller/gold_record_list.htm");
       }
 
       if ((type.equals("integral")) && (
         (trade_status.equals("WAIT_SELLER_SEND_GOODS")) || 
         (trade_status.equals("TRADE_FINISHED")) || 
         (trade_status.equals("TRADE_SUCCESS")))) {
         if (ig_order.getIgo_status() < 20) {
           ig_order.setIgo_status(20);
           ig_order.setIgo_pay_time(new Date());
           ig_order.setIgo_payment("alipay");
           this.integralGoodsOrderService.update(ig_order);
           for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
             IntegralGoods goods = igc.getGoods();
             goods.setIg_goods_count(goods.getIg_goods_count() - 
               igc.getCount());
             goods.setIg_exchange_count(goods
               .getIg_exchange_count() + igc.getCount());
             this.integralGoodsService.update(goods);
           }
         }
         mv = new JModelAndView("integral_order_finish.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("obj", ig_order);
       }
     }
     else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "支付回调失败！");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
 
   @RequestMapping({"/alipay_notify.htm"})
   public void alipay_notify(HttpServletRequest request, HttpServletResponse response)
     throws Exception
   {
     String trade_no = request.getParameter("trade_no");
     String order_no = request.getParameter("out_trade_no");
     String total_fee = request.getParameter("price");
     String subject = request.getParameter("subject");
 
     String type = CommUtil.null2String(request.getParameter("body")).trim();
     String trade_status = request.getParameter("trade_status");
     OrderForm order = null;
     Predeposit obj = null;
     GoldRecord gold = null;
     IntegralGoodsOrder ig_order = null;
     if (type.equals("goods")) {
       order = this.orderFormService.getObjById(
         CommUtil.null2Long(order_no));
     }
     if (type.equals("cash")) {
       obj = this.predepositService.getObjById(
         CommUtil.null2Long(order_no));
     }
     if (type.equals("gold")) {
       gold = this.goldRecordService.getObjById(
         CommUtil.null2Long(order_no));
     }
     if (type.equals("integral")) {
       ig_order = this.integralGoodsOrderService.getObjById(
         CommUtil.null2Long(order_no));
     }
 
     Map params = new HashMap();
     Map requestParams = request.getParameterMap();
     for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
       String name = (String)iter.next();
       String[] values = (String[])requestParams.get(name);
       String valueStr = "";
       for (int i = 0; i < values.length; i++) {
         //valueStr =  valueStr + values[i] + ",";
         valueStr = valueStr + values[i];
       }
 
       valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
       params.put(name, valueStr);
     }
     AlipayConfig config = new AlipayConfig();
     if (type.equals("goods")) {
       config.setKey(order.getPayment().getSafeKey());
       config.setPartner(order.getPayment().getPartner());
       config.setSeller_email(order.getPayment().getSeller_email());
     }
     if ((type.equals("cash")) || (type.equals("gold")) || 
       (type.equals("integral"))) {
       Map q_params = new HashMap();
       q_params.put("install", Boolean.valueOf(true));
       if (type.equals("cash")) {
         q_params.put("mark", obj.getPd_payment());
       }
       if (type.equals("gold")) {
         q_params.put("mark", gold.getGold_payment());
       }
       if (type.equals("integral")) {
         q_params.put("mark", ig_order.getIgo_payment());
       }
       q_params.put("type", "admin");
       List payments = this.paymentService
         .query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type", 
         q_params, -1, -1);
       config.setKey(((Payment)payments.get(0)).getSafeKey());
       config.setPartner(((Payment)payments.get(0)).getPartner());
       config.setSeller_email(((Payment)payments.get(0)).getSeller_email());
     }
     config.setNotify_url(CommUtil.getURL(request) + "/alipay_notify.htm");
     config.setReturn_url(CommUtil.getURL(request) + "/aplipay_return.htm");
     boolean verify_result = AlipayNotify.verify(config, params);
     if (verify_result) {
       if ((type.equals("goods")) && 
         ((trade_status.equals("WAIT_SELLER_SEND_GOODS")) || 
         (trade_status.equals("TRADE_FINISHED")) || 
         (trade_status.equals("TRADE_SUCCESS"))) && 
         (order.getOrder_status() < 20)) {
         order.setOrder_status(20);
         order.setOut_order_id(trade_no);
         order.setPayTime(new Date());
         this.orderFormService.update(order);
 
         update_goods_inventory(order);
         OrderFormLog ofl = new OrderFormLog();
         ofl.setAddTime(new Date());
         ofl.setLog_info("支付宝在线支付");
         ofl.setLog_user(order.getUser());
         ofl.setOf(order);
         this.orderFormLogService.save(ofl);
 
         if (this.configService.getSysConfig().isEmailEnable()) {
           send_order_email(request, order, order
             .getUser().getEmail(), 
             "email_tobuyer_online_pay_ok_notify");
           send_order_email(request, order, order
             .getStore().getUser().getEmail(), 
             "email_toseller_online_pay_ok_notify");
         }
 
         if (this.configService.getSysConfig().isSmsEnbale()) {
           send_order_sms(request, order, order.getUser()
             .getMobile(), 
             "sms_tobuyer_online_pay_ok_notify");
           send_order_sms(request, order, order
             .getStore().getUser().getMobile(), 
             "sms_toseller_online_pay_ok_notify");
         }
 
       }
 
       if ((type.equals("cash")) && 
         ((trade_status.equals("WAIT_SELLER_SEND_GOODS")) || 
         (trade_status.equals("TRADE_FINISHED")) || 
         (trade_status.equals("TRADE_SUCCESS"))) && 
         (obj.getPd_pay_status() < 2)) {
         obj.setPd_status(1);
         obj.setPd_pay_status(2);
         this.predepositService.update(obj);
         User user = this.userService.getObjById(obj
           .getPd_user().getId());
         user.setAvailableBalance(BigDecimal.valueOf(
           CommUtil.add(user.getAvailableBalance(), 
           obj.getPd_amount())));
         this.userService.update(user);
         PredepositLog log = new PredepositLog();
         log.setAddTime(new Date());
         log.setPd_log_amount(obj.getPd_amount());
         log.setPd_log_user(obj.getPd_user());
         log.setPd_op_type("充值");
         log.setPd_type("可用预存款");
         log.setPd_log_info("支付宝在线支付");
         this.predepositLogService.save(log);
       }
       GoldLog log;
       if ((type.equals("gold")) && 
         ((trade_status.equals("WAIT_SELLER_SEND_GOODS")) || 
         (trade_status.equals("TRADE_FINISHED")) || 
         (trade_status.equals("TRADE_SUCCESS"))) && 
         (gold.getGold_pay_status() < 2)) {
         gold.setGold_status(1);
         gold.setGold_pay_status(2);
         this.goldRecordService.update(gold);
         User user = this.userService.getObjById(gold
           .getGold_user().getId());
         user.setGold(user.getGold() + gold.getGold_count());
         this.userService.update(user);
         log = new GoldLog();
         log.setAddTime(new Date());
         log.setGl_payment(gold.getGold_payment());
         log.setGl_content("支付宝在线支付");
         log.setGl_money(gold.getGold_money());
         log.setGl_count(gold.getGold_count());
         log.setGl_type(0);
         log.setGl_user(gold.getGold_user());
         log.setGr(gold);
         this.goldLogService.save(log);
       }
 
       if ((type.equals("integral")) && 
         ((trade_status.equals("WAIT_SELLER_SEND_GOODS")) || 
         (trade_status.equals("TRADE_FINISHED")) || 
         (trade_status.equals("TRADE_SUCCESS"))) && 
         (ig_order.getIgo_status() < 20)) {
         ig_order.setIgo_status(20);
         ig_order.setIgo_pay_time(new Date());
         ig_order.setIgo_payment("alipay");
         this.integralGoodsOrderService.update(ig_order);
         for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
           IntegralGoods goods = igc.getGoods();
           goods.setIg_goods_count(goods.getIg_goods_count() - 
             igc.getCount());
           goods.setIg_exchange_count(goods
             .getIg_exchange_count() + igc.getCount());
           this.integralGoodsService.update(goods);
         }
 
       }
 
       response.setContentType("text/plain");
       response.setHeader("Cache-Control", "no-cache");
       response.setCharacterEncoding("UTF-8");
       try
       {
         PrintWriter writer = response.getWriter();
         writer.print("success");
       }
       catch (IOException e) {
         e.printStackTrace();
       }
     }
     else {
       response.setContentType("text/plain");
       response.setHeader("Cache-Control", "no-cache");
       response.setCharacterEncoding("UTF-8");
       try
       {
         PrintWriter writer = response.getWriter();
         writer.print("fail");
       }
       catch (IOException e) {
         e.printStackTrace();
       }
     }
   }
 
   @RequestMapping({"/bill_return.htm"})
   public ModelAndView bill_return(HttpServletRequest request, HttpServletResponse response)
     throws Exception
   {
     ModelAndView mv = new JModelAndView("order_finish.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
 
     String ext1 = request.getParameter("ext1").trim();
     String ext2 = CommUtil.null2String(request.getParameter("ext2").trim());
     OrderForm order = null;
     Predeposit obj = null;
     GoldRecord gold = null;
     IntegralGoodsOrder ig_order = null;
     if (ext2.equals("goods")) {
       order = this.orderFormService.getObjById(CommUtil.null2Long(ext1));
     }
     if (ext2.equals("cash")) {
       obj = this.predepositService.getObjById(CommUtil.null2Long(ext1));
     }
     if (ext2.equals("gold")) {
       gold = this.goldRecordService.getObjById(CommUtil.null2Long(ext1));
     }
     if (ext2.equals("integral")) {
       ig_order = this.integralGoodsOrderService.getObjById(
         CommUtil.null2Long(ext1));
     }
 
     String merchantAcctId = request.getParameter("merchantAcctId")
       .trim();
 
     String key = "";
     if (ext2.equals("goods")) {
       key = order.getPayment().getRmbKey();
     }
     if ((ext2.equals("cash")) || (ext2.equals("gold")) || 
       (ext2.equals("integral"))) {
       Map q_params = new HashMap();
       q_params.put("install", Boolean.valueOf(true));
       if (ext2.equals("cash")) {
         q_params.put("mark", obj.getPd_payment());
       }
       if (ext2.equals("gold")) {
         q_params.put("mark", gold.getGold_payment());
       }
       if (ext2.equals("integral")) {
         q_params.put("mark", ig_order.getIgo_payment());
       }
       q_params.put("type", "admin");
       List payments = this.paymentService
         .query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type", 
         q_params, -1, -1);
       key = ((Payment)payments.get(0)).getRmbKey();
     }
 
     String version = request.getParameter("version").trim();
 
     String language = request.getParameter("language").trim();
 
     String signType = request.getParameter("signType").trim();
 
     String payType = request.getParameter("payType").trim();
 
     String bankId = request.getParameter("bankId").trim();
 
     String orderId = request.getParameter("orderId").trim();
 
     String orderTime = request.getParameter("orderTime").trim();
 
     String orderAmount = request.getParameter("orderAmount")
       .trim();
 
     String dealId = request.getParameter("dealId").trim();
 
     String bankDealId = request.getParameter("bankDealId").trim();
 
     String dealTime = request.getParameter("dealTime").trim();
 
     String payAmount = request.getParameter("payAmount").trim();
 
     String fee = request.getParameter("fee").trim();
 
     String payResult = request.getParameter("payResult").trim();
 
     String errCode = request.getParameter("errCode").trim();
 
     String signMsg = request.getParameter("signMsg").trim();
 
     String merchantSignMsgVal = "";
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "merchantAcctId", 
       merchantAcctId);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "version", version);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "language", 
       language);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType", 
       signType);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType", payType);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId", bankId);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId", orderId);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime", 
       orderTime);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount", 
       orderAmount);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId", dealId);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId", 
       bankDealId);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime", 
       dealTime);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount", 
       payAmount);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult", 
       payResult);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode", errCode);
     merchantSignMsgVal = appendParam(merchantSignMsgVal, "key", key);
 
     String merchantSignMsg = MD5Util.md5Hex(
       merchantSignMsgVal.getBytes("utf-8")).toUpperCase();
 
     if (signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase()))
     {
       switch (Integer.parseInt(payResult))
       {
       case 10:
         if (ext2.equals("goods")) {
           order.setOrder_status(20);
           order.setPayTime(new Date());
           this.orderFormService.update(order);
           update_goods_inventory(order);
           OrderFormLog ofl = new OrderFormLog();
           ofl.setAddTime(new Date());
           ofl.setLog_info("快钱在线支付");
           ofl.setLog_user(SecurityUserHolder.getCurrentUser());
           ofl.setOf(order);
           this.orderFormLogService.save(ofl);
           mv.addObject("obj", order);
 
           if (this.configService.getSysConfig().isEmailEnable()) {
             send_order_email(request, order, order.getUser()
               .getEmail(), 
               "email_tobuyer_online_pay_ok_notify");
             send_order_email(request, order, order.getStore()
               .getUser().getEmail(), 
               "email_toseller_online_pay_ok_notify");
           }
 
           if (this.configService.getSysConfig().isSmsEnbale()) {
             send_order_sms(request, order, order.getUser()
               .getMobile(), 
               "sms_tobuyer_online_pay_ok_notify");
             send_order_sms(request, order, order.getStore()
               .getUser().getMobile(), 
               "sms_toseller_online_pay_ok_notify");
           }
         }
         if (ext2.equals("cash")) {
           obj.setPd_status(1);
           obj.setPd_pay_status(2);
           this.predepositService.update(obj);
           User user = this.userService.getObjById(obj.getPd_user()
             .getId());
           user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
             user.getAvailableBalance(), obj.getPd_amount())));
           this.userService.update(user);
           PredepositLog log = new PredepositLog();
           log.setAddTime(new Date());
           log.setPd_log_amount(obj.getPd_amount());
           log.setPd_log_user(obj.getPd_user());
           log.setPd_op_type("充值");
           log.setPd_type("可用预存款");
           log.setPd_log_info("快钱在线支付");
           this.predepositLogService.save(log);
           mv = new JModelAndView("success.html", 
             this.configService.getSysConfig(), 
             this.userConfigService.getUserConfig(), 1, request, 
             response);
           mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
           mv.addObject("url", CommUtil.getURL(request) + 
             "/buyer/predeposit_list.htm");
         }
         GoldLog log;
         if (ext2.equals("gold")) {
           gold.setGold_status(1);
           gold.setGold_pay_status(2);
           this.goldRecordService.update(gold);
           User user = this.userService.getObjById(gold.getGold_user()
             .getId());
           user.setGold(user.getGold() + gold.getGold_count());
           this.userService.update(user);
           log = new GoldLog();
           log.setAddTime(new Date());
           log.setGl_payment(gold.getGold_payment());
           log.setGl_content("快钱在线支付");
           log.setGl_money(gold.getGold_money());
           log.setGl_count(gold.getGold_count());
           log.setGl_type(0);
           log.setGl_user(gold.getGold_user());
           log.setGr(gold);
           this.goldLogService.save(log);
           mv = new JModelAndView("success.html", 
             this.configService.getSysConfig(), 
             this.userConfigService.getUserConfig(), 1, request, 
             response);
           mv.addObject("op_title", "兑换" + gold.getGold_count() + 
             "金币成功");
           mv.addObject("url", CommUtil.getURL(request) + 
             "/seller/gold_record_list.htm");
         }
         if (!ext2.equals("integral")) break;
         ig_order.setIgo_status(20);
         ig_order.setIgo_pay_time(new Date());
         ig_order.setIgo_payment("bill");
         this.integralGoodsOrderService.update(ig_order);
         for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
           IntegralGoods goods = igc.getGoods();
           goods.setIg_goods_count(goods.getIg_goods_count() - 
             igc.getCount());
           goods.setIg_exchange_count(goods.getIg_exchange_count() + 
             igc.getCount());
           this.integralGoodsService.update(goods);
         }
         mv = new JModelAndView("integral_order_finish.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("obj", ig_order);
 
         break;
       default:
         mv = new JModelAndView("error.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "快钱支付失败！");
         mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
 
         break;
       } } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "快钱支付失败！");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
 
   public String appendParam(String returnStr, String paramId, String paramValue)
   {
     if (!returnStr.equals("")) {
       if (!paramValue.equals("")) {
         returnStr = returnStr + "&" + paramId + "=" + paramValue;
       }
     }
     else if (!paramValue.equals("")) {
       returnStr = paramId + "=" + paramValue;
     }
 
     return returnStr;
   }
 
   @RequestMapping({"/tenpay.htm"})
   public void tenpay(HttpServletRequest request, HttpServletResponse response, String id, String type, String payment_id)
     throws IOException
   {
     OrderForm of = null;
     Predeposit obj = null;
     GoldRecord gold = null;
     IntegralGoodsOrder ig_order = null;
     if (type.equals("goods")) {
       of = this.orderFormService.getObjById(CommUtil.null2Long(id));
     }
     if (type.equals("cash")) {
       obj = this.predepositService.getObjById(CommUtil.null2Long(id));
     }
     if (type.equals("gold")) {
       gold = this.goldRecordService.getObjById(CommUtil.null2Long(id));
     }
     if (type.equals("integral")) {
       ig_order = this.integralGoodsOrderService.getObjById(
         CommUtil.null2Long(id));
     }
 
     String order_price = "";
     if (type.equals("goods")) {
       order_price = CommUtil.null2String(of.getTotalPrice());
     }
     if (type.equals("cash")) {
       order_price = CommUtil.null2String(obj.getPd_amount());
     }
     if (type.equals("gold")) {
       order_price = CommUtil.null2String(Integer.valueOf(gold.getGold_money()));
     }
     if (type.equals("integral")) {
       order_price = CommUtil.null2String(ig_order.getIgo_trans_fee());
     }
 
     double total_fee = CommUtil.null2Double(order_price) * 100.0D;
     int fee = (int)total_fee;
 
     String product_name = "";
     if (type.equals("goods")) {
       product_name = of.getOrder_id();
     }
     if (type.equals("cash")) {
       product_name = obj.getPd_sn();
     }
     if (type.equals("gold")) {
       product_name = gold.getGold_sn();
     }
     if (type.equals("integral")) {
       product_name = ig_order.getIgo_order_sn();
     }
 
     String remarkexplain = "";
     if (type.equals("goods")) {
       remarkexplain = of.getMsg();
     }
     if (type.equals("cash")) {
       remarkexplain = obj.getPd_remittance_info();
     }
     if (type.equals("gold")) {
       remarkexplain = gold.getGold_exchange_info();
     }
     if (type.equals("integral")) {
       remarkexplain = ig_order.getIgo_msg();
     }
     String attach = "";
     if (type.equals("goods")) {
       attach = type + "," + of.getId().toString();
     }
     if (type.equals("cash")) {
       attach = type + "," + obj.getId().toString();
     }
     if (type.equals("gold")) {
       attach = type + "," + gold.getId().toString();
     }
     if (type.equals("integral")) {
       attach = type + "," + ig_order.getId().toString();
     }
     String desc = "商品：" + product_name;
 
     String out_trade_no = "";
     if (type.equals("goods")) {
       out_trade_no = of.getOrder_id();
     }
     if (type.endsWith("cash")) {
       out_trade_no = obj.getPd_sn();
     }
     if (type.endsWith("gold")) {
       out_trade_no = gold.getGold_sn();
     }
     if (type.equals("integral")) {
       out_trade_no = ig_order.getIgo_order_sn();
     }
 
     Payment payment = this.paymentService.getObjById(
       CommUtil.null2Long(payment_id));
     if (payment == null) {
       payment = new Payment();
     }
     String trade_mode = CommUtil.null2String(Integer.valueOf(payment.getTrade_mode()));
     String currTime = TenpayUtil.getCurrTime();
 
     RequestHandler reqHandler = new RequestHandler(request, response);
     reqHandler.init();
 
     reqHandler.setKey(payment.getTenpay_key());
 
     reqHandler.setGateUrl("https://gw.tenpay.com/gateway/pay.htm");
 
     reqHandler.setParameter("partner", payment.getTenpay_partner());
     reqHandler.setParameter("out_trade_no", out_trade_no);
     reqHandler.setParameter("total_fee", String.valueOf(fee));
     reqHandler.setParameter("return_url", CommUtil.getURL(request) + 
       "/tenpay_return.htm");
     reqHandler.setParameter("notify_url", CommUtil.getURL(request) + 
       "/tenpay_notify.htm");
     reqHandler.setParameter("body", desc);
     reqHandler.setParameter("bank_type", "DEFAULT");
     reqHandler.setParameter("spbill_create_ip", request.getRemoteAddr());
     reqHandler.setParameter("fee_type", "1");
     reqHandler.setParameter("subject", desc);
 
     reqHandler.setParameter("sign_type", "MD5");
     reqHandler.setParameter("service_version", "1.0");
     reqHandler.setParameter("input_charset", "UTF-8");
     reqHandler.setParameter("sign_key_index", "1");
 
     reqHandler.setParameter("attach", attach);
     reqHandler.setParameter("product_fee", "");
     reqHandler.setParameter("transport_fee", "0");
     reqHandler.setParameter("time_start", currTime);
     reqHandler.setParameter("time_expire", "");
     reqHandler.setParameter("buyer_id", "");
     reqHandler.setParameter("goods_tag", "");
     reqHandler.setParameter("trade_mode", trade_mode);
     reqHandler.setParameter("transport_desc", "");
     reqHandler.setParameter("trans_type", "1");
     reqHandler.setParameter("agentid", "");
     reqHandler.setParameter("agent_type", "");
     reqHandler.setParameter("seller_id", "");
 
     String requestUrl = reqHandler.getRequestURL();
     response.sendRedirect(requestUrl);
   }
 
   @RequestMapping({"/tenpay_return.htm"})
   public ModelAndView tenpay_return(HttpServletRequest request, HttpServletResponse response)
     throws Exception
   {
     ModelAndView mv = new JModelAndView("order_finish.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     ResponseHandler resHandler = new ResponseHandler(request, response);
     String[] attachs = request.getParameter("attach").split(",");
 
     String out_trade_no = resHandler.getParameter("out_trade_no");
     OrderForm order = null;
     Predeposit obj = null;
     GoldRecord gold = null;
     IntegralGoodsOrder ig_order = null;
     if (attachs[0].equals("integral")) {
       ig_order = this.integralGoodsOrderService.getObjById(
         CommUtil.null2Long(attachs[1]));
       Map q_params = new HashMap();
       q_params.put("install", Boolean.valueOf(true));
       q_params.put("mark", ig_order.getIgo_payment());
       q_params.put("type", "admin");
       List payments = this.paymentService
         .query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type", 
         q_params, -1, -1);
       resHandler.setKey(((Payment)payments.get(0)).getTenpay_key());
     }
     if (attachs[0].equals("cash")) {
       obj = this.predepositService.getObjById(
         CommUtil.null2Long(attachs[1]));
       Map q_params = new HashMap();
       q_params.put("install", Boolean.valueOf(true));
       q_params.put("mark", obj.getPd_payment());
       q_params.put("type", "admin");
       List payments = this.paymentService
         .query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type", 
         q_params, -1, -1);
       resHandler.setKey(((Payment)payments.get(0)).getTenpay_key());
     }
     if (attachs[0].equals("gold")) {
       gold = this.goldRecordService.getObjById(
         CommUtil.null2Long(attachs[1]));
       Map q_params = new HashMap();
       q_params.put("install", Boolean.valueOf(true));
       q_params.put("mark", gold.getGold_payment());
       q_params.put("type", "admin");
       List payments = this.paymentService
         .query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type", 
         q_params, -1, -1);
       resHandler.setKey(((Payment)payments.get(0)).getTenpay_key());
     }
     if (attachs[0].equals("goods")) {
       order = this.orderFormService.getObjById(
         CommUtil.null2Long(attachs[1]));
       resHandler.setKey(order.getPayment().getTenpay_key());
     }
 
     if (resHandler.isTenpaySign())
     {
       String notify_id = resHandler.getParameter("notify_id");
 
       String transaction_id = resHandler.getParameter("transaction_id");
 
       String total_fee = resHandler.getParameter("total_fee");
 
       String discount = resHandler.getParameter("discount");
 
       String trade_state = resHandler.getParameter("trade_state");
 
       String trade_mode = resHandler.getParameter("trade_mode");
       if ("1".equals(trade_mode)) {
         if ("0".equals(trade_state))
         {
           if (attachs[0].equals("cash")) {
             obj.setPd_status(1);
             obj.setPd_pay_status(2);
             this.predepositService.update(obj);
             User user = this.userService.getObjById(obj
               .getPd_user().getId());
             user.setAvailableBalance(BigDecimal.valueOf(
               CommUtil.add(user.getAvailableBalance(), 
               obj.getPd_amount())));
             this.userService.update(user);
             PredepositLog log = new PredepositLog();
             log.setAddTime(new Date());
             log.setPd_log_amount(obj.getPd_amount());
             log.setPd_log_user(obj.getPd_user());
             log.setPd_op_type("充值");
             log.setPd_type("可用预存款");
             log.setPd_log_info("财付通及时到账");
             this.predepositLogService.save(log);
             mv = new JModelAndView("success.html", 
               this.configService.getSysConfig(), 
               this.userConfigService.getUserConfig(), 1, 
               request, response);
             mv.addObject("op_title", "充值" + obj.getPd_amount() + 
               "成功");
             mv.addObject("url", CommUtil.getURL(request) + 
               "/buyer/predeposit_list.htm");
           }
           if (attachs[0].equals("goods")) {
             order.setOrder_status(20);
             order.setPayTime(new Date());
             this.orderFormService.update(order);
             update_goods_inventory(order);
             OrderFormLog ofl = new OrderFormLog();
             ofl.setAddTime(new Date());
             ofl.setLog_info("财付通及时到账支付");
             ofl.setLog_user(SecurityUserHolder.getCurrentUser());
             ofl.setOf(order);
             this.orderFormLogService.save(ofl);
             mv.addObject("obj", order);
 
             if (this.configService.getSysConfig().isEmailEnable()) {
               send_order_email(request, order, order
                 .getUser().getEmail(), 
                 "email_tobuyer_online_pay_ok_notify");
               send_order_email(request, order, order
                 .getStore().getUser().getEmail(), 
                 "email_toseller_online_pay_ok_notify");
             }
 
             if (this.configService.getSysConfig().isSmsEnbale()) {
               send_order_sms(request, order, order.getUser()
                 .getMobile(), 
                 "sms_tobuyer_online_pay_ok_notify");
               send_order_sms(request, order, order
                 .getStore().getUser().getMobile(), 
                 "sms_toseller_online_pay_ok_notify");
             }
           }
           GoldLog log;
           if (attachs[0].equals("gold")) {
             gold.setGold_status(1);
             gold.setGold_pay_status(2);
             this.goldRecordService.update(gold);
             User user = this.userService.getObjById(gold
               .getGold_user().getId());
             user.setGold(user.getGold() + gold.getGold_count());
             this.userService.update(user);
             log = new GoldLog();
             log.setAddTime(new Date());
             log.setGl_payment(gold.getGold_payment());
             log.setGl_content("财付通及时到账支付");
             log.setGl_money(gold.getGold_money());
             log.setGl_count(gold.getGold_count());
             log.setGl_type(0);
             log.setGl_user(gold.getGold_user());
             log.setGr(gold);
             this.goldLogService.save(log);
             mv = new JModelAndView("success.html", 
               this.configService.getSysConfig(), 
               this.userConfigService.getUserConfig(), 1, 
               request, response);
             mv.addObject("op_title", "兑换" + gold.getGold_count() + 
               "金币成功");
             mv.addObject("url", CommUtil.getURL(request) + 
               "/seller/gold_record_list.htm");
           }
           if (attachs[0].equals("integral")) {
             ig_order.setIgo_status(20);
             ig_order.setIgo_pay_time(new Date());
             ig_order.setIgo_payment("bill");
             this.integralGoodsOrderService.update(ig_order);
             for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
               IntegralGoods goods = igc.getGoods();
               goods.setIg_goods_count(goods.getIg_goods_count() - 
                 igc.getCount());
               goods.setIg_exchange_count(goods
                 .getIg_exchange_count() + igc.getCount());
               this.integralGoodsService.update(goods);
             }
             mv = new JModelAndView("integral_order_finish.html", 
               this.configService.getSysConfig(), 
               this.userConfigService.getUserConfig(), 1, 
               request, response);
             mv.addObject("obj", ig_order);
           }
         } else {
           mv = new JModelAndView("error.html", 
             this.configService.getSysConfig(), 
             this.userConfigService.getUserConfig(), 1, request, 
             response);
           mv.addObject("op_title", "财付通支付失败！");
           mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         }
       } else if ("2".equals(trade_mode))
         if ("0".equals(trade_state))
         {
           if (attachs[0].equals("cash")) {
             obj.setPd_status(1);
             obj.setPd_pay_status(2);
             this.predepositService.update(obj);
             User user = this.userService.getObjById(obj
               .getPd_user().getId());
             user.setAvailableBalance(BigDecimal.valueOf(
               CommUtil.add(user.getAvailableBalance(), 
               obj.getPd_amount())));
             this.userService.update(user);
             PredepositLog log = new PredepositLog();
             log.setAddTime(new Date());
             log.setPd_log_amount(obj.getPd_amount());
             log.setPd_log_user(obj.getPd_user());
             log.setPd_op_type("充值");
             log.setPd_type("可用预存款");
             log.setPd_log_info("财付通中介担保付款");
             this.predepositLogService.save(log);
             mv = new JModelAndView("success.html", 
               this.configService.getSysConfig(), 
               this.userConfigService.getUserConfig(), 1, 
               request, response);
             mv.addObject("op_title", "充值" + obj.getPd_amount() + 
               "成功");
             mv.addObject("url", CommUtil.getURL(request) + 
               "/buyer/predeposit_list.htm");
           }
           if (attachs[0].equals("goods")) {
             order.setOrder_status(20);
             order.setPayTime(new Date());
             this.orderFormService.update(order);
             update_goods_inventory(order);
             OrderFormLog ofl = new OrderFormLog();
             ofl.setAddTime(new Date());
             ofl.setLog_info("财付通中介担保付款成功");
             ofl.setLog_user(SecurityUserHolder.getCurrentUser());
             ofl.setOf(order);
             this.orderFormLogService.save(ofl);
             mv.addObject("obj", order);
 
             if (this.configService.getSysConfig().isEmailEnable()) {
               send_order_email(request, order, order
                 .getUser().getEmail(), 
                 "email_tobuyer_online_pay_ok_notify");
               send_order_email(request, order, order
                 .getStore().getUser().getEmail(), 
                 "email_toseller_online_pay_ok_notify");
             }
 
             if (this.configService.getSysConfig().isSmsEnbale()) {
               send_order_sms(request, order, order.getUser()
                 .getMobile(), 
                 "sms_tobuyer_online_pay_ok_notify");
               send_order_sms(request, order, order
                 .getStore().getUser().getMobile(), 
                 "sms_toseller_online_pay_ok_notify");
             }
           }
           GoldLog log;
           if (attachs[0].equals("gold")) {
             gold.setGold_status(1);
             gold.setGold_pay_status(2);
             this.goldRecordService.update(gold);
             User user = this.userService.getObjById(gold
               .getGold_user().getId());
             user.setGold(user.getGold() + gold.getGold_count());
             this.userService.update(user);
             log = new GoldLog();
             log.setAddTime(new Date());
             log.setGl_payment(gold.getGold_payment());
             log.setGl_content("财付通中介担保付款成功");
             log.setGl_money(gold.getGold_money());
             log.setGl_count(gold.getGold_count());
             log.setGl_type(0);
             log.setGl_user(gold.getGold_user());
             log.setGr(gold);
             this.goldLogService.save(log);
             mv = new JModelAndView("success.html", 
               this.configService.getSysConfig(), 
               this.userConfigService.getUserConfig(), 1, 
               request, response);
             mv.addObject("op_title", "兑换" + gold.getGold_count() + 
               "金币成功");
             mv.addObject("url", CommUtil.getURL(request) + 
               "/seller/gold_record_list.htm");
           }
           if (attachs[0].equals("integral")) {
             ig_order.setIgo_status(20);
             ig_order.setIgo_pay_time(new Date());
             ig_order.setIgo_payment("bill");
             this.integralGoodsOrderService.update(ig_order);
             for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
               IntegralGoods goods = igc.getGoods();
               goods.setIg_goods_count(goods.getIg_goods_count() - 
                 igc.getCount());
               goods.setIg_exchange_count(goods
                 .getIg_exchange_count() + igc.getCount());
               this.integralGoodsService.update(goods);
             }
             mv = new JModelAndView("integral_order_finish.html", 
               this.configService.getSysConfig(), 
               this.userConfigService.getUserConfig(), 1, 
               request, response);
             mv.addObject("obj", ig_order);
           }
         } else {
           mv = new JModelAndView("error.html", 
             this.configService.getSysConfig(), 
             this.userConfigService.getUserConfig(), 1, request, 
             response);
           mv.addObject("op_title", "财付通支付失败！");
           mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         }
     }
     else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "财付通认证签名失败！");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
 
   @RequestMapping({"/chinabank_return.htm"})
   public ModelAndView chinabank_return(HttpServletRequest request, HttpServletResponse response)
     throws Exception
   {
     ModelAndView mv = new JModelAndView("order_finish.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     String remark1 = request.getParameter("remark1");
     String remark2 = CommUtil.null2String(request.getParameter("remark2"));
     OrderForm order = null;
     Predeposit obj = null;
     GoldRecord gold = null;
     IntegralGoodsOrder ig_order = null;
     if (remark2.equals("goods")) {
       order = this.orderFormService.getObjById(CommUtil.null2Long(remark1
         .trim()));
     }
     if (remark2.equals("cash")) {
       obj = this.predepositService
         .getObjById(CommUtil.null2Long(remark1));
     }
     if (remark2.equals("gold")) {
       gold = this.goldRecordService.getObjById(
         CommUtil.null2Long(remark1));
     }
     if (remark2.equals("integral")) {
       ig_order = this.integralGoodsOrderService.getObjById(
         CommUtil.null2Long(remark1));
     }
     String key = "";
     if (remark2.equals("goods")) {
       key = order.getPayment().getChinabank_key();
     }
     if ((remark2.equals("cash")) || (remark2.equals("gold")) || 
       (remark2.equals("integral"))) {
       Map q_params = new HashMap();
       q_params.put("install", Boolean.valueOf(true));
       if (remark2.equals("cash")) {
         q_params.put("mark", obj.getPd_payment());
       }
       if (remark2.equals("gold")) {
         q_params.put("mark", gold.getGold_payment());
       }
       if (remark2.equals("integral")) {
         q_params.put("mark", ig_order.getIgo_payment());
       }
       q_params.put("type", "admin");
       List payments = this.paymentService
         .query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type", 
         q_params, -1, -1);
       key = ((Payment)payments.get(0)).getChinabank_key();
     }
     String v_oid = request.getParameter("v_oid");
     String v_pmode = request.getParameter("v_pmode");
 
     String v_pstatus = request.getParameter("v_pstatus");
     String v_pstring = request.getParameter("v_pstring");
 
     String v_amount = request.getParameter("v_amount");
     String v_moneytype = request.getParameter("v_moneytype");
     String v_md5str = request.getParameter("v_md5str");
     String text = v_oid + v_pstatus + v_amount + v_moneytype + key;
     String v_md5text = Md5Encrypt.md5(text).toUpperCase();
     if (v_md5str.equals(v_md5text)) {
       if ("20".equals(v_pstatus))
       {
         if (remark2.equals("goods")) {
           order.setOrder_status(20);
           order.setPayTime(new Date());
           this.orderFormService.update(order);
           update_goods_inventory(order);
           OrderFormLog ofl = new OrderFormLog();
           ofl.setAddTime(new Date());
           ofl.setLog_info("网银在线支付");
           ofl.setLog_user(SecurityUserHolder.getCurrentUser());
           ofl.setOf(order);
           this.orderFormLogService.save(ofl);
           mv.addObject("obj", order);
 
           if (this.configService.getSysConfig().isEmailEnable()) {
             send_order_email(request, order, order.getUser()
               .getEmail(), 
               "email_tobuyer_online_pay_ok_notify");
             send_order_email(request, order, order.getStore()
               .getUser().getEmail(), 
               "email_toseller_online_pay_ok_notify");
           }
 
           if (this.configService.getSysConfig().isSmsEnbale()) {
             send_order_sms(request, order, order.getUser()
               .getMobile(), 
               "sms_tobuyer_online_pay_ok_notify");
             send_order_sms(request, order, order.getStore()
               .getUser().getMobile(), 
               "sms_toseller_online_pay_ok_notify");
           }
         }
         if (remark2.endsWith("cash")) {
           obj.setPd_status(1);
           obj.setPd_pay_status(2);
           this.predepositService.update(obj);
           User user = this.userService.getObjById(obj.getPd_user()
             .getId());
           user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
             user.getAvailableBalance(), obj.getPd_amount())));
           this.userService.update(user);
           PredepositLog log = new PredepositLog();
           log.setAddTime(new Date());
           log.setPd_log_amount(obj.getPd_amount());
           log.setPd_log_user(obj.getPd_user());
           log.setPd_op_type("充值");
           log.setPd_type("可用预存款");
           log.setPd_log_info("网银在线支付");
           this.predepositLogService.save(log);
           mv = new JModelAndView("success.html", 
             this.configService.getSysConfig(), 
             this.userConfigService.getUserConfig(), 1, request, 
             response);
           mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
           mv.addObject("url", CommUtil.getURL(request) + 
             "/buyer/predeposit_list.htm");
         }
         GoldLog log;
         if (remark2.equals("gold")) {
           gold.setGold_status(1);
           gold.setGold_pay_status(2);
           this.goldRecordService.update(gold);
           User user = this.userService.getObjById(gold.getGold_user()
             .getId());
           user.setGold(user.getGold() + gold.getGold_count());
           this.userService.update(user);
           log = new GoldLog();
           log.setAddTime(new Date());
           log.setGl_payment(gold.getGold_payment());
           log.setGl_content("网银在线支付");
           log.setGl_money(gold.getGold_money());
           log.setGl_count(gold.getGold_count());
           log.setGl_type(0);
           log.setGl_user(gold.getGold_user());
           log.setGr(gold);
           this.goldLogService.save(log);
           mv = new JModelAndView("success.html", 
             this.configService.getSysConfig(), 
             this.userConfigService.getUserConfig(), 1, request, 
             response);
           mv.addObject("op_title", "兑换" + gold.getGold_count() + 
             "金币成功");
           mv.addObject("url", CommUtil.getURL(request) + 
             "/seller/gold_record_list.htm");
         }
         if (remark2.equals("gold")) {
           ig_order.setIgo_status(20);
           ig_order.setIgo_pay_time(new Date());
           ig_order.setIgo_payment("bill");
           this.integralGoodsOrderService.update(ig_order);
           for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
             IntegralGoods goods = igc.getGoods();
             goods.setIg_goods_count(goods.getIg_goods_count() - 
               igc.getCount());
             goods.setIg_exchange_count(goods.getIg_exchange_count() + 
               igc.getCount());
             this.integralGoodsService.update(goods);
           }
           mv = new JModelAndView("integral_order_finish.html", 
             this.configService.getSysConfig(), 
             this.userConfigService.getUserConfig(), 1, request, 
             response);
           mv.addObject("obj", ig_order);
         }
       }
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "网银在线支付失败！");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
 
     return mv;
   }
 
   @RequestMapping({"/paypal_return.htm"})
   public ModelAndView paypal_return(HttpServletRequest request, HttpServletResponse response)
     throws Exception
   {
     ModelAndView mv = new JModelAndView("order_finish.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     Enumeration en = request.getParameterNames();
     String str = "cmd=_notify-validate";
     while (en.hasMoreElements()) {
       String paramName = (String)en.nextElement();
       String paramValue = request.getParameter(paramName);
       str = str + "&" + paramName + "=" + URLEncoder.encode(paramValue);
     }
     String[] customs = CommUtil.null2String(request.getParameter("custom"))
       .split(",");
     String remark1 = customs[0];
     String remark2 = customs[1];
     String item_name = request.getParameter("item_name");
     String txnId = request.getParameter("txn_id");
     OrderForm order = null;
     Predeposit obj = null;
     GoldRecord gold = null;
     IntegralGoodsOrder ig_order = null;
     if (remark2.equals("goods")) {
       order = this.orderFormService.getObjById(CommUtil.null2Long(remark1
         .trim()));
     }
     if (remark2.equals("cash")) {
       obj = this.predepositService
         .getObjById(CommUtil.null2Long(remark1));
     }
     if (remark2.equals("gold")) {
       gold = this.goldRecordService.getObjById(
         CommUtil.null2Long(remark1));
     }
     if (remark2.equals("integral")) {
       ig_order = this.integralGoodsOrderService.getObjById(
         CommUtil.null2Long(remark1));
     }
     String txn_id = request.getParameter("txn_id");
 
     String itemName = request.getParameter("item_name");
     String paymentStatus = request.getParameter("payment_status");
     String paymentAmount = request.getParameter("mc_gross");
     String paymentCurrency = request.getParameter("mc_currency");
     String receiverEmail = request.getParameter("receiver_email");
     String payerEmail = request.getParameter("payer_email");
 
     if ((paymentStatus.equals("Completed")) || 
       (paymentStatus.equals("Pending"))) {
       if (remark2.equals("goods")) {
         order.setOrder_status(20);
         order.setPayTime(new Date());
         this.orderFormService.update(order);
         update_goods_inventory(order);
         OrderFormLog ofl = new OrderFormLog();
         ofl.setAddTime(new Date());
         ofl.setLog_info("Paypal在线支付");
         ofl.setLog_user(SecurityUserHolder.getCurrentUser());
         ofl.setOf(order);
         this.orderFormLogService.save(ofl);
         mv.addObject("obj", order);
 
         if (this.configService.getSysConfig().isEmailEnable()) {
           send_order_email(request, order, order.getUser()
             .getEmail(), "email_tobuyer_online_pay_ok_notify");
           send_order_email(request, order, order.getStore()
             .getUser().getEmail(), 
             "email_toseller_online_pay_ok_notify");
         }
 
         if (this.configService.getSysConfig().isSmsEnbale()) {
           send_order_sms(request, order, order.getUser()
             .getMobile(), "sms_tobuyer_online_pay_ok_notify");
           send_order_sms(request, order, order.getStore()
             .getUser().getMobile(), 
             "sms_toseller_online_pay_ok_notify");
         }
       }
       if (remark2.endsWith("cash")) {
         obj.setPd_status(1);
         obj.setPd_pay_status(2);
         this.predepositService.update(obj);
         User user = this.userService.getObjById(obj.getPd_user()
           .getId());
         user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
           user.getAvailableBalance(), obj.getPd_amount())));
         this.userService.update(user);
         PredepositLog log = new PredepositLog();
         log.setAddTime(new Date());
         log.setPd_log_amount(obj.getPd_amount());
         log.setPd_log_user(obj.getPd_user());
         log.setPd_op_type("充值");
         log.setPd_type("可用预存款");
         log.setPd_log_info("Paypal在线支付");
         this.predepositLogService.save(log);
         mv = new JModelAndView("success.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "成功充值：" + obj.getPd_amount());
         mv.addObject("url", CommUtil.getURL(request) + 
           "/buyer/predeposit_list.htm");
       }
       GoldLog log;
       if (remark2.equals("gold")) {
         gold.setGold_status(1);
         gold.setGold_pay_status(2);
         this.goldRecordService.update(gold);
         User user = this.userService.getObjById(gold.getGold_user()
           .getId());
         user.setGold(user.getGold() + gold.getGold_count());
         this.userService.update(user);
         log = new GoldLog();
         log.setAddTime(new Date());
         log.setGl_payment(gold.getGold_payment());
         log.setGl_content("Paypal");
         log.setGl_money(gold.getGold_money());
         log.setGl_count(gold.getGold_count());
         log.setGl_type(0);
         log.setGl_user(gold.getGold_user());
         log.setGr(gold);
         this.goldLogService.save(log);
         mv = new JModelAndView("success.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "成功充值金币:" + gold.getGold_count());
         mv.addObject("url", CommUtil.getURL(request) + 
           "/seller/gold_record_list.htm");
       }
       if (remark2.equals("gold")) {
         ig_order.setIgo_status(20);
         ig_order.setIgo_pay_time(new Date());
         ig_order.setIgo_payment("paypal");
         this.integralGoodsOrderService.update(ig_order);
         for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
           IntegralGoods goods = igc.getGoods();
           goods.setIg_goods_count(goods.getIg_goods_count() - 
             igc.getCount());
           goods.setIg_exchange_count(goods.getIg_exchange_count() + 
             igc.getCount());
           this.integralGoodsService.update(goods);
         }
         mv = new JModelAndView("integral_order_finish.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("obj", ig_order);
       }
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "Paypal支付失败");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
   
   /***
    * 付款成功回调处理，你必须要返回SUCCESS信息给微信服务器，告诉微信服务器我已经收到支付成功的后台通知了。
    * 不然的话，微信会一直调用该回调地址，当达到8次的时候还是没有收到SUCCESS的返回，微信服务器则认为此订单支付失败。
    *
    * 该回调地址是异步的。 这里可以处理数据库中的订单状态。
    *
    * 作者: YUKE 日期：2016年1月14日 上午9:25:29
    *
    * @param response
    * @throws IOException
    * @throws JDOMException
    */
   @RequestMapping({"/wechat/paynotify.htm"})
   public void notify_success(HttpServletRequest request, HttpServletResponse response) throws IOException, JDOMException {
       
	   InputStream inStream = request.getInputStream();
       ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
       byte[] buffer = new byte[1024];
       int len = 0;
       while ((len = inStream.read(buffer)) != -1) {
           outSteam.write(buffer, 0, len);
       }
       logger.info("~~~~~~~~~~~~~~~~付款成功，收到微信回调~~~~~~~~~");
       outSteam.close();
       inStream.close();

       /** 支付成功后，微信回调返回的信息 */
       String result = new String(outSteam.toByteArray(), "utf-8");
       Map<String, String> map = WxCommonUtil.doXMLParse(result);
       
       for (Object keyValue : map.keySet()) {
           /** 输出返回的订单支付信息 */
           logger.info(keyValue + "=" + map.get(keyValue));
       }
       //支付签名验证
       /*if (verifyNotify(notifyMethod, request)) {

		}*/
       String order_no = map.get("out_trade_no");
       OrderForm order = null;
       order = this.orderFormService.getObjById(CommUtil.null2Long(order_no));
       
       if (map.get("result_code").toString().equalsIgnoreCase("SUCCESS") && (order.getOrder_status() < 20)) {
               order.setOrder_status(20);
               order.setOut_order_id(map.get("transaction_id"));
               order.setPayTime(new Date());
               this.orderFormService.update(order);
       
               update_goods_inventory(order);
               OrderFormLog ofl = new OrderFormLog();
               ofl.setAddTime(new Date());
               ofl.setLog_info("微信公众号在线支付");
               ofl.setLog_user(order.getUser());
               ofl.setOf(order);
               this.orderFormLogService.save(ofl);
       
               // 邮件和短信通知,根据情况使用
               /*if (this.configService.getSysConfig().isEmailEnable()) {
                 send_order_email(request, order, order.getUser().getEmail(), 
                   "email_tobuyer_online_pay_ok_notify");
                 send_order_email(request, order, order
                   .getStore().getUser().getEmail(), 
                   "email_toseller_online_pay_ok_notify");
               }
               if (this.configService.getSysConfig().isSmsEnbale()) {
                 send_order_sms(request, order, order.getUser().getMobile(), 
                   "sms_tobuyer_online_pay_ok_notify");
                 send_order_sms(request, order, order
                   .getStore().getUser().getMobile(), 
                   "sms_toseller_online_pay_ok_notify");
               }*/
      
           // 告诉微信服务器，我收到信息了，不要在调用回调方法(/pay)了
           logger.info("-------------" + WxCommonUtil.setXML("SUCCESS", "OK"));
           response.getWriter().write(WxCommonUtil.setXML("SUCCESS", "OK"));
       } else {
    	   logger.info("------微信异步回调失败-------");
       }
   }
   
   /**
    * 微信公众号支付前台页面回调
	* @param request
	* @param response
	* @return
	*/
   @SecurityMapping(display = false, rsequence = 0, title = "微信前台回调", value = "/wechat/paysuccess.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
   @RequestMapping({"/wechat/paysuccess.htm"})
   public ModelAndView wxpaySuccess(HttpServletRequest request, HttpServletResponse response, String totalPrice) {
	    ModelAndView mv = new JModelAndView("wap/paysuccess.html", this.configService.getSysConfig(), 
	       this.userConfigService.getUserConfig(), 1, request, response);
	    mv.addObject("totalPrice",totalPrice);
	    return mv;
   }
   
    /**
	 * 支付宝后台台回调
	 */
	@SecurityMapping(display = false, rsequence = 0, title = "支付宝后台回调", value = "/alipay/alipay_notify.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/alipay/alipay_notify.htm" })
	public void alipayNotify(HttpServletRequest request, HttpServletResponse respons) throws Exception {
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		System.out.println(request.getQueryString());
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				//valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i];
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		// 商户订单号
		String out_trade_no = request.getParameter("out_trade_no");

		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(out_trade_no));

		// 支付宝交易号
		String trade_no = request.getParameter("trade_no");

		// 交易状态
		String trade_status = request.getParameter("trade_status");

		AlipayConfig config = new AlipayConfig();
	    
        config.setKey(order.getPayment().getSafeKey());
        config.setPartner(order.getPayment().getPartner());
        config.setSeller_email(order.getPayment().getSeller_email());
        
        config.setTransport("https");
	     
	    config.setNotify_url(CommUtil.getURL(request, this.configService.getSysConfig()) + "/alipay_notify.htm");
	    config.setReturn_url(CommUtil.getURL(request, this.configService.getSysConfig()) + "/aplipay_return.htm");

	    boolean check = AlipayNotify.verifyWap(config,params);
	    check = true;
		if (check) {// 验证成功
			
			if (((trade_status.equals("WAIT_SELLER_SEND_GOODS")) || 
		         (trade_status.equals("TRADE_FINISHED")) || 
		         (trade_status.equals("TRADE_SUCCESS"))) && (order.getOrder_status() < 20)) {
		         order.setOrder_status(20);
		         order.setOut_order_id(trade_no);
		         order.setPayTime(new Date());
		         this.orderFormService.update(order);
		 
		         update_goods_inventory(order);
		         OrderFormLog ofl = new OrderFormLog();
		         ofl.setAddTime(new Date());
		         ofl.setLog_info("支付宝wap在线支付");
		         ofl.setLog_user(order.getUser());
		         ofl.setOf(order);
		         this.orderFormLogService.save(ofl);
		 
		         if (this.configService.getSysConfig().isEmailEnable()) {
		           send_order_email(request, order, order.getUser().getEmail(), "email_tobuyer_online_pay_ok_notify");
		           send_order_email(request, order, order.getStore().getUser().getEmail(), "email_toseller_online_pay_ok_notify");
		         }
		 
		         if (this.configService.getSysConfig().isSmsEnbale()) {
		           send_order_sms(request, order, order.getUser().getMobile(), "sms_tobuyer_online_pay_ok_notify");
		           send_order_sms(request, order, order.getStore().getUser().getMobile(), "sms_toseller_online_pay_ok_notify");
		         }
			}
			try {
				respons.getWriter().println("success");
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("返回支付宝发送错误");
			}
			
		} else {// 验证失败
			logger.error("支付宝验证失败");
			try {
				respons.getWriter().println("fail");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 支付宝前台回调
	 */
	@SecurityMapping(display = false, rsequence = 0, title = "支付宝前台回调", value = "/alipay_retrun.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/alipay/alipay_retrun.htm" })
	public void alipayRetrun(HttpServletRequest request, HttpServletResponse respons) {
		// 获取支付宝GET过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}

		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		// 商户订单号
		// 商户订单号
		String out_trade_no = request.getParameter("out_trade_no");
		
		logger.info("-----------------支付宝回调-----------------out_trade_no="+out_trade_no);

		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(out_trade_no));
		
		// 支付宝交易号
		String trade_no = request.getParameter("trade_no");

		// 交易状态
		String trade_status = request.getParameter("trade_status");


		AlipayConfig config = new AlipayConfig();
		
		//通过RSA校验,可以注释掉，通md5的校验打开
        //config.setSeller_email(order.getPayment().getSeller_email());
        //config.setKey(order.getPayment().getSafeKey());
        config.setPartner(order.getPayment().getPartner());
	     
	     
	    config.setNotify_url(CommUtil.getURL(request, this.configService.getSysConfig()) + "/alipay/alipay_notify.htm");
	    config.setReturn_url(CommUtil.getURL(request, this.configService.getSysConfig()) + "/alipay/aplipay_return.htm");


		// 计算得出通知验证结果
		//boolean verify_result = AlipayNotify.verify(config, params);
	    boolean verify_result = true;

		if (verify_result) {// 验证成功

			if (trade_status.equals("TRADE_FINISHED")
					|| trade_status.equals("TRADE_SUCCESS")) {
				
				try {
					respons.sendRedirect(CommUtil.getURL(request, this.configService.getSysConfig()) + "/alipay/paysuccess.htm");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} else {
			// 该页面可做页面美工编辑
			try {
				respons.getWriter().println("验证失败");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

   /**
    * 支付宝前台页面回调显示
	* @param request
	* @param response
	* @return
	*/
   @SecurityMapping(display = false, rsequence = 0, title = "微信前台回调", value = "/alipay/paysuccess.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
   @RequestMapping({"/alipay/paysuccess.htm"})
   public ModelAndView account_password(HttpServletRequest request, HttpServletResponse response) {
	    ModelAndView mv = new JModelAndView("alipaysuccess.html", this.configService.getSysConfig(), 
	       this.userConfigService.getUserConfig(), 1, request, response);
	    //mv.addObject("totalPrice",totalPrice);
	    return mv;
   }
   
   /**
    * 更新库存
    * @param order
    */
   private void update_goods_inventory(OrderForm order)
   {
     for (GoodsCart gc : order.getGcs()) {
       Goods goods = gc.getGoods();
       if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
         for (GroupGoods gg : goods.getGroup_goods_list()) {
           if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
             gg.setGg_def_count(gg.getGg_def_count() + gc.getCount());
             gg.setGg_count(gg.getGg_count() - gc.getCount());
             this.groupGoodsService.update(gg);
           }
         }
       }
       List gsps = new ArrayList();
       for (GoodsSpecProperty gsp : gc.getGsps()) {
         gsps.add(gsp.getId().toString());
       }
       String[] gsp_list = new String[gsps.size()];
       gsps.toArray(gsp_list);
       goods.setGoods_salenum(goods.getGoods_salenum() + gc.getCount());
       String inventory_type = goods.getInventory_type() == null ? "all" : goods.getInventory_type();
       Map temp;
       if (inventory_type.equals("all")) {
         goods.setGoods_inventory(goods.getGoods_inventory() - gc.getCount());
       } else {
         List list = (List)Json.fromJson(ArrayList.class, CommUtil.null2String(goods.getGoods_inventory_detail()));
         for (Iterator localIterator4 = list.iterator(); localIterator4.hasNext(); ) { temp = (Map)localIterator4.next();
           String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
           Arrays.sort(temp_ids);
           Arrays.sort(gsp_list);
           if (Arrays.equals(temp_ids, gsp_list)) {
             temp.put("count", Integer.valueOf(CommUtil.null2Int(temp.get("count")) - gc.getCount()));
           }
         }
         goods.setGoods_inventory_detail(Json.toJson(list, JsonFormat.compact()));
       }
       for (GroupGoods gg : goods.getGroup_goods_list()) {
         if ((!gg.getGroup().getId().equals(goods.getGroup().getId())) || (gg.getGg_count() != 0)) 
        	 continue;
         goods.setGroup_buy(3);
       }
 
       this.goodsService.update(goods);
     }
   }
 
   private void send_order_email(HttpServletRequest request, OrderForm order, String email, String mark) throws Exception
   {
     com.shopping.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
     if ((template != null) && (template.isOpen())) {
       String subject = template.getTitle();
       String path = request.getSession().getServletContext().getRealPath("/") + "/vm/";
       PrintWriter pwrite = new PrintWriter(
         new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
       pwrite.print(template.getContent());
       pwrite.flush();
       pwrite.close();
 
       Properties p = new Properties();
       p.setProperty("file.resource.loader.path", 
         request.getRealPath("/") + "vm" + File.separator);
       p.setProperty("input.encoding", "UTF-8");
       p.setProperty("output.encoding", "UTF-8");
       Velocity.init(p);
       org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", 
         "UTF-8");
       VelocityContext context = new VelocityContext();
       context.put("buyer", order.getUser());
       context.put("seller", order.getStore().getUser());
       context.put("config", this.configService.getSysConfig());
       context.put("send_time", CommUtil.formatLongDate(new Date()));
       context.put("webPath", CommUtil.getURL(request));
       context.put("order", order);
       StringWriter writer = new StringWriter();
       blank.merge(context, writer);
 
       String content = writer.toString();
       this.msgTools.sendEmail(email, subject, content);
     }
   }
 
   private void send_order_sms(HttpServletRequest request, OrderForm order, String mobile, String mark) throws Exception
   {
     com.shopping.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
     if ((template != null) && (template.isOpen())) {
       String path = request.getSession().getServletContext()
         .getRealPath("/") + 
         "/vm/";
       PrintWriter pwrite = new PrintWriter(
         new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
       pwrite.print(template.getContent());
       pwrite.flush();
       pwrite.close();
 
       Properties p = new Properties();
       p.setProperty("file.resource.loader.path", 
         request.getRealPath("/") + "vm" + File.separator);
       p.setProperty("input.encoding", "UTF-8");
       p.setProperty("output.encoding", "UTF-8");
       Velocity.init(p);
       org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", 
         "UTF-8");
       VelocityContext context = new VelocityContext();
       context.put("buyer", order.getUser());
       context.put("seller", order.getStore().getUser());
       context.put("config", this.configService.getSysConfig());
       context.put("send_time", CommUtil.formatLongDate(new Date()));
       context.put("webPath", CommUtil.getURL(request));
       context.put("order", order);
       StringWriter writer = new StringWriter();
       blank.merge(context, writer);
 
       String content = writer.toString();
       this.msgTools.sendSMS(mobile, content);
     }
   }
 }


 
 
 