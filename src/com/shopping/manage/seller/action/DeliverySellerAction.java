 package com.shopping.manage.seller.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.DeliveryGoods;
 import com.shopping.foundation.domain.DeliveryLog;
 import com.shopping.foundation.domain.GoldLog;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.Store;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.DeliveryGoodsQueryObject;
 import com.shopping.foundation.domain.query.DeliveryLogQueryObject;
 import com.shopping.foundation.domain.query.GoodsQueryObject;
 import com.shopping.foundation.service.IDeliveryGoodsService;
 import com.shopping.foundation.service.IDeliveryLogService;
 import com.shopping.foundation.service.IGoldLogService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.IStoreService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Map;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class DeliverySellerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IDeliveryGoodsService deliveryGoodsService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IStoreService storeService;
 
   @Autowired
   private IGoldLogService goldLogService;
 
   @Autowired
   private IDeliveryLogService deliveryLogService;
 
   @SecurityMapping(display = false, rsequence = 0, title="买就送", value="/seller/delivery.htm*", rtype="seller", rname="买就送", rcode="delivery_seller", rgroup="促销管理")
   @RequestMapping({"/seller/delivery.htm"})
   public ModelAndView delivery(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/delivery.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     DeliveryGoodsQueryObject qo = new DeliveryGoodsQueryObject(currentPage, 
       mv, orderBy, orderType);
     qo.addQuery("obj.d_goods.goods_store.id", 
       new SysMap("store_id", user
       .getStore().getId()), "=");
     IPageList pList = this.deliveryGoodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="买就送套餐购买日志", value="/seller/delivery_log.htm*", rtype="seller", rname="买就送", rcode="delivery_seller", rgroup="促销管理")
   @RequestMapping({"/seller/delivery_log.htm"})
   public ModelAndView delivery_log(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/delivery_log.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     DeliveryLogQueryObject qo = new DeliveryLogQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.store.id", 
       new SysMap("store_id", user.getStore()
       .getId()), "=");
     IPageList pList = this.deliveryLogService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="买就送套餐购买", value="/seller/delivery_buy.htm*", rtype="seller", rname="买就送", rcode="delivery_seller", rgroup="促销管理")
   @RequestMapping({"/seller/delivery_buy.htm"})
   public ModelAndView delivery_buy(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/delivery_buy.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     mv.addObject("user", user);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="买就送套餐购买保存", value="/seller/delivery_buy_save.htm*", rtype="seller", rname="买就送", rcode="delivery_seller", rgroup="促销管理")
   @RequestMapping({"/seller/delivery_buy_save.htm"})
   public String delivery_buy_save(HttpServletRequest request, HttpServletResponse response, String count)
   {
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     int gold = user.getGold();
     int delivery_gold = CommUtil.null2Int(count) * 
       this.configService.getSysConfig().getDelivery_amount();
     if (gold > delivery_gold)
     {
       user.setGold(gold - delivery_gold);
       this.userService.update(user);
 
       GoldLog log = new GoldLog();
       log.setAddTime(new Date());
       log.setGl_content("购买买就送套餐");
       log.setGl_count(delivery_gold);
       log.setGl_user(user);
       log.setGl_type(-1);
       this.goldLogService.save(log);
 
       Store store = user.getStore();
       if (store.getDelivery_begin_time() == null) {
         store.setDelivery_begin_time(new Date());
       }
       Calendar cal = Calendar.getInstance();
       if (store.getDelivery_end_time() != null) {
         cal.setTime(store.getDelivery_end_time());
       }
       cal.add(2, CommUtil.null2Int(count));
       store.setDelivery_end_time(cal.getTime());
       this.storeService.update(store);
 
       DeliveryLog d_log = new DeliveryLog();
       d_log.setAddTime(new Date());
       d_log.setBegin_time(new Date());
       d_log.setEnd_time(cal.getTime());
       d_log.setGold(delivery_gold);
       d_log.setStore(store);
       this.deliveryLogService.save(d_log);
       return "redirect:delivery_buy_success.htm";
     }
     return "redirect:delivery_buy_error.htm";
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="买就送套餐购买成功", value="/seller/delivery_buy_success.htm*", rtype="seller", rname="买就送", rcode="delivery_seller", rgroup="促销管理")
   @RequestMapping({"/seller/delivery_buy_success.htm"})
   public ModelAndView delivery_buy_success(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView("success.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     mv.addObject("op_title", "买就送套餐购买成功");
     mv.addObject("url", CommUtil.getURL(request) + "/seller/delivery.htm");
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="买就送套餐购买失败", value="/seller/delivery_buy_error.htm*", rtype="seller", rname="买就送", rcode="delivery_seller", rgroup="促销管理")
   @RequestMapping({"/seller/delivery_buy_error.htm"})
   public ModelAndView delivery_buy_error(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView("error.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     mv.addObject("op_title", "金币不足不能购买套餐");
     mv.addObject("url", CommUtil.getURL(request) + "/seller/delivery.htm");
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="申请买就送", value="/seller/delivery_apply.htm*", rtype="seller", rname="买就送", rcode="delivery_seller", rgroup="促销管理")
   @RequestMapping({"/seller/delivery_apply.htm"})
   public ModelAndView delivery_apply(HttpServletRequest request, HttpServletResponse response, String id)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/delivery_apply.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     Store store = user.getStore();
     if (store.getDelivery_end_time() == null) {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您尚未购买买就送套餐");
       mv.addObject("url", CommUtil.getURL(request) + 
         "/seller/delivery_buy.htm");
       return mv;
     }
     if (store.getDelivery_end_time().before(new Date())) {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您的买就送套餐已经过期");
       mv.addObject("url", CommUtil.getURL(request) + 
         "/seller/delivery_buy.htm");
       return mv;
     }
     Map map = CommUtil.cal_time_space(new Date(), store
       .getDelivery_begin_time());
     int minDate = CommUtil.null2Int(map.get("day"));
     minDate = minDate > 0 ? minDate : 0;
     map.clear();
     map = CommUtil.cal_time_space(new Date(), store.getDelivery_end_time());
     int maxDate = CommUtil.null2Int(map.get("day")) + 1;
     maxDate = maxDate > 0 ? maxDate : 0;
     mv.addObject("minDate", Integer.valueOf(minDate));
     mv.addObject("maxDate", Integer.valueOf(maxDate));
     String delivery_session = CommUtil.randomString(32);
     mv.addObject("delivery_session", delivery_session);
     request.getSession(false).setAttribute("delivery_session", 
       delivery_session);
     if (!CommUtil.null2String(id).equals("")) {
       DeliveryGoods obj = this.deliveryGoodsService.getObjById(
         CommUtil.null2Long(id));
       mv.addObject("obj", obj);
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="加载买就送商品", value="/seller/delivery_goods.htm*", rtype="seller", rname="买就送", rcode="delivery_seller", rgroup="促销管理")
   @RequestMapping({"/seller/delivery_goods.htm"})
   public ModelAndView delivery_goods(HttpServletRequest request, HttpServletResponse response, String goods_name, String currentPage, String node_id)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/delivery_goods.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     Store store = user.getStore();
     GoodsQueryObject qo = new GoodsQueryObject();
     qo.setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
     if (!CommUtil.null2String(goods_name).equals("")) {
       qo.addQuery("obj.goods_name", 
         new SysMap("goods_name", "%" + 
         CommUtil.null2String(goods_name) + "%"), "like");
     }
     qo.addQuery("obj.delivery_status", new SysMap("delivery_status", Integer.valueOf(0)), 
       "=");
     qo.addQuery("obj.goods_store.id", 
       new SysMap("store_id", store.getId()), "=");
     qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
     qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
     qo.addQuery("obj.group_buy", new SysMap("group_buy", Integer.valueOf(0)), "=");
     qo.addQuery("obj.activity_status", new SysMap("activity_status", Integer.valueOf(0)), "=");
     qo.addQuery("obj.combin_status", new SysMap("combin_status", Integer.valueOf(0)), "=");
     qo.setPageSize(Integer.valueOf(15));
     IPageList pList = this.goodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
       "/seller/delivery_goods.htm", "", 
       "&goods_name=" + goods_name, pList, mv);
     mv.addObject("node_id", node_id);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="买就送商品保存", value="/seller/delivery_apply_save.htm*", rtype="seller", rname="买就送", rcode="delivery_seller", rgroup="促销管理")
   @RequestMapping({"/seller/delivery_apply_save.htm"})
   public ModelAndView delivery_apply_save(HttpServletRequest request, HttpServletResponse response, String main_goods_id, String give_goods_id, String delivery_session)
   {
     ModelAndView mv = new JModelAndView("success.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     String delivery_session1 = CommUtil.null2String(request.getSession(
       false).getAttribute("delivery_session"));
     if ((!delivery_session1.equals("")) && 
       (delivery_session1.equals(delivery_session))) {
       request.getSession(false).removeAttribute("delivery_session");
       WebForm wf = new WebForm();
       DeliveryGoods obj = (DeliveryGoods)wf.toPo(request, DeliveryGoods.class);
       obj.setAddTime(new Date());
       Goods d_goods = this.goodsService.getObjById(
         CommUtil.null2Long(main_goods_id));
       obj.setD_goods(d_goods);
       Goods d_delivery_goods = this.goodsService.getObjById(
         CommUtil.null2Long(give_goods_id));
       obj.setD_delivery_goods(d_delivery_goods);
       this.deliveryGoodsService.save(obj);
       d_goods.setDelivery_status(1);
       this.goodsService.update(d_goods);
       mv.addObject("op_title", "买就送申请成功");
       mv.addObject("url", CommUtil.getURL(request) + 
         "/seller/delivery.htm");
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "禁止重复提交特送申请");
       mv.addObject("url", CommUtil.getURL(request) + 
         "/seller/delivery.htm");
     }
 
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="买就送删除", value="/seller/delivery_del.htm*", rtype="seller", rname="买就送", rcode="delivery_seller", rgroup="促销管理")
   @RequestMapping({"/seller/delivery_del.htm"})
   public String delivery_del(HttpServletRequest request, HttpServletResponse response, String currentPage, String mulitId) {
     for (String id : mulitId.split(",")) {
       if (!CommUtil.null2String(id).equals("")) {
         DeliveryGoods obj = this.deliveryGoodsService
           .getObjById(CommUtil.null2Long(id));
         if (obj.getD_status() != 1) {
           this.deliveryGoodsService.delete(obj.getId());
           Goods goods = obj.getD_goods();
           goods.setDelivery_status(0);
           this.goodsService.update(goods);
         }
       }
     }
     return "redirect:delivery.htm?currentPage=" + currentPage;
   }
 }


 
 
 