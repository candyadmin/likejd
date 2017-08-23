 package com.shopping.manage.seller.action;
 
 import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.annotation.SecurityMapping;
import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.Evaluate;
import com.shopping.foundation.domain.ExpressCompany;
import com.shopping.foundation.domain.Goods;
import com.shopping.foundation.domain.GoodsCart;
import com.shopping.foundation.domain.GoodsReturn;
import com.shopping.foundation.domain.GoodsReturnItem;
import com.shopping.foundation.domain.GoodsReturnLog;
import com.shopping.foundation.domain.GoodsSpecProperty;
import com.shopping.foundation.domain.GroupGoods;
import com.shopping.foundation.domain.IntegralLog;
import com.shopping.foundation.domain.OrderForm;
import com.shopping.foundation.domain.OrderFormLog;
import com.shopping.foundation.domain.Payment;
import com.shopping.foundation.domain.RefundLog;
import com.shopping.foundation.domain.User;
import com.shopping.foundation.domain.query.OrderFormQueryObject;
import com.shopping.foundation.domain.virtual.TransInfo;
import com.shopping.foundation.service.IEvaluateService;
import com.shopping.foundation.service.IExpressCompanyService;
import com.shopping.foundation.service.IGoodsCartService;
import com.shopping.foundation.service.IGoodsReturnItemService;
import com.shopping.foundation.service.IGoodsReturnLogService;
import com.shopping.foundation.service.IGoodsReturnService;
import com.shopping.foundation.service.IGoodsService;
import com.shopping.foundation.service.IGroupGoodsService;
import com.shopping.foundation.service.IIntegralLogService;
import com.shopping.foundation.service.IOrderFormLogService;
import com.shopping.foundation.service.IOrderFormService;
import com.shopping.foundation.service.IPaymentService;
import com.shopping.foundation.service.IRefundLogService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.ITemplateService;
import com.shopping.foundation.service.IUserConfigService;
import com.shopping.foundation.service.IUserService;
import com.shopping.manage.admin.tools.MsgTools;
import com.shopping.manage.admin.tools.PaymentTools;
import com.shopping.pay.alipay.config.AlipayConfig;
import com.shopping.pay.alipay.util.AlipaySubmit;
import com.shopping.view.web.tools.StoreViewTools;
 
 @Controller
 public class OrderSellerAction
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
   private IRefundLogService refundLogService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IGoodsReturnService goodsReturnService;
 
   @Autowired
   private IGoodsReturnItemService goodsReturnItemService;
 
   @Autowired
   private IGoodsReturnLogService goodsReturnLogService;
 
   @Autowired
   private IGoodsCartService goodsCartService;
 
   @Autowired
   private IEvaluateService evaluateService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IIntegralLogService integralLogService;
 
   @Autowired
   private IGroupGoodsService groupGoodsService;
 
   @Autowired
   private ITemplateService templateService;
 
   @Autowired
   private IPaymentService paymentService;
 
   @Autowired
   private IExpressCompanyService expressCompayService;
 
   @Autowired
   private StoreViewTools storeViewTools;
 
   @Autowired
   private MsgTools msgTools;
 
   @Autowired
   private PaymentTools paymentTools;
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家订单列表", value="/seller/order.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order.htm"})
   public ModelAndView order(HttpServletRequest request, HttpServletResponse response, String currentPage, String order_status, String order_id, String beginTime, String endTime, String buyer_userName)
   {
     ModelAndView mv = mv = new JModelAndView(
       "user/default/usercenter/seller_order.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, 
       "addTime", "desc");
     ofqo.addQuery("obj.store.user.id", 
       new SysMap("user_id", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     if (!CommUtil.null2String(order_status).equals("")) {
       if (order_status.equals("order_submit")) {
         ofqo.addQuery("obj.order_status", 
           new SysMap("order_status", Integer.valueOf(10)), "=");
       }
       if (order_status.equals("order_pay")) {
         ofqo.addQuery("obj.order_status", 
           new SysMap("order_status", Integer.valueOf(20)), "=");
       }
       if (order_status.equals("order_shipping")) {
         ofqo.addQuery("obj.order_status", 
           new SysMap("order_status", Integer.valueOf(30)), "=");
       }
       if (order_status.equals("order_receive")) {
         ofqo.addQuery("obj.order_status", 
           new SysMap("order_status", Integer.valueOf(40)), "=");
       }
       if (order_status.equals("order_evaluate")) {
         ofqo.addQuery("obj.order_status", 
           new SysMap("order_status", Integer.valueOf(50)), "=");
       }
       if (order_status.equals("order_finish")) {
         ofqo.addQuery("obj.order_status", 
           new SysMap("order_status", Integer.valueOf(60)), "=");
       }
       if (order_status.equals("order_cancel")) {
         ofqo.addQuery("obj.order_status", 
           new SysMap("order_status", Integer.valueOf(0)), "=");
       }
     }
     if (!CommUtil.null2String(order_id).equals("")) {
       ofqo.addQuery("obj.order_id", 
         new SysMap("order_id", "%" + order_id + 
         "%"), "like");
     }
     if (!CommUtil.null2String(beginTime).equals("")) {
       ofqo.addQuery("obj.addTime", 
         new SysMap("beginTime", CommUtil.formatDate(beginTime)), 
         ">=");
     }
     if (!CommUtil.null2String(endTime).equals("")) {
       ofqo.addQuery("obj.addTime", 
         new SysMap("endTime", CommUtil.formatDate(endTime)), ">=");
     }
     if (!CommUtil.null2String(buyer_userName).equals("")) {
       ofqo.addQuery("obj.user.userName", 
         new SysMap("userName", 
         buyer_userName), "=");
     }
     IPageList pList = this.orderFormService.list(ofqo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("storeViewTools", this.storeViewTools);
     mv.addObject("order_id", order_id);
     mv.addObject("order_status", order_status == null ? "all" : 
       order_status);
     mv.addObject("beginTime", beginTime);
     mv.addObject("endTime", endTime);
     mv.addObject("buyer_userName", buyer_userName);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家订单详情", value="/seller/order_view.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_view.htm"})
   public ModelAndView order_view(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/order_view.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       mv.addObject("obj", obj);
       TransInfo transInfo = query_ship_getData(
         CommUtil.null2String(obj.getId()));
       mv.addObject("transInfo", transInfo);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家取消订单", value="/seller/order_cancel.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_cancel.htm"})
   public ModelAndView order_cancel(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/seller_order_cancel.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       mv.addObject("obj", obj);
       mv.addObject("currentPage", currentPage);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家取消订单保存", value="/seller/order_cancel_save.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_cancel_save.htm"})
   public String order_cancel_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String state_info, String other_state_info) throws Exception {
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       obj.setOrder_status(0);
       this.orderFormService.update(obj);
       OrderFormLog ofl = new OrderFormLog();
       ofl.setAddTime(new Date());
       ofl.setLog_info("取消订单");
       ofl.setLog_user(SecurityUserHolder.getCurrentUser());
       ofl.setOf(obj);
       if (state_info.equals("other"))
         ofl.setState_info(other_state_info);
       else {
         ofl.setState_info(state_info);
       }
       this.orderFormLogService.save(ofl);
       if (this.configService.getSysConfig().isEmailEnable()) {
         send_email(request, obj, 
           "email_tobuyer_order_cancel_notify");
       }
       if (this.configService.getSysConfig().isSmsEnbale()) {
         send_sms(request, obj, obj.getUser().getMobile(), 
           "sms_tobuyer_order_cancel_notify");
       }
     }
     return "redirect:order.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家调整订单费用", value="/seller/order_fee.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_fee.htm"})
   public ModelAndView order_fee(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/seller_order_fee.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       mv.addObject("obj", obj);
       mv.addObject("currentPage", currentPage);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家调整订单费用保存", value="/seller/order_fee_save.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_fee_save.htm"})
   public String order_fee_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String goods_amount, String ship_price, String totalPrice) throws Exception
   {
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       obj.setGoods_amount(BigDecimal.valueOf(
         CommUtil.null2Double(goods_amount)));
       obj.setShip_price(BigDecimal.valueOf(
         CommUtil.null2Double(ship_price)));
       obj.setTotalPrice(BigDecimal.valueOf(
         CommUtil.null2Double(totalPrice)));
       this.orderFormService.update(obj);
       OrderFormLog ofl = new OrderFormLog();
       ofl.setAddTime(new Date());
       ofl.setLog_info("调整订单费用");
       ofl.setLog_user(SecurityUserHolder.getCurrentUser());
       ofl.setOf(obj);
       this.orderFormLogService.save(ofl);
       if (this.configService.getSysConfig().isEmailEnable()) {
         send_email(request, obj, 
           "email_tobuyer_order_update_fee_notify");
       }
       if (this.configService.getSysConfig().isSmsEnbale()) {
         send_sms(request, obj, obj.getUser().getMobile(), 
           "sms_tobuyer_order_fee_notify");
       }
     }
     return "redirect:order.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="线下付款确认", value="/seller/seller_order_outline.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/seller_order_outline.htm"})
   public ModelAndView seller_order_outline(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/seller_order_outline.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       mv.addObject("obj", obj);
       mv.addObject("currentPage", currentPage);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="线下付款确认保存", value="/seller/seller_order_outline_save.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/seller_order_outline_save.htm"})
   public String seller_order_outline_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String state_info) throws Exception {
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       obj.setOrder_status(20);
       this.orderFormService.update(obj);
 
       for (GoodsCart gc : obj.getGcs()) {
         Goods goods = gc.getGoods();
         if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
           for (GroupGoods gg : goods.getGroup_goods_list()) {
             if (gg.getGroup().equals(goods.getGroup().getId())) {
               gg.setGg_count(gg.getGg_count() - gc.getCount());
               gg.setGg_def_count(gg.getGg_def_count() + 
                 gc.getCount());
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
         String inventory_type = goods.getInventory_type() == null ? "all" : 
           goods.getInventory_type();
         Map temp;
         if (inventory_type.equals("all")) {
           goods.setGoods_inventory(goods.getGoods_inventory() - 
             gc.getCount());
         } else {
           List list = (List)Json.fromJson(ArrayList.class, 
             goods.getGoods_inventory_detail());
           for (Iterator localIterator4 = list.iterator(); localIterator4.hasNext(); ) { temp = (Map)localIterator4.next();
             String[] temp_ids = 
               CommUtil.null2String(temp.get("id")).split("_");
             Arrays.sort(temp_ids);
             Arrays.sort(gsp_list);
             if (Arrays.equals(temp_ids, gsp_list)) {
               temp.put(
                 "count", 
                 Integer.valueOf(CommUtil.null2Int(temp.get("count")) - 
                 gc.getCount()));
             }
           }
           goods.setGoods_inventory_detail(Json.toJson(list, 
             JsonFormat.compact()));
         }
         for (GroupGoods gg : goods.getGroup_goods_list()) {
           if ((!gg.getGroup().getId().equals(goods.getGroup().getId())) || 
             (gg.getGg_count() != 0)) continue;
           goods.setGroup_buy(3);
         }
 
         this.goodsService.update(goods);
       }
       OrderFormLog ofl = new OrderFormLog();
       ofl.setAddTime(new Date());
       ofl.setLog_info("确认线下付款");
       ofl.setLog_user(SecurityUserHolder.getCurrentUser());
       ofl.setOf(obj);
       ofl.setState_info(state_info);
       this.orderFormLogService.save(ofl);
       if (this.configService.getSysConfig().isEmailEnable()) {
         send_email(request, obj, 
           "email_tobuyer_order_outline_pay_ok_notify");
       }
       if (this.configService.getSysConfig().isSmsEnbale()) {
         send_sms(request, obj, obj.getUser().getMobile(), 
           "sms_tobuyer_order_outline_pay_ok_notify");
       }
     }
     return "redirect:order.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家确认发货", value="/seller/order_shipping.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_shipping.htm"})
   public ModelAndView order_shipping(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/seller_order_shipping.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       mv.addObject("obj", obj);
       mv.addObject("currentPage", currentPage);
 
       Map map = new HashMap();
       map.put("oid", CommUtil.null2Long(id));
       List<GoodsCart> goodsCarts = this.goodsCartService.query(
         "select obj from GoodsCart obj where obj.of.id = :oid", 
         map, -1, -1);
       List deliveryGoods = new ArrayList();
       boolean physicalGoods = false;
       for (GoodsCart gc : goodsCarts) {
         if (gc.getGoods().getGoods_choice_type() == 1)
           deliveryGoods.add(gc);
         else {
           physicalGoods = true;
         }
       }
       Map params = new HashMap();
       params.put("status", Integer.valueOf(0));
       List expressCompanys = this.expressCompayService
         .query("select obj from ExpressCompany obj where obj.company_status=:status order by company_sequence asc", 
         params, -1, -1);
       mv.addObject("expressCompanys", expressCompanys);
       mv.addObject("physicalGoods", Boolean.valueOf(physicalGoods));
       mv.addObject("deliveryGoods", deliveryGoods);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家确认发货保存", value="/seller/order_shipping_save.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_shipping_save.htm"})
   public String order_shipping_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String shipCode, String state_info, String order_seller_intro, String ec_id) throws Exception
   {
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
     ExpressCompany ec = this.expressCompayService.getObjById(
       CommUtil.null2Long(ec_id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       obj.setOrder_status(30);
       obj.setShipCode(shipCode);
       obj.setShipTime(new Date());
       obj.setEc(ec);
       obj.setOrder_seller_intro(order_seller_intro);
       this.orderFormService.update(obj);
       OrderFormLog ofl = new OrderFormLog();
       ofl.setAddTime(new Date());
       ofl.setLog_info("确认发货");
       ofl.setState_info(state_info);
       ofl.setLog_user(SecurityUserHolder.getCurrentUser());
       ofl.setOf(obj);
       this.orderFormLogService.save(ofl);
       if (this.configService.getSysConfig().isEmailEnable()) {
         send_email(request, obj, "email_tobuyer_order_ship_notify");
       }
       if (this.configService.getSysConfig().isSmsEnbale()) {
         send_sms(request, obj, obj.getUser().getMobile(), 
           "sms_tobuyer_order_ship_notify");
       }
 
       if (obj.getPayment().getMark().equals("alipay"))
       {
         boolean synch = false;
         String safe_key = "";
         String partner = "";
 
         if (!CommUtil.null2String(obj.getPayment().getSafeKey())
           .equals(""))
         {
           if (!CommUtil.null2String(obj.getPayment().getPartner())
             .equals("")) {
             safe_key = obj.getPayment().getSafeKey();
             partner = obj.getPayment().getPartner();
             synch = true;
           }
         }Map params = new HashMap();
         params.put("type", "admin");
         params.put("mark", "alipay");
         List payments = this.paymentService
           .query("select obj from Payment obj where obj.type=:type and obj.mark=:mark", 
           params, -1, -1);
         if ((payments.size() > 0) && 
           (payments.get(0) != null))
         {
           if (!CommUtil.null2String(
             ((Payment)payments.get(0)).getSafeKey()).equals(""))
           {
             if (!CommUtil.null2String(
               ((Payment)payments.get(0)).getPartner()).equals("")) {
               safe_key = ((Payment)payments.get(0)).getSafeKey();
               partner = ((Payment)payments.get(0)).getPartner();
               synch = true;
             }
           }
         }
         label480: if (synch) {
           AlipayConfig config = new AlipayConfig();
           config.setKey(safe_key);
           config.setPartner(partner);
           Map sParaTemp = new HashMap();
           sParaTemp.put("service", "send_goods_confirm_by_platform");
           sParaTemp.put("partner", config.getPartner());
           sParaTemp.put("_input_charset", config.getInput_charset());
           sParaTemp.put("trade_no", obj.getOut_order_id());
           sParaTemp.put("logistics_name", ec.getCompany_name());
           sParaTemp.put("invoice_no", shipCode);
           sParaTemp.put("transport_type", ec.getCompany_type());
 
           String str1 = AlipaySubmit.buildRequest(config, "web", 
             sParaTemp, "", "");
         }
       }
     }
 
     return "redirect:order.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家修改物流", value="/seller/order_shipping_code.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_shipping_code.htm"})
   public ModelAndView order_shipping_code(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/seller_order_shipping_code.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       mv.addObject("obj", obj);
       mv.addObject("currentPage", currentPage);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家修改物流保存", value="/seller/order_shipping_code_save.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_shipping_code_save.htm"})
   public String order_shipping_code_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String shipCode, String state_info) {
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       obj.setShipCode(shipCode);
       this.orderFormService.update(obj);
       OrderFormLog ofl = new OrderFormLog();
       ofl.setAddTime(new Date());
       ofl.setLog_info("修改物流信息");
       ofl.setState_info(state_info);
       ofl.setLog_user(SecurityUserHolder.getCurrentUser());
       ofl.setOf(obj);
       this.orderFormLogService.save(ofl);
     }
     return "redirect:order.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家退款", value="/seller/order_refund.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_refund.htm"})
   public ModelAndView order_refund(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/seller_order_refund.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       mv.addObject("obj", obj);
       mv.addObject("currentPage", currentPage);
       mv.addObject("paymentTools", this.paymentTools);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家退款保存", value="/seller/order_refund_save.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_refund_save.htm"})
   public String order_refund_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String refund, String refund_log, String refund_type) {
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       obj.setRefund(BigDecimal.valueOf(CommUtil.add(obj.getRefund(), 
         refund)));
       this.orderFormService.update(obj);
 
       String type = "预存款";
       if (type.equals(refund_type)) {
         User seller = this.userService.getObjById(obj.getStore()
           .getUser().getId());
         seller.setAvailableBalance(BigDecimal.valueOf(
           CommUtil.subtract(seller.getAvailableBalance(), 
           BigDecimal.valueOf(CommUtil.null2Double(refund)))));
         this.userService.update(seller);
         User buyer = obj.getUser();
         buyer.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
           buyer.getAvailableBalance(), 
           BigDecimal.valueOf(CommUtil.null2Double(refund)))));
         this.userService.update(buyer);
       }
       RefundLog log = new RefundLog();
       log.setAddTime(new Date());
       log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + 
         obj.getUser().getId().toString());
       log.setOf(obj);
       log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(refund)));
       log.setRefund_log(refund_log);
       log.setRefund_type(refund_type);
       log.setRefund_user(SecurityUserHolder.getCurrentUser());
       this.refundLogService.save(log);
     }
     return "redirect:order.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家退货", value="/seller/order_return.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_return.htm"})
   public ModelAndView order_return(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/seller_order_return.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       mv.addObject("obj", obj);
       mv.addObject("currentPage", currentPage);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家退货保存", value="/seller/order_return_save.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_return_save.htm"})
   public String order_return_save(HttpServletRequest request, HttpServletResponse response, String id, String return_info, String currentPage) {
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       Enumeration enum1 = request.getParameterNames();
       GoodsReturn gr = new GoodsReturn();
       gr.setAddTime(new Date());
       gr.setOf(obj);
       gr.setReturn_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + 
         obj.getUser().getId().toString());
       gr.setUser(SecurityUserHolder.getCurrentUser());
       gr.setReturn_info(return_info);
       this.goodsReturnService.save(gr);
       while (enum1.hasMoreElements()) {
         String paramName = (String)enum1.nextElement();
         if (paramName.indexOf("refund_count_") >= 0) {
           GoodsCart gc = this.goodsCartService.getObjById(
             CommUtil.null2Long(paramName.substring(13)));
           int count = CommUtil.null2Int(request
             .getParameter(paramName));
           if (count > 0) {
             gc.setCount(gc.getCount() - count);
             this.goodsCartService.update(gc);
             GoodsReturnItem item = new GoodsReturnItem();
             item.setAddTime(new Date());
             item.setCount(count);
             item.setGoods(gc.getGoods());
             item.setGr(gr);
             for (GoodsSpecProperty gsp : gc.getGsps()) {
               item.getGsps().add(gsp);
             }
             item.setSpec_info(gc.getSpec_info());
             this.goodsReturnItemService.save(item);
 
             Goods goods = gc.getGoods();
             if (goods.getInventory_type().equals("all")) {
               goods.setGoods_inventory(goods.getGoods_inventory() + 
                 count);
             } else {
               Object gsps = new ArrayList();
               for (GoodsSpecProperty gsp : gc.getGsps()) {
                 ((List)gsps).add(gsp.getId().toString());
               }
               String[] gsp_list = new String[((List)gsps).size()];
               ((List)gsps).toArray(gsp_list);
               List<Map> list = (List)Json.fromJson(ArrayList.class, 
                 goods.getGoods_inventory_detail());
               for (Map temp : list) {
                 String[] temp_ids = CommUtil.null2String(
                   temp.get("id")).split("_");
                 Arrays.sort(temp_ids);
                 Arrays.sort(gsp_list);
                 if (Arrays.equals(temp_ids, gsp_list)) {
                   temp.put(
                     "count", 
                     Integer.valueOf(CommUtil.null2Int(temp.get("count")) + 
                     count));
                 }
               }
               goods.setGoods_inventory_detail(Json.toJson(list, 
                 JsonFormat.compact()));
             }
             goods.setGoods_salenum(goods.getGoods_salenum() - count);
             this.goodsService.update(goods);
 
             if (obj.getPayment().getMark().equals("balance")) {
               BigDecimal balance = goods.getGoods_current_price();
               User seller = this.userService
                 .getObjById(
                 SecurityUserHolder.getCurrentUser().getId());
 
               if (this.configService.getSysConfig()
                 .getBalance_fenrun() == 1) {
                 Object params = new HashMap();
                 ((Map)params).put("type", "admin");
                 ((Map)params).put("mark", "balance");
                 List payments = this.paymentService
                   .query("select obj from Payment obj where obj.type=:type and obj.mark=:mark", 
                   (Map)params, -1, -1);
                 Payment shop_payment = new Payment();
                 if (payments.size() > 0) {
                   shop_payment = (Payment)payments.get(0);
                 }
 
                 double shop_availableBalance = 
                   CommUtil.null2Double(balance) * 
                   CommUtil.null2Double(shop_payment
                   .getBalance_divide_rate());
                 balance = BigDecimal.valueOf(
                   CommUtil.null2Double(balance) - 
                   shop_availableBalance);
               }
               seller.setAvailableBalance(
                 BigDecimal.valueOf(CommUtil.subtract(
                 seller.getAvailableBalance(), 
                 balance)));
               this.userService.update(seller);
               User buyer = obj.getUser();
               buyer.setAvailableBalance(
                 BigDecimal.valueOf(CommUtil.add(
                 buyer.getAvailableBalance(), 
                 balance)));
               this.userService.update(buyer);
             }
           }
         }
       }
       GoodsReturnLog grl = new GoodsReturnLog();
       grl.setAddTime(new Date());
       grl.setGr(gr);
       grl.setOf(obj);
       grl.setReturn_user(SecurityUserHolder.getCurrentUser());
       this.goodsReturnLogService.save(grl);
     }
     return (String)(String)("redirect:order.htm?currentPage=" + currentPage);
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家评价", value="/seller/order_evaluate.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_evaluate.htm"})
   public ModelAndView order_evaluate(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/seller_order_evaluate.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       mv.addObject("obj", obj);
       mv.addObject("currentPage", currentPage);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家评价保存", value="/seller/order_evaluate_save.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_evaluate_save.htm"})
   public ModelAndView order_evaluate_save(HttpServletRequest request, HttpServletResponse response, String id, String evaluate_info, String evaluate_seller_val) {
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       if (obj.getOrder_status() == 50) {
         obj.setOrder_status(60);
         obj.setFinishTime(new Date());
         this.orderFormService.update(obj);
         Enumeration enum1 = request.getParameterNames();
         List maps = new ArrayList();
         while (enum1.hasMoreElements()) {
           String paramName = (String)enum1.nextElement();
           if (paramName.indexOf("evaluate_seller_val") >= 0) {
             String value = request.getParameter(paramName);
             Evaluate eva = this.evaluateService.getObjById(
               CommUtil.null2Long(paramName.substring(19)));
             eva.setEvaluate_seller_val(CommUtil.null2Int(request
               .getParameter(paramName)));
             eva.setEvaluate_seller_user(
               SecurityUserHolder.getCurrentUser());
             eva.setEvaluate_seller_info(request
               .getParameter("evaluate_info" + 
               eva.getId().toString()));
             eva.setEvaluate_seller_time(new Date());
             this.evaluateService.update(eva);
             User user = obj.getUser();
             user.setUser_credit(user.getUser_credit() + 
               eva.getEvaluate_seller_val());
 
             if (this.configService.getSysConfig().isIntegral()) {
               int integral = 0;
 
               if (this.configService.getSysConfig()
                 .getConsumptionRatio() > 0) {
                 integral = CommUtil.null2Int(Double.valueOf(CommUtil.div(obj
                   .getTotalPrice(), 
                   Integer.valueOf(this.configService
                   .getSysConfig().getConsumptionRatio()))));
               }
               integral = integral > this.configService
                 .getSysConfig().getEveryIndentLimit() ? this.configService
                 .getSysConfig().getEveryIndentLimit() : 
                 integral;
               user.setIntegral(user.getIntegral() + integral);
               this.userService.update(user);
               IntegralLog log = new IntegralLog();
               log.setAddTime(new Date());
               log.setContent("订单" + obj.getOrder_id() + "完成增加" + 
                 integral + "分");
               log.setIntegral(integral);
               log.setIntegral_user(user);
               log.setType("login");
               this.integralLogService.save(log);
             }
           }
         }
       }
 
       OrderFormLog ofl = new OrderFormLog();
       ofl.setAddTime(new Date());
       ofl.setLog_info("评价订单");
       ofl.setLog_user(SecurityUserHolder.getCurrentUser());
       ofl.setOf(obj);
       this.orderFormLogService.save(ofl);
     }
     ModelAndView mv = new JModelAndView("success.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     mv.addObject("op_title", "订单评价成功！");
     mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="打印订单", value="/seller/order_print.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_print.htm"})
   public ModelAndView order_print(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/order_print.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       OrderForm orderform = this.orderFormService.getObjById(
         CommUtil.null2Long(id));
       mv.addObject("obj", orderform);
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家物流详情", value="/seller/ship_view.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/ship_view.htm"})
   public ModelAndView order_ship_view(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/order_ship_view.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       mv.addObject("obj", obj);
       TransInfo transInfo = query_ship_getData(
         CommUtil.null2String(obj.getId()));
       mv.addObject("transInfo", transInfo);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
 
   private TransInfo query_ship_getData(String id) {
     TransInfo info = new TransInfo();
     OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
     try {
       URL url = new URL("http://api.kuaidi100.com/api?id=" + 
    	 this.configService.getSysConfig().getKuaidi_id() + 
         "&com=" + (obj.getEc() != null ? obj.getEc().getCompany_mark() : "") + 
         "&nu=" + obj.getShipCode() + "&show=0&muti=1&order=asc");
       URLConnection con = url.openConnection();
       con.setAllowUserInteraction(false);
       InputStream urlStream = url.openStream();
       String type = URLConnection.guessContentTypeFromStream(urlStream);
       String charSet = null;
       if (type == null)
         type = con.getContentType();
       if ((type == null) || (type.trim().length() == 0) || (type.trim().indexOf("text/html") < 0))
         return info;
       if (type.indexOf("charset=") > 0)
         charSet = type.substring(type.indexOf("charset=") + 8);
       
       /*byte[] b = new byte[10000];
       int numRead = urlStream.read(b);
       String content = new String(b, 0, numRead, charSet);
       while (numRead != -1) {
         numRead = urlStream.read(b);
         if (numRead == -1)
           continue;
         String newContent = new String(b, 0, numRead, charSet);
         content = content + newContent;
       }*/
       
       String line;   
       StringBuffer stringBuffer = new StringBuffer();    
       Reader reader = new InputStreamReader(urlStream, charSet);   
       //增加缓冲功能   
       BufferedReader bufferedReader = new BufferedReader(reader);   
       while ((line = bufferedReader.readLine()) != null) {   
           stringBuffer.append(line);   
       }   
       if (bufferedReader != null) {   
           bufferedReader.close();   
       }   
       String content = stringBuffer.toString();   
 
       info = (TransInfo)Json.fromJson(TransInfo.class, content);
       urlStream.close();
     } catch (MalformedURLException e) {
       e.printStackTrace();
     } catch (IOException e) {
       e.printStackTrace();
     }
     return info;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家物流详情", value="/seller/order_query_userinfor.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/order_query_userinfor.htm"})
   public ModelAndView seller_query_userinfor(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/seller_query_userinfor.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="买家退货申请详情", value="/seller/seller_order_return_apply_view.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/seller_order_return_apply_view.htm"})
   public ModelAndView seller_order_return_apply_view(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/seller_order_return_apply_view.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getUser().getId()
       .equals(SecurityUserHolder.getCurrentUser().getId())) {
       mv.addObject("obj", obj);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家保存退货申请", value="/seller/seller_order_return.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/seller_order_return.htm"})
   public String seller_order_return(HttpServletRequest request, HttpServletResponse response, String id, String gr_id, String currentPage, String mark) throws Exception {
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
     if (mark.equals("true"))
     {
       if (obj.getStore()
         .getId()
         .equals(SecurityUserHolder.getCurrentUser().getStore()
         .getId())) {
         Enumeration enum1 = request.getParameterNames();
         GoodsReturn gr = this.goodsReturnService.getObjById(
           CommUtil.null2Long(gr_id));
         obj.setOrder_status(46);
         int auto_order_return = this.configService.getSysConfig()
           .getAuto_order_return();
         Calendar cal = Calendar.getInstance();
         cal.add(6, auto_order_return);
         obj.setReturn_shipTime(cal.getTime());
         if (this.configService.getSysConfig().isEmailEnable()) {
           send_email(request, obj, 
             "email_tobuyer_order_return_apply_ok_notify");
         }
         if (this.configService.getSysConfig().isSmsEnbale())
           send_sms(request, obj, obj.getUser().getMobile(), 
             "sms_tobuyer_order_return_apply_ok_notify");
       }
     }
     else {
       obj.setOrder_status(48);
       if (this.configService.getSysConfig().isEmailEnable()) {
         send_email(request, obj, 
           "email_tobuyer_order_return_apply_refuse_notify");
       }
       if (this.configService.getSysConfig().isSmsEnbale()) {
         send_sms(request, obj, obj.getUser().getMobile(), 
           "sms_tobuyer_order_return_apply_refuse_notify");
       }
     }
     this.orderFormService.update(obj);
     return "redirect:order.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="确认买家退货", value="/seller/seller_order_return_confirm.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/seller_order_return_confirm.htm"})
   public ModelAndView seller_order_return_confirm(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView("error.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       obj.setOrder_status(47);
       this.orderFormService.update(obj);
       mv = new JModelAndView("success.html", 
         this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您已成功确认退货");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     } else {
       mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="买家退货物流详情", value="/seller/seller_order_return_ship_view.htm*", rtype="seller", rname="订单管理", rcode="order_seller", rgroup="交易管理")
   @RequestMapping({"/seller/seller_order_return_ship_view.htm"})
   public ModelAndView seller_order_return_ship_view(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView("error.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
 
     if (obj.getStore().getId()
       .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
       if ((obj.getReturn_shipCode() != null) && 
         (!obj.getReturn_shipCode().equals("")) && 
         (obj.getReturn_ec() != null) && 
         (!obj.getReturn_ec().equals(""))) {
         mv = new JModelAndView(
           "user/default/usercenter/seller_order_return_ship_view.html", 
           this.configService.getSysConfig(), this.userConfigService
           .getUserConfig(), 0, request, response);
         TransInfo transInfo = query_return_ship(
           CommUtil.null2String(obj.getId()));
         mv.addObject("obj", obj);
         mv.addObject("transInfo", transInfo);
       } else {
         mv.addObject("op_title", "买家没有提交退货物流信息");
         mv.addObject("url", CommUtil.getURL(request) + 
           "/seller/order.htm");
       }
     } else {
       mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
       mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
     }
     return mv;
   }
 
   private TransInfo query_return_ship(String id) {
     TransInfo info = new TransInfo();
     OrderForm obj = this.orderFormService
       .getObjById(CommUtil.null2Long(id));
     try {
       URL url = new URL("http://api.kuaidi100.com/api?id=" + 
         this.configService.getSysConfig().getKuaidi_id() + 
         "&com=" + (
         obj.getReturn_ec() != null ? obj.getReturn_ec()
         .getCompany_mark() : "") + "&nu=" + 
         obj.getReturn_shipCode() + "&show=0&muti=1&order=asc");
       URLConnection con = url.openConnection();
       con.setAllowUserInteraction(false);
       InputStream urlStream = url.openStream();
       String type = URLConnection.guessContentTypeFromStream(urlStream);
       String charSet = null;
       if (type == null)
         type = con.getContentType();
       if ((type == null) || (type.trim().length() == 0) || 
         (type.trim().indexOf("text/html") < 0))
         return info;
       if (type.indexOf("charset=") > 0)
         charSet = type.substring(type.indexOf("charset=") + 8);
       byte[] b = new byte[10000];
       int numRead = urlStream.read(b);
       String content = new String(b, 0, numRead, charSet);
       while (numRead != -1) {
         numRead = urlStream.read(b);
         if (numRead == -1)
           continue;
         String newContent = new String(b, 0, numRead, charSet);
         content = content + newContent;
       }
 
       info = (TransInfo)Json.fromJson(TransInfo.class, content);
       urlStream.close();
     } catch (MalformedURLException e) {
       e.printStackTrace();
     } catch (IOException e) {
       e.printStackTrace();
     }
     return info;
   }
 
   private void send_email(HttpServletRequest request, OrderForm order, String mark) throws Exception
   {
     com.shopping.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
     if ((template != null) && (template.isOpen())) {
       String email = order.getUser().getEmail();
       String subject = template.getTitle();
       String path = request.getSession().getServletContext()
         .getRealPath("") + 
         File.separator + "vm" + File.separator;
       if (!CommUtil.fileExist(path)) {
         CommUtil.createFolder(path);
       }
       PrintWriter pwrite = new PrintWriter(
         new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
       pwrite.print(template.getContent());
       pwrite.flush();
       pwrite.close();
 
       Properties p = new Properties();
       p.setProperty("file.resource.loader.path", 
         request.getRealPath("") + File.separator + "vm" + 
         File.separator);
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
 
   private void send_sms(HttpServletRequest request, OrderForm order, String mobile, String mark) throws Exception
   {
     com.shopping.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
     if ((template != null) && (template.isOpen())) {
       String path = request.getSession().getServletContext()
         .getRealPath("") + 
         File.separator + "vm" + File.separator;
       if (!CommUtil.fileExist(path)) {
         CommUtil.createFolder(path);
       }
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


 
 
 