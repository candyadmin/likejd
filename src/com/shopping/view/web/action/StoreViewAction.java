 package com.shopping.view.web.action;
 
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Store;
 import com.shopping.foundation.domain.StoreClass;
 import com.shopping.foundation.domain.StoreNavigation;
 import com.shopping.foundation.domain.StorePoint;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.EvaluateQueryObject;
 import com.shopping.foundation.domain.query.GoodsQueryObject;
 import com.shopping.foundation.domain.query.StoreQueryObject;
 import com.shopping.foundation.service.IEvaluateService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.IStoreClassService;
 import com.shopping.foundation.service.IStoreNavigationService;
 import com.shopping.foundation.service.IStorePartnerService;
 import com.shopping.foundation.service.IStoreService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserGoodsClassService;
 import com.shopping.view.web.tools.AreaViewTools;
 import com.shopping.view.web.tools.GoodsViewTools;
 import com.shopping.view.web.tools.StoreViewTools;
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
 public class StoreViewAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IStoreService storeService;
 
   @Autowired
   private IStoreClassService storeClassService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IUserGoodsClassService userGoodsClassService;
 
   @Autowired
   private IStoreNavigationService storenavigationService;
 
   @Autowired
   private IStorePartnerService storepartnerService;
 
   @Autowired
   private IEvaluateService evaluateService;
 
   @Autowired
   private AreaViewTools areaViewTools;
 
   @Autowired
   private GoodsViewTools goodsViewTools;
 
   @Autowired
   private StoreViewTools storeViewTools;
 
   @RequestMapping({"/store.htm"})
   public ModelAndView store(HttpServletRequest request, HttpServletResponse response, String id)
   {
     String serverName = request.getServerName().toLowerCase();
     Store store = null;
     if ((id == null) && (serverName.indexOf(".") >= 0) && 
       (serverName.indexOf(".") != serverName.lastIndexOf(".")) && 
       (this.configService.getSysConfig().isSecond_domain_open())) {
       String secondDomain = serverName.substring(0, 
         serverName.indexOf("."));
       store = this.storeService.getObjByProperty("store_second_domain", 
         secondDomain);
     } else {
       store = this.storeService.getObjById(CommUtil.null2Long(id));
     }
     if (store == null) {
       ModelAndView mv = new JModelAndView("error.html", 
         this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "不存在该店铺信息");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
       return mv;
     }
     String template = "default";
     if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_index.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, 
       response);
     if (store.getStore_status() == 2) {
       add_store_common_info(mv, store);
       mv.addObject("store", store);
       mv.addObject("nav_id", "store_index");
     } else {
       mv = new JModelAndView("error.html", 
         this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "店铺已经关闭或者未开通店铺");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     generic_evaluate(store, mv);
     return mv;
   }
 
   @RequestMapping({"/store_left.htm"})
   public ModelAndView store_left(HttpServletRequest request, HttpServletResponse response)
   {
     Store store = this.storeService.getObjById(CommUtil.null2Long(request
       .getAttribute("id")));
     String template = "default";
     if ((store != null) && (store.getTemplate() != null) && 
       (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_left.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     mv.addObject("store", store);
     add_store_common_info(mv, store);
     generic_evaluate(store, mv);
     Map params = new HashMap();
     params.put("store_id", store.getId());
     List partners = this.storepartnerService
       .query("select obj from StorePartner obj where obj.store.id=:store_id order by obj.sequence asc", 
       params, -1, -1);
     mv.addObject("partners", partners);
     mv.addObject("goodsViewTools", this.goodsViewTools);
     return mv;
   }
 
   @RequestMapping({"/store_left1.htm"})
   public ModelAndView store_left1(HttpServletRequest request, HttpServletResponse response) {
     Store store = this.storeService.getObjById(CommUtil.null2Long(request
       .getAttribute("id")));
     String template = "default";
     if ((store != null) && (store.getTemplate() != null) && 
       (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_left1.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     mv.addObject("store", store);
     add_store_common_info(mv, store);
     Map params = new HashMap();
     params.put("store_id", store.getId());
     List partners = this.storepartnerService
       .query("select obj from StorePartner obj where obj.store.id=:store_id order by obj.sequence asc", 
       params, -1, -1);
     mv.addObject("partners", partners);
     return mv;
   }
 
   @RequestMapping({"/store_left2.htm"})
   public ModelAndView store_left2(HttpServletRequest request, HttpServletResponse response) {
     Store store = this.storeService.getObjById(CommUtil.null2Long(request
       .getAttribute("id")));
     String template = "default";
     if ((store != null) && (store.getTemplate() != null) && 
       (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_left2.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     mv.addObject("store", store);
     add_store_common_info(mv, store);
     return mv;
   }
 
   @RequestMapping({"/store_nav.htm"})
   public ModelAndView store_nav(HttpServletRequest request, HttpServletResponse response) {
     Long id = CommUtil.null2Long(request.getAttribute("id"));
     Store store = this.storeService.getObjById(id);
     String template = "default";
     if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_nav.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     if (store.getStore_status() == 2) {
       Map params = new HashMap();
       params.put("store_id", store.getId());
       params.put("display", Boolean.valueOf(true));
       List navs = this.storenavigationService
         .query("select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display=:display order by obj.sequence asc", 
         params, -1, -1);
       mv.addObject("navs", navs);
       mv.addObject("store", store);
       String goods_view = CommUtil.null2String(request
         .getAttribute("goods_view"));
       mv.addObject("goods_view", Boolean.valueOf(CommUtil.null2Boolean(goods_view)));
       mv.addObject("goods_id", 
         CommUtil.null2String(request.getAttribute("goods_id")));
       mv.addObject("goods_list", 
         Boolean.valueOf(CommUtil.null2Boolean(request.getAttribute("goods_list"))));
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "店铺信息错误");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
 
   @RequestMapping({"/store_credit.htm"})
   public ModelAndView store_credit(HttpServletRequest request, HttpServletResponse response, String id) {
     Store store = this.storeService.getObjById(CommUtil.null2Long(id));
     String template = "default";
     if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_credit.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     if (store.getStore_status() == 2) {
       EvaluateQueryObject qo = new EvaluateQueryObject("1", mv, 
         "addTime", "desc");
       qo.addQuery("obj.of.store.id", 
         new SysMap("store_id", store.getId()), "=");
       IPageList pList = this.evaluateService.list(qo);
       CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
         "/store_eva.htm", "", "", pList, mv);
       mv.addObject("store", store);
       mv.addObject("nav_id", "store_credit");
       mv.addObject("storeViewTools", this.storeViewTools);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "店铺信息错误");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
 
   @RequestMapping({"/store_eva.htm"})
   public ModelAndView store_eva(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String eva_val)
   {
     Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
     String template = "default";
     if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_eva.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     if (store.getStore_status() == 2) {
       EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, 
         "addTime", "desc");
       qo.addQuery("obj.evaluate_goods.goods_store.id", 
         new SysMap("store_id", store.getId()), "=");
       if (!CommUtil.null2String(eva_val).equals("")) {
         qo.addQuery("obj.evaluate_buyer_val", 
           new SysMap("evaluate_buyer_val", Integer.valueOf(CommUtil.null2Int(eva_val))), "=");
       }
       IPageList pList = this.evaluateService.list(qo);
       CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
         "/store_eva.htm", "", 
         "&eva_val=" + CommUtil.null2String(eva_val), pList, mv);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "店铺信息错误");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
 
   @RequestMapping({"/store_info.htm"})
   public ModelAndView store_info(HttpServletRequest request, HttpServletResponse response, String id) {
     Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
     String template = "default";
     if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_info.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     if (store.getStore_status() == 2) {
       mv.addObject("store", store);
       mv.addObject("nav_id", "store_info");
       mv.addObject("areaViewTools", this.areaViewTools);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "店铺信息错误");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
 
   @RequestMapping({"/store_url.htm"})
   public ModelAndView store_url(HttpServletRequest request, HttpServletResponse response, String id) {
     StoreNavigation nav = this.storenavigationService.getObjById(
       CommUtil.null2Long(id));
     String template = "default";
     if ((nav.getStore().getTemplate() != null) && 
       (!nav.getStore().getTemplate().equals(""))) {
       template = nav.getStore().getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/store_url.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     mv.addObject("store", nav.getStore());
     mv.addObject("nav", nav);
     mv.addObject("nav_id", nav.getId());
     return mv;
   }
 
   private void add_store_common_info(ModelAndView mv, Store store) {
     Map params = new HashMap();
     params.put("user_id", store.getUser().getId());
     params.put("display", Boolean.valueOf(true));
     List ugcs = this.userGoodsClassService
       .query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc", 
       params, -1, -1);
     mv.addObject("ugcs", ugcs);
     params.clear();
     params.put("recommend", Boolean.valueOf(true));
     params.put("goods_store_id", store.getId());
     params.put("goods_status", Integer.valueOf(0));
     List goods_recommend = this.goodsService
       .query("select obj from Goods obj where obj.goods_recommend=:recommend and obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.addTime desc", 
       params, 0, 8);
     params.clear();
     params.put("goods_store_id", store.getId());
     params.put("goods_status", Integer.valueOf(0));
     List goods_new = this.goodsService
       .query("select obj from Goods obj where obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.addTime desc ", 
       params, 0, 12);
     mv.addObject("goods_recommend", goods_recommend);
     mv.addObject("goods_new", goods_new);
     mv.addObject("goodsViewTools", this.goodsViewTools);
     mv.addObject("storeViewTools", this.storeViewTools);
     mv.addObject("areaViewTools", this.areaViewTools);
   }
 
   @RequestMapping({"/store_list.htm"})
   public ModelAndView store_list(HttpServletRequest request, HttpServletResponse response, String id, String sc_id, String currentPage, String orderType, String store_name, String store_ower, String type)
   {
     ModelAndView mv = new JModelAndView("store_list.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     List scs = this.storeClassService
       .query("select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc", 
       null, -1, -1);
     mv.addObject("scs", scs);
     StoreQueryObject sqo = new StoreQueryObject(currentPage, mv, 
       "store_credit", orderType);
     if ((sc_id != null) && (!sc_id.equals(""))) {
       sqo.addQuery("obj.sc.id", 
         new SysMap("sc_id", CommUtil.null2Long(sc_id)), "=");
     }
     if ((store_name != null) && (!store_name.equals(""))) {
       sqo.addQuery("obj.store_name", 
         new SysMap("store_name", "%" + 
         store_name + "%"), "like");
       mv.addObject("store_name", store_name);
     }
     if ((store_ower != null) && (!store_ower.equals(""))) {
       sqo.addQuery("obj.store_ower", 
         new SysMap("store_ower", "%" + 
         store_ower + "%"), "like");
       mv.addObject("store_ower", store_ower);
     }
     sqo.addQuery("obj.store_status", new SysMap("store_status", Integer.valueOf(2)), "=");
     IPageList pList = this.storeService.list(sqo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("storeViewTools", this.storeViewTools);
     mv.addObject("type", type);
     return mv;
   }
 
   @RequestMapping({"/store_goods_search.htm"})
   public ModelAndView store_goods_search(HttpServletRequest request, HttpServletResponse response, String keyword, String store_id, String currentPage)
   {
     Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(store_id)));
     String template = "default";
     if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + 
       "/store_goods_search.html", this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, null, null);
     gqo.addQuery("obj.goods_store.id", 
       new SysMap("store_id", CommUtil.null2Long(store_id)), "=");
     gqo.addQuery("obj.goods_name", 
       new SysMap("goods_name", "%" + keyword + 
       "%"), "like");
     gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
     gqo.setPageSize(Integer.valueOf(20));
     IPageList pList = this.goodsService.list(gqo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("keyword", keyword);
     mv.addObject("store", store);
     return mv;
   }
 
   @RequestMapping({"/store_head.htm"})
   public ModelAndView store_head(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView("store_head.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     Store store = this.storeService.getObjById(CommUtil.null2Long(request
       .getAttribute("store_id")));
     generic_evaluate(store, mv);
     mv.addObject("store", store);
     mv.addObject("storeViewTools", this.storeViewTools);
     return mv;
   }
 
   private void generic_evaluate(Store store, ModelAndView mv) {
     double description_result = 0.0D;
     double service_result = 0.0D;
     double ship_result = 0.0D;
     if ((store != null) && (store.getSc() != null) && (store.getPoint() != null)) {
       StoreClass sc = this.storeClassService.getObjById(store.getSc()
         .getId());
       float description_evaluate = CommUtil.null2Float(sc
         .getDescription_evaluate());
       float service_evaluate = CommUtil.null2Float(sc
         .getService_evaluate());
       float ship_evaluate = CommUtil.null2Float(sc.getShip_evaluate());
       float store_description_evaluate = CommUtil.null2Float(store
         .getPoint().getDescription_evaluate());
       float store_service_evaluate = CommUtil.null2Float(store.getPoint()
         .getService_evaluate());
       float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
         .getShip_evaluate());
 
       description_result = CommUtil.div(Float.valueOf(store_description_evaluate - 
         description_evaluate), Float.valueOf(description_evaluate));
       service_result = CommUtil.div(Float.valueOf(store_service_evaluate - 
         service_evaluate), Float.valueOf(service_evaluate));
       ship_result = CommUtil.div(Float.valueOf(store_ship_evaluate - ship_evaluate), 
         Float.valueOf(ship_evaluate));
     }
     if (description_result > 0.0D) {
       mv.addObject("description_css", "better");
       mv.addObject("description_type", "高于");
       mv.addObject(
         "description_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(description_result), Integer.valueOf(100)) > 100.0D ? 100.0D : 
         CommUtil.mul(Double.valueOf(description_result), Integer.valueOf(100)))) + 
         "%");
     }
     if (description_result == 0.0D) {
       mv.addObject("description_css", "better");
       mv.addObject("description_type", "持平");
       mv.addObject("description_result", "-----");
     }
     if (description_result < 0.0D) {
       mv.addObject("description_css", "lower");
       mv.addObject("description_type", "低于");
       mv.addObject(
         "description_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(-description_result), Integer.valueOf(100)))) + 
         "%");
     }
     if (service_result > 0.0D) {
       mv.addObject("service_css", "better");
       mv.addObject("service_type", "高于");
       mv.addObject("service_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(service_result), Integer.valueOf(100)))) + 
         "%");
     }
     if (service_result == 0.0D) {
       mv.addObject("service_css", "better");
       mv.addObject("service_type", "持平");
       mv.addObject("service_result", "-----");
     }
     if (service_result < 0.0D) {
       mv.addObject("service_css", "lower");
       mv.addObject("service_type", "低于");
       mv.addObject("service_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(-service_result), Integer.valueOf(100)))) + 
         "%");
     }
     if (ship_result > 0.0D) {
       mv.addObject("ship_css", "better");
       mv.addObject("ship_type", "高于");
       mv.addObject("ship_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(ship_result), Integer.valueOf(100)))) + "%");
     }
     if (ship_result == 0.0D) {
       mv.addObject("ship_css", "better");
       mv.addObject("ship_type", "持平");
       mv.addObject("ship_result", "-----");
     }
     if (ship_result < 0.0D) {
       mv.addObject("ship_css", "lower");
       mv.addObject("ship_type", "低于");
       mv.addObject("ship_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(-ship_result), Integer.valueOf(100)))) + "%");
     }
   }
 }


 
 
 