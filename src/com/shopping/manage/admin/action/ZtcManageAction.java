 package com.shopping.manage.admin.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.Store;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.ZTCGoldLog;
 import com.shopping.foundation.domain.query.GoodsQueryObject;
 import com.shopping.foundation.domain.query.ZTCGoldLogQueryObject;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import com.shopping.foundation.service.IZTCGoldLogService;
 import java.util.Calendar;
 import java.util.Date;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class ZtcManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IZTCGoldLogService ztcGoldLogService;
 
   @SecurityMapping(display = false, rsequence = 0, title="直通车设置", value="/admin/ztc_set.htm*", rtype="admin", rname="竞价直通车", rcode="ztc_set", rgroup="运营")
   @RequestMapping({"/admin/ztc_set.htm"})
   public ModelAndView ztc_set(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView("admin/blue/ztc_set.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="直通车设置保存", value="/admin/ztc_set_save.htm*", rtype="admin", rname="竞价直通车", rcode="ztc_set", rgroup="运营")
   @RequestMapping({"/admin/ztc_set_save.htm"})
   public ModelAndView ztc_set_save(HttpServletRequest request, HttpServletResponse response, String id, String ztc_status, String ztc_price, String ztc_goods_view) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     SysConfig config = this.configService.getSysConfig();
     config.setZtc_price(CommUtil.null2Int(ztc_price));
     config.setZtc_status(CommUtil.null2Boolean(ztc_status));
     config.setZtc_goods_view(CommUtil.null2Int(ztc_goods_view));
     if (id.equals(""))
       this.configService.save(config);
     else {
       this.configService.update(config);
     }
     mv.addObject("op_title", "直通车设置成功");
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/ztc_set.htm");
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="直通车申请列表", value="/admin/ztc_apply.htm*", rtype="admin", rname="竞价直通车", rcode="ztc_set", rgroup="运营")
   @RequestMapping({"/admin/ztc_apply.htm"})
   public ModelAndView ztc_apply(HttpServletRequest request, HttpServletResponse response, String currentPage, String goods_name, String userName, String store_name, String ztc_status, String ztc_pay_status)
   {
     ModelAndView mv = new JModelAndView("admin/blue/ztc_apply.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isZtc_status()) {
       GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, 
         "ztc_apply_time", "desc");
       qo.addQuery("obj.ztc_status", new SysMap("ztc_status", Integer.valueOf(1)), "=");
       if (!CommUtil.null2String(goods_name).equals("")) {
         qo.addQuery("obj.goods_name", 
           new SysMap("goods_name", "%" + 
           goods_name.trim() + "%"), "like");
       }
       if (!CommUtil.null2String(userName).equals("")) {
         qo.addQuery("obj.goods_store.user.userName", 
           new SysMap("userName", userName.trim()), "=");
       }
       if (!CommUtil.null2String(store_name).equals("")) {
         qo.addQuery("obj.goods_store.store_name", 
           new SysMap("store_name", store_name), "=");
       }
       if (!CommUtil.null2String(ztc_status).equals("")) {
         qo.addQuery("obj.ztc_status", 
           new SysMap("ztc_status", 
           Integer.valueOf(CommUtil.null2Int(ztc_status))), "=");
       }
       if (!CommUtil.null2String(ztc_pay_status).equals("")) {
         qo.addQuery("obj.ztc_pay_status", 
           new SysMap("ztc_pay_status", 
           Integer.valueOf(CommUtil.null2Int(ztc_pay_status))), "=");
       }
       IPageList pList = this.goodsService.list(qo);
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
       mv.addObject("goods_name", goods_name);
       mv.addObject("userName", userName);
       mv.addObject("store_name", store_name);
       mv.addObject("ztc_status", ztc_status);
       mv.addObject("ztc_pay_status", ztc_pay_status);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启直通车");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/ztc_set.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="直通车商品审核", value="/admin/ztc_apply_edit.htm*", rtype="admin", rname="竞价直通车", rcode="ztc_set", rgroup="运营")
   @RequestMapping({"/admin/ztc_apply_edit.htm"})
   public ModelAndView ztc_apply_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView("admin/blue/ztc_apply_edit.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isZtc_status()) {
       Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
       mv.addObject("obj", obj);
       mv.addObject("currentPage", currentPage);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启直通车");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/ztc_set.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="直通车商品查看", value="/admin/ztc_apply_view.htm*", rtype="admin", rname="竞价直通车", rcode="ztc_set", rgroup="运营")
   @RequestMapping({"/admin/ztc_apply_view.htm"})
   public ModelAndView ztc_apply_view(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView("admin/blue/ztc_apply_view.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isZtc_status()) {
       Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
       mv.addObject("obj", obj);
       mv.addObject("currentPage", currentPage);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启直通车");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/ztc_set.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="直通车商品审核保存", value="/admin/ztc_apply_save.htm*", rtype="admin", rname="竞价直通车", rcode="ztc_set", rgroup="运营")
   @RequestMapping({"/admin/ztc_apply_save.htm"})
   public ModelAndView ztc_apply_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String ztc_status, String ztc_admin_content) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isZtc_status()) {
       Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
       obj.setZtc_admin(SecurityUserHolder.getCurrentUser());
       obj.setZtc_admin_content(ztc_admin_content);
       Calendar cal = Calendar.getInstance();
       if ((CommUtil.null2Int(ztc_status) == 2) && 
         (cal.after(obj.getZtc_begin_time())))
         obj.setZtc_dredge_price(obj.getZtc_price());
       else {
         obj.setZtc_status(CommUtil.null2Int(ztc_status));
       }
       boolean ret = this.goodsService.update(obj);
       if ((ret) && (obj.getZtc_status() == 2)) {
         User user = obj.getGoods_store().getUser();
         user.setGold(user.getGold() - obj.getZtc_gold());
         this.userService.update(user);
         ZTCGoldLog log = new ZTCGoldLog();
         log.setAddTime(new Date());
         log.setZgl_content("开通直通车，消耗金币");
         log.setZgl_gold(obj.getZtc_gold());
         log.setZgl_goods(obj);
         log.setZgl_type(1);
         this.ztcGoldLogService.save(log);
       }
       mv.addObject("currentPage", currentPage);
       mv.addObject("op_title", "直通车审核成功");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/ztc_apply.htm?currentPage=" + currentPage);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启直通车");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/ztc_set.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="直通车商品", value="/admin/ztc_goods.htm*", rtype="admin", rname="竞价直通车", rcode="ztc_set", rgroup="运营")
   @RequestMapping({"/admin/ztc_goods.htm"})
   public ModelAndView ztc_goods(HttpServletRequest request, HttpServletResponse response, String currentPage, String goods_name, String userName, String store_name) {
     ModelAndView mv = new JModelAndView("admin/blue/ztc_goods.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isZtc_status()) {
       GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, 
         "ztc_apply_time", "desc");
       qo.addQuery("obj.ztc_status", new SysMap("ztc_status", Integer.valueOf(2)), "=");
       if (!CommUtil.null2String(goods_name).equals("")) {
         qo.addQuery("obj.goods_name", 
           new SysMap("goods_name", "%" + 
           goods_name.trim() + "%"), "like");
       }
       if (!CommUtil.null2String(userName).equals("")) {
         qo.addQuery("obj.goods_store.user.userName", 
           new SysMap("userName", userName.trim()), "=");
       }
       if (!CommUtil.null2String(store_name).equals("")) {
         qo.addQuery("obj.goods_store.store_name", 
           new SysMap("store_name", store_name), "=");
       }
 
       IPageList pList = this.goodsService.list(qo);
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
       mv.addObject("goods_name", goods_name);
       mv.addObject("userName", userName);
       mv.addObject("store_name", store_name);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启直通车");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/ztc_set.htm");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="直通车金币日志", value="/admin/ztc_gold_log.htm*", rtype="admin", rname="竞价直通车", rcode="ztc_set", rgroup="运营")
   @RequestMapping({"/admin/ztc_gold_log.htm"})
   public ModelAndView ztc_gold_log(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String goods_name, String store_name, String beginTime, String endTime)
   {
     ModelAndView mv = new JModelAndView("admin/blue/ztc_gold_log.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isZtc_status()) {
       ZTCGoldLogQueryObject qo = new ZTCGoldLogQueryObject(currentPage, 
         mv, "addTime", "desc");
       if (!CommUtil.null2String(goods_name).equals("")) {
         qo.addQuery("obj.zgl_goods.goods_name", 
           new SysMap("goods_name", "%" + goods_name.trim() + "%"), "like");
       }
       if (!CommUtil.null2String(store_name).equals("")) {
         qo.addQuery("obj.zgl_goods.goods_store.store_name", 
           new SysMap("store_name", store_name), "=");
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
       IPageList pList = this.ztcGoldLogService.list(qo);
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
       mv.addObject("goods_name", goods_name);
       mv.addObject("store_name", store_name);
       mv.addObject("beginTime", beginTime);
       mv.addObject("endTime", endTime);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启直通车");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/ztc_set.htm");
     }
     return mv;
   }
 }


 
 
 