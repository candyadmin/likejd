 package com.shopping.manage.admin.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.domain.IntegralGoods;
 import com.shopping.foundation.domain.IntegralGoodsCart;
 import com.shopping.foundation.domain.IntegralGoodsOrder;
 import com.shopping.foundation.domain.IntegralLog;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.IntegralGoodsOrderQueryObject;
 import com.shopping.foundation.domain.query.IntegralGoodsQueryObject;
 import com.shopping.foundation.service.IAccessoryService;
 import com.shopping.foundation.service.IIntegralGoodsOrderService;
 import com.shopping.foundation.service.IIntegralGoodsService;
 import com.shopping.foundation.service.IIntegralLogService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import java.io.File;
 import java.io.IOException;
 import java.math.BigDecimal;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.HashMap;
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
 public class IntegralGoodsManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IIntegralGoodsService integralgoodsService;
 
   @Autowired
   private IIntegralGoodsOrderService integralGoodsOrderService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IIntegralLogService integralLogService;
 
   @SecurityMapping(display = false, rsequence = 0, title="积分礼品列表", value="/admin/integral_goods.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_goods.htm"})
   public ModelAndView integral_goods(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String ig_goods_name, String ig_show)
   {
     ModelAndView mv = new JModelAndView("admin/blue/integral_goods.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isIntegralStore()) {
       IntegralGoodsQueryObject qo = new IntegralGoodsQueryObject(
         currentPage, mv, orderBy, orderType);
       if (!CommUtil.null2String(ig_goods_name).equals("")) {
         qo.addQuery("obj.ig_goods_name", 
           new SysMap("ig_goods_name", 
           "%" + ig_goods_name.trim() + "%"), "like");
       }
       if (!CommUtil.null2String(ig_show).equals("")) {
         qo.addQuery("obj.ig_show", 
           new SysMap("ig_show", 
           Boolean.valueOf(CommUtil.null2Boolean(ig_show))), "=");
       }
       IPageList pList = this.integralgoodsService.list(qo);
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
       mv.addObject("ig_goods_name", ig_goods_name);
       mv.addObject("ig_show", ig_show);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启积分商城");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/operation_base_set.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="积分礼品添加", value="/admin/integral_goods_add.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_goods_add.htm"})
   public ModelAndView integral_goods_add(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/integral_goods_add.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isIntegralStore()) {
       mv.addObject("currentPage", currentPage);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启积分商城");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/operation_base_set.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="积分礼品编辑", value="/admin/integral_goods_edit.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_goods_edit.htm"})
   public ModelAndView integral_goods_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/integral_goods_add.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isIntegralStore()) {
       if ((id != null) && (!id.equals(""))) {
         IntegralGoods integralgoods = this.integralgoodsService
           .getObjById(Long.valueOf(Long.parseLong(id)));
         mv.addObject("obj", integralgoods);
         mv.addObject("currentPage", currentPage);
         mv.addObject("edit", Boolean.valueOf(true));
       }
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启积分商城");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/operation_base_set.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="积分礼品保存", value="/admin/integral_goods_save.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_goods_save.htm"})
   public ModelAndView integral_goods_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String begin_hour, String end_hour, String list_url, String add_url)
   {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isIntegralStore()) {
       WebForm wf = new WebForm();
       IntegralGoods goods = null;
       if (id.equals("")) {
         goods = (IntegralGoods)wf.toPo(request, IntegralGoods.class);
         goods.setAddTime(new Date());
         goods.setIg_goods_sn("gift" + 
           CommUtil.formatTime("yyyyMMddHHmmss", new Date()) + 
           SecurityUserHolder.getCurrentUser().getId());
       } else {
         IntegralGoods obj = this.integralgoodsService.getObjById(
           Long.valueOf(Long.parseLong(id)));
         goods = (IntegralGoods)wf.toPo(request, obj);
       }
       String uploadFilePath = this.configService.getSysConfig()
         .getUploadFilePath();
       String saveFilePathName = request.getSession().getServletContext()
         .getRealPath("/") + 
         uploadFilePath + File.separator + "integral_goods";
       Map map = new HashMap();
       String fileName = "";
       if (goods.getIg_goods_img() != null)
         fileName = goods.getIg_goods_img().getName();
       try
       {
         map = CommUtil.saveFileToServer(request, "img1", 
           saveFilePathName, fileName, null);
         Accessory acc = null;
         if (fileName.equals("")) {
           if (map.get("fileName") != "") {
             acc = new Accessory();
             acc.setName(CommUtil.null2String(map.get("fileName")));
             acc.setExt(CommUtil.null2String(map.get("mime")));
             acc.setSize(CommUtil.null2Float(map.get("fileSize")));
             acc.setPath(uploadFilePath + "/integral_goods");
             acc.setWidth(CommUtil.null2Int(map.get("width")));
             acc.setHeight(CommUtil.null2Int(map.get("height")));
             acc.setAddTime(new Date());
             this.accessoryService.save(acc);
             goods.setIg_goods_img(acc);
           }
         }
         else if (map.get("fileName") != "") {
           acc = goods.getIg_goods_img();
           acc.setName(CommUtil.null2String(map.get("fileName")));
           acc.setExt(CommUtil.null2String(map.get("mime")));
           acc.setSize(CommUtil.null2Float(map.get("fileSize")));
           acc.setPath(uploadFilePath + "/integral_goods");
           acc.setWidth(CommUtil.null2Int(map.get("width")));
           acc.setHeight(CommUtil.null2Int(map.get("height")));
           acc.setAddTime(new Date());
           this.accessoryService.update(acc);
         }
       }
       catch (IOException e)
       {
         e.printStackTrace();
       }
       String ext = goods.getIg_goods_img().getExt().indexOf(".") < 0 ? "." + 
         goods.getIg_goods_img().getExt() : 
         goods.getIg_goods_img().getExt();
       String source = request.getSession().getServletContext()
         .getRealPath("/") + 
         goods.getIg_goods_img().getPath() + 
         File.separator + 
         goods.getIg_goods_img().getName();
       String target = source + "_small" + ext;
       CommUtil.createSmall(source, target, this.configService
         .getSysConfig().getSmallWidth(), this.configService
         .getSysConfig().getSmallHeight());
       Calendar cal = Calendar.getInstance();
       if (goods.getIg_begin_time() != null) {
         cal.setTime(goods.getIg_begin_time());
         cal.add(10, CommUtil.null2Int(begin_hour));
         goods.setIg_begin_time(cal.getTime());
       }
       if (goods.getIg_end_time() != null) {
         cal.setTime(goods.getIg_end_time());
         cal.add(10, CommUtil.null2Int(end_hour));
         goods.setIg_end_time(cal.getTime());
       }
       if (id.equals(""))
         this.integralgoodsService.save(goods);
       else {
         this.integralgoodsService.update(goods);
       }
       mv.addObject("list_url", list_url);
       mv.addObject("op_title", "保存积分商品成功");
       if (add_url != null)
         mv
           .addObject("add_url", add_url + "?currentPage=" + 
           currentPage);
     }
     else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启积分商城");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/operation_base_set.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="积分礼品删除", value="/admin/integral_goods_del.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_goods_del.htm"})
   public String integral_goods_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         IntegralGoods integralgoods = this.integralgoodsService
           .getObjById(Long.valueOf(Long.parseLong(id)));
         CommUtil.del_acc(request, integralgoods.getIg_goods_img());
         this.integralgoodsService.delete(Long.valueOf(Long.parseLong(id)));
       }
     }
     return "redirect:integral_goods_list.htm?currentPage=" + currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="积分礼品兑换列表", value="/admin/integral_order.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_order.htm"})
   public ModelAndView integral_order(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String igo_order_sn, String userName, String igo_payment, String igo_status)
   {
     ModelAndView mv = new JModelAndView("admin/blue/integral_order.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isIntegralStore()) {
       IntegralGoodsOrderQueryObject qo = new IntegralGoodsOrderQueryObject(
         currentPage, mv, orderBy, orderType);
       if (!CommUtil.null2String(igo_order_sn).equals("")) {
         qo.addQuery("obj.igo_order_sn", 
           new SysMap("igo_order_sn", "%" + 
           igo_order_sn.trim() + "%"), "like");
       }
       if (!CommUtil.null2String(userName).equals("")) {
         qo.addQuery("obj.igo_user.userName", 
           new SysMap("uesrName", 
           userName), "like");
       }
       if (!CommUtil.null2String(igo_payment).equals("")) {
         qo.addQuery("obj.igo_payment", 
           new SysMap("igo_payment", 
           igo_payment.trim()), "=");
       }
       if (!CommUtil.null2String(igo_status).equals("")) {
         qo.addQuery("obj.igo_status", 
           new SysMap("igo_status", 
           Integer.valueOf(CommUtil.null2Int(igo_status))), "=");
       }
       IPageList pList = this.integralGoodsOrderService.list(qo);
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
       mv.addObject("igo_order_sn", igo_order_sn);
       mv.addObject("userName", userName);
       mv.addObject("igo_payment", igo_payment);
       mv.addObject("igo_status", igo_status);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启积分商城");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/operation_base_set.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="积分礼品兑换详情", value="/admin/integral_order_view.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_order_view.htm"})
   public ModelAndView integral_order_view(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/integral_order_view.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isIntegralStore()) {
       IntegralGoodsOrder obj = this.integralGoodsOrderService
         .getObjById(CommUtil.null2Long(id));
       mv.addObject("obj", obj);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启积分商城");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/operation_base_set.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="取消积分订单", value="/admin/integral_order_cancel.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_order_cancel.htm"})
   public ModelAndView integral_order_cancel(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     IntegralGoodsOrder obj = this.integralGoodsOrderService
       .getObjById(CommUtil.null2Long(id));
     if (this.configService.getSysConfig().isIntegralStore()) {
       obj.setIgo_status(-1);
       this.integralGoodsOrderService.update(obj);
       for (IntegralGoodsCart igc : obj.getIgo_gcs()) {
         IntegralGoods goods = igc.getGoods();
         goods.setIg_goods_count(goods.getIg_goods_count() + 
           igc.getCount());
         this.integralgoodsService.update(goods);
       }
       User user = obj.getIgo_user();
       user.setIntegral(user.getIntegral() + obj.getIgo_total_integral());
       this.userService.update(user);
       IntegralLog log = new IntegralLog();
       log.setAddTime(new Date());
       log.setContent("取消" + obj.getIgo_order_sn() + "积分兑换，返还积分");
       log.setIntegral(obj.getIgo_total_integral());
       log.setIntegral_user(obj.getIgo_user());
       log.setOperate_user(SecurityUserHolder.getCurrentUser());
       log.setType("integral_order");
       this.integralLogService.save(log);
       mv.addObject("op_title", "积分兑换取消成功");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/integral_order.htm");
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启积分商城");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/operation_base_set.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="订单确认付款", value="/admin/integral_order_payok.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_order_payok.htm"})
   public ModelAndView integral_order_payok(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     IntegralGoodsOrder obj = this.integralGoodsOrderService
       .getObjById(CommUtil.null2Long(id));
     if (this.configService.getSysConfig().isIntegralStore()) {
       obj.setIgo_status(20);
       this.integralGoodsOrderService.update(obj);
       mv.addObject("op_title", "确认收款成功");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/integral_order.htm");
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启积分商城");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/operation_base_set.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="订单删除", value="/admin/integral_order_del.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_order_del.htm"})
   public ModelAndView integral_order_del(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     IntegralGoodsOrder obj = this.integralGoodsOrderService
       .getObjById(CommUtil.null2Long(id));
     if (this.configService.getSysConfig().isIntegralStore()) {
       if (obj.getIgo_status() == -1) {
         this.integralGoodsOrderService.delete(obj.getId());
       }
       mv.addObject("op_title", "删除兑换订单成功");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/integral_order.htm");
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启积分商城");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/operation_base_set.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="订单费用调整", value="/admin/integral_order_fee.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_order_fee.htm"})
   public ModelAndView integral_order_fee(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/integral_order_fee.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     IntegralGoodsOrder obj = this.integralGoodsOrderService
       .getObjById(CommUtil.null2Long(id));
     if (this.configService.getSysConfig().isIntegralStore()) {
       mv.addObject("obj", obj);
       mv.addObject("currentPage", currentPage);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启积分商城");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/operation_base_set.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="订单费用调整保存", value="/admin/integral_order_fee_save.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_order_fee_save.htm"})
   public String integral_order_fee_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String igo_trans_fee) {
     IntegralGoodsOrder obj = this.integralGoodsOrderService
       .getObjById(CommUtil.null2Long(id));
     if (this.configService.getSysConfig().isIntegralStore()) {
       obj.setIgo_trans_fee(BigDecimal.valueOf(
         CommUtil.null2Double(igo_trans_fee)));
       if (CommUtil.null2Double(obj.getIgo_trans_fee()) == 0.0D) {
         obj.setIgo_pay_time(new Date());
         obj.setIgo_status(20);
       }
       this.integralGoodsOrderService.update(obj);
     }
     return "redirect:integral_order.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="发货设置", value="/admin/integral_order_ship.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_order_ship.htm"})
   public ModelAndView integral_order_ship(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/integral_order_ship.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     IntegralGoodsOrder obj = this.integralGoodsOrderService
       .getObjById(CommUtil.null2Long(id));
     if (this.configService.getSysConfig().isIntegralStore()) {
       mv.addObject("obj", obj);
       mv.addObject("currentPage", currentPage);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启积分商城");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/operation_base_set.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="发货设置保存", value="/admin/integral_order_ship_save.htm*", rtype="admin", rname="积分商城", rcode="integral_goods_admin", rgroup="运营")
   @RequestMapping({"/admin/integral_order_ship_save.htm"})
   public String integral_order_ship_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String igo_ship_code, String igo_ship_time, String igo_ship_content) {
     IntegralGoodsOrder obj = this.integralGoodsOrderService
       .getObjById(CommUtil.null2Long(id));
     if (this.configService.getSysConfig().isIntegralStore()) {
       obj.setIgo_status(30);
       obj.setIgo_ship_code(igo_ship_code);
       obj.setIgo_ship_time(CommUtil.formatDate(igo_ship_time));
       obj.setIgo_ship_content(igo_ship_content);
       this.integralGoodsOrderService.update(obj);
     }
     return "redirect:integral_order.htm?currentPage=" + currentPage;
   }
 }


 
 
 