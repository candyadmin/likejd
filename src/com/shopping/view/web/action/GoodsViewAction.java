 package com.shopping.view.web.action;
 
 import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.Area;
import com.shopping.foundation.domain.Consult;
import com.shopping.foundation.domain.Goods;
import com.shopping.foundation.domain.GoodsBrand;
import com.shopping.foundation.domain.GoodsClass;
import com.shopping.foundation.domain.GoodsSpecProperty;
import com.shopping.foundation.domain.GoodsTypeProperty;
import com.shopping.foundation.domain.Group;
import com.shopping.foundation.domain.GroupGoods;
import com.shopping.foundation.domain.Store;
import com.shopping.foundation.domain.StoreClass;
import com.shopping.foundation.domain.UserGoodsClass;
import com.shopping.foundation.domain.query.ConsultQueryObject;
import com.shopping.foundation.domain.query.EvaluateQueryObject;
import com.shopping.foundation.domain.query.GoodsCartQueryObject;
import com.shopping.foundation.domain.query.GoodsQueryObject;
import com.shopping.foundation.service.IAreaService;
import com.shopping.foundation.service.IConsultService;
import com.shopping.foundation.service.IEvaluateService;
import com.shopping.foundation.service.IGoodsBrandService;
import com.shopping.foundation.service.IGoodsCartService;
import com.shopping.foundation.service.IGoodsClassService;
import com.shopping.foundation.service.IGoodsService;
import com.shopping.foundation.service.IGoodsSpecPropertyService;
import com.shopping.foundation.service.IGoodsTypePropertyService;
import com.shopping.foundation.service.IOrderFormService;
import com.shopping.foundation.service.IStoreClassService;
import com.shopping.foundation.service.IStoreService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.IUserConfigService;
import com.shopping.foundation.service.IUserGoodsClassService;
import com.shopping.manage.admin.tools.UserTools;
import com.shopping.manage.seller.Tools.TransportTools;
import com.shopping.view.web.tools.AreaViewTools;
import com.shopping.view.web.tools.GoodsViewTools;
import com.shopping.view.web.tools.IpAddress;
import com.shopping.view.web.tools.StoreViewTools;
 
 @Controller
 public class GoodsViewAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IGoodsClassService goodsClassService;
 
   @Autowired
   private IUserGoodsClassService userGoodsClassService;
 
   @Autowired
   private IStoreService storeService;
 
   @Autowired
   private IEvaluateService evaluateService;
 
   @Autowired
   private IOrderFormService orderFormService;
 
   @Autowired
   private IGoodsCartService goodsCartService;
 
   @Autowired
   private IConsultService consultService;
 
   @Autowired
   private IGoodsBrandService brandService;
 
   @Autowired
   private IGoodsSpecPropertyService goodsSpecPropertyService;
 
   @Autowired
   private IGoodsTypePropertyService goodsTypePropertyService;
 
   @Autowired
   private IAreaService areaService;
 
   @Autowired
   private IStoreClassService storeClassService;
 
   @Autowired
   private AreaViewTools areaViewTools;
 
   @Autowired
   private GoodsViewTools goodsViewTools;
 
   @Autowired
   private StoreViewTools storeViewTools;
 
   @Autowired
   private UserTools userTools;
 
   @Autowired
   private TransportTools transportTools;
 
   @RequestMapping({"/goods_list.htm"})
   public ModelAndView goods_list(HttpServletRequest request, HttpServletResponse response, String gc_id, String store_id, String recommend, String currentPage, String orderBy, String orderType, String begin_price, String end_price)
   {
     UserGoodsClass ugc = this.userGoodsClassService.getObjById(
       CommUtil.null2Long(gc_id));
     String template = "default";
     Store store = this.storeService
       .getObjById(CommUtil.null2Long(store_id));
     if (store != null) {
       if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
         template = store.getTemplate();
       }
       ModelAndView mv = new JModelAndView(template + "/goods_list.html", 
         this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, 
         orderBy, orderType);
       gqo.addQuery("obj.goods_store.id", 
         new SysMap("goods_store_id", 
         store.getId()), "=");
       if (ugc != null) {
         Set<Long> ids = genericUserGcIds(ugc);
         List ugc_list = new ArrayList();
         for (Long g_id : ids) {
           UserGoodsClass temp_ugc = this.userGoodsClassService
             .getObjById(g_id);
           ugc_list.add(temp_ugc);
         }
         gqo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
         for (int i = 0; i < ugc_list.size(); i++)
           gqo.addQuery("ugc" + i, ugc_list.get(i), "obj.goods_ugcs", 
             "member of", "or");
       }
       else {
         ugc = new UserGoodsClass();
         ugc.setClassName("全部商品");
         mv.addObject("ugc", ugc);
       }
       if ((recommend != null) && (!recommend.equals(""))) {
         gqo.addQuery("obj.goods_recommend", 
           new SysMap("goods_recommend", Boolean.valueOf(CommUtil.null2Boolean(recommend))), 
           "=");
       }
       gqo.setPageSize(Integer.valueOf(20));
       if ((begin_price != null) && (!begin_price.equals(""))) {
         gqo.addQuery("obj.store_price", 
           new SysMap("begin_price", 
           BigDecimal.valueOf(CommUtil.null2Double(begin_price))), 
           ">=");
       }
       if ((end_price != null) && (!end_price.equals(""))) {
         gqo.addQuery("obj.store_price", 
           new SysMap("end_price", 
           BigDecimal.valueOf(CommUtil.null2Double(end_price))), 
           "<=");
       }
       IPageList pList = this.goodsService.list(gqo);
       String url = this.configService.getSysConfig().getAddress();
       if ((url == null) || (url.equals(""))) {
         url = CommUtil.getURL(request);
       }
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
       mv.addObject("ugc", ugc);
       mv.addObject("store", store);
       mv.addObject("recommend", recommend);
       mv.addObject("begin_price", begin_price);
       mv.addObject("end_price", end_price);
       mv.addObject("goodsViewTools", this.goodsViewTools);
       mv.addObject("storeViewTools", this.storeViewTools);
       mv.addObject("areaViewTools", this.areaViewTools);
       return mv;
     }
     ModelAndView mv = new JModelAndView("error.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, 
       response);
     mv.addObject("op_title", "请求参数错误");
     mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     return mv;
   }
 
   private Set<Long> genericUserGcIds(UserGoodsClass ugc)
   {
     Set ids = new HashSet();
     ids.add(ugc.getId());
     for (UserGoodsClass child : ugc.getChilds()) {
       Set<Long> cids = genericUserGcIds(child);
       for (Long cid : cids) {
         ids.add(cid);
       }
       ids.add(child.getId());
     }
     return ids;
   }
 
   @RequestMapping({"/goods.htm"})
   public ModelAndView goods(HttpServletRequest request, HttpServletResponse response, String id)
   {
     ModelAndView mv = null;
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
     Goods obj = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
     if (obj.getGoods_status() == 0) {
       String template = "default";
       if ((obj.getGoods_store().getTemplate() != null) && 
         (!obj.getGoods_store().getTemplate().equals(""))) {
         template = obj.getGoods_store().getTemplate();
       }
       mv = new JModelAndView(template + "/store_goods.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, response);
       
	   if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
		   mv = new JModelAndView("wap/store_goods.html", this.configService.getSysConfig(), 
			         this.userConfigService.getUserConfig(), 1, request, response);
	   }
       obj.setGoods_click(obj.getGoods_click() + 1);
       if ((this.configService.getSysConfig().isZtc_status()) && (obj.getZtc_status() == 2)) {
         obj.setZtc_click_num(obj.getZtc_click_num() + 1);
       }
       if ((obj.getGroup() != null) && (obj.getGroup_buy() == 2)) {
         Group group = obj.getGroup();
         if (group.getEndTime().before(new Date())) {
           obj.setGroup(null);
           obj.setGroup_buy(0);
           obj.setGoods_current_price(obj.getStore_price());
         }
       }
 
       this.goodsService.update(obj);
       if (obj.getGoods_store().getStore_status() == 2) {
         mv.addObject("obj", obj);
         mv.addObject("store", obj.getGoods_store());
         Map params = new HashMap();
         params.put("user_id", obj.getGoods_store().getUser().getId());
         params.put("display", Boolean.valueOf(true));
         List ugcs = this.userGoodsClassService.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc", params, -1, -1);
         mv.addObject("ugcs", ugcs);
         GoodsQueryObject gqo = new GoodsQueryObject();
         gqo.setPageSize(Integer.valueOf(4));
         gqo.addQuery("obj.goods_store.id", new SysMap("store_id", obj.getGoods_store().getId()), "=");
         gqo.addQuery("obj.goods_recommend", new SysMap("goods_recommend", Boolean.valueOf(true)), "=");
         gqo.addQuery("obj.id", new SysMap("id", obj.getId()), "!=");
         gqo.setOrderBy("addTime");
         gqo.setOrderType("desc");
         gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
         mv.addObject("goods_recommend_list", this.goodsService.list(gqo).getResult());
         params.clear();
         params.put("goods_id", obj.getId());
         params.put("evaluate_type", "buyer");
         List evas = this.evaluateService.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type", params, -1, -1);
         mv.addObject("eva_count", Integer.valueOf(evas.size()));
         mv.addObject("goodsViewTools", this.goodsViewTools);
         mv.addObject("storeViewTools", this.storeViewTools);
         mv.addObject("areaViewTools", this.areaViewTools);
         mv.addObject("transportTools", this.transportTools);
 
         List<Goods> user_viewed_goods = (List)request.getSession(false).getAttribute("user_viewed_goods");
         if (user_viewed_goods == null) {
        	 user_viewed_goods = new ArrayList();
         }
         boolean add = true;
         for (Goods goods : user_viewed_goods) {
           if (goods.getId().equals(obj.getId())) {
             add = false;
             break;
           }
         }
         if (add) {
           if (user_viewed_goods.size() >= 4)
             user_viewed_goods.set(1, obj);
           else
             user_viewed_goods.add(obj);
         }
         request.getSession(false).setAttribute("user_viewed_goods", user_viewed_goods);
 
         IpAddress ipAddr = IpAddress.getInstance();
         String current_ip = CommUtil.getIpAddr(request);
         String current_city = ipAddr.IpStringToAddress(current_ip);
         if ((current_city == null) || (current_city.equals(""))) {
           current_city = "全国";
         }
 
         mv.addObject("current_city", current_city);
 
         List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
         mv.addObject("areas", areas);
         generic_evaluate(obj.getGoods_store(), mv);
       } else {
    	   mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
    			   this.userConfigService.getUserConfig(), 1, request, response);
    	   if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
    		   mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), 
    		           this.userConfigService.getUserConfig(), 1, request, response);
    	   }
    	   mv.addObject("op_title", "店铺够开通，拒绝访问");
    	   mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
       }
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
    	   mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       }
       mv.addObject("op_title", "该商品未上架，不允许查看");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
 
   @RequestMapping({"/store_goods_list.htm"})
   public ModelAndView store_goods_list(HttpServletRequest request, HttpServletResponse response, String gc_id, String currentPage, String orderBy, String orderType, String store_price_begin, String store_price_end, String brand_ids, String gs_ids, String properties, String op, String goods_name, String area_name, String area_id, String goods_view, String all_property_status, String detail_property_status)
   {
     ModelAndView mv = new JModelAndView("store_goods_list.html", this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
	 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
		 mv = new JModelAndView("wap/store_goods_list.html", this.configService.getSysConfig(), 
			       this.userConfigService.getUserConfig(), 1, request, response);
	 }
     GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
     mv.addObject("gc", gc);
     if ((orderBy == null) || (orderBy.equals(""))) {
       orderBy = "addTime";
     }
     if ((op != null) && (!op.equals(""))) {
       mv.addObject("op", op);
     }
     String orderBy1 = orderBy;
     if (this.configService.getSysConfig().isZtc_status()) {
       orderBy = "ztc_dredge_price desc,obj." + orderBy;
     }
     GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
     Set ids = genericIds(gc);
     Map paras = new HashMap();
     paras.put("ids", ids);
     gqo.addQuery("obj.gc.id in (:ids)", paras);
     if ((store_price_begin != null) && (!store_price_begin.equals(""))) {
       gqo.addQuery("obj.store_price", new SysMap("store_price_begin", BigDecimal.valueOf(CommUtil.null2Double(store_price_begin))), ">=");
       mv.addObject("store_price_begin", store_price_begin);
     }
     if ((store_price_end != null) && (!store_price_end.equals(""))) {
       gqo.addQuery("obj.store_price", new SysMap("store_price_end", BigDecimal.valueOf(CommUtil.null2Double(store_price_end))), "<=");
       mv.addObject("store_price_end", store_price_end);
     }
     if ((goods_name != null) && (!goods_name.equals(""))) {
       gqo.addQuery("obj.goods_name", new SysMap("name", "%" + goods_name.trim() + "%"), "like");
       mv.addObject("goods_name", goods_name);
     }
 
     if ((area_id != null) && (!area_id.equals(""))) {
       Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
       mv.addObject("area", area);
       Set area_ids = getAreaChildIds(area);
       Map p_area = new HashMap();
       p_area.put("area_ids", area_ids);
       gqo.addQuery("obj.goods_store.area.id in (:area_ids)", p_area);
     }
     if ((area_name != null) && (!area_name.equals(""))) {
       mv.addObject("area_name", area_name);
       Map like_area = new HashMap();
       like_area.put("area_name", area_name + "%");
       List likes_areas = this.areaService.query("select obj from Area obj where obj.areaName like:area_name", like_area, -1, -1);
       Set like_area_ids = getArrayAreaChildIds(likes_areas);
       like_area.clear();
       like_area.put("like_area_ids", like_area_ids);
       gqo.addQuery("obj.goods_store.area.id in (:like_area_ids)", like_area);
     }
 
     gqo.addQuery("obj.goods_store.store_status", new SysMap("store_status", Integer.valueOf(2)), "=");
     gqo.setPageSize(Integer.valueOf(20));
     gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
     List goods_property = new ArrayList();
     if (!CommUtil.null2String(brand_ids).equals("")) {
       String[] brand_id_list = brand_ids.substring(1).split("\\|");
       if (brand_id_list.length == 1) {
         String brand_id = brand_id_list[0];
         String[] brand_info_list = brand_id.split(",");
         gqo.addQuery("obj.goods_brand.id", new SysMap("brand_id", CommUtil.null2Long(brand_info_list[0])), "=", "and");
         Map map = new HashMap();
         GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_info_list[0]));
         map.put("name", "品牌");
         map.put("value", brand.getName());
         map.put("type", "brand");
         map.put("id", brand.getId());
         goods_property.add(map);
       } else {
         for (int i = 0; i < brand_id_list.length; i++) {
           String brand_id = brand_id_list[i];
           if (i == 0) {
             String[] brand_info_list = brand_id.split(",");
             gqo.addQuery("and (obj.goods_brand.id=" + CommUtil.null2Long(brand_info_list[0]), null);
             Map map = new HashMap();
             GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_info_list[0]));
             map.put("name", "品牌");
             map.put("value", brand.getName());
             map.put("type", "brand");
             map.put("id", brand.getId());
             goods_property.add(map);
           } else if (i == brand_id_list.length - 1) {
             String[] brand_info_list = brand_id.split(",");
             gqo.addQuery("or obj.goods_brand.id=" + CommUtil.null2Long(brand_info_list[0]) + ")", null);
             Map map = new HashMap();
             GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_info_list[0]));
             map.put("name", "品牌");
             map.put("value", brand.getName());
             map.put("type", "brand");
             map.put("id", brand.getId());
             goods_property.add(map);
           } else {
             String[] brand_info_list = brand_id.split(",");
             gqo.addQuery("or obj.goods_brand.id=" + CommUtil.null2Long(brand_info_list[0]), null);
             Map map = new HashMap();
             GoodsBrand brand = this.brandService.getObjById(CommUtil.null2Long(brand_info_list[0]));
             map.put("name", "品牌");
             map.put("value", brand.getName());
             map.put("type", "brand");
             map.put("id", brand.getId());
             goods_property.add(map);
           }
         }
       }
       mv.addObject("brand_ids", brand_ids);
     }
     if (!CommUtil.null2String(gs_ids).equals("")) {
       List gsp_lists = generic_gsp(gs_ids);
 
       for (int j = 0; j < gsp_lists.size(); j++) {
         List gsp_list = (List)gsp_lists.get(j);
         if (gsp_list.size() == 1) {
           GoodsSpecProperty gsp = (GoodsSpecProperty)gsp_list.get(0);
           gqo.addQuery("gsp" + j, gsp, "obj.goods_specs", "member of", "and");
           Map map = new HashMap();
           map.put("name", gsp.getSpec().getName());
           map.put("value", gsp.getValue());
           map.put("type", "gs");
           map.put("id", gsp.getId());
           goods_property.add(map);
         } else {
           for (int i = 0; i < gsp_list.size(); i++) {
             if (i == 0) {
               GoodsSpecProperty gsp = (GoodsSpecProperty)gsp_list.get(i);
               gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs", "member of", "and(");
               Map map = new HashMap();
               map.put("name", gsp.getSpec().getName());
               map.put("value", gsp.getValue());
               map.put("type", "gs");
               map.put("id", gsp.getId());
               goods_property.add(map);
             } else if (i == gsp_list.size() - 1) {
               GoodsSpecProperty gsp = (GoodsSpecProperty)gsp_list.get(i);
               gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs)", "member of", "or");
               Map map = new HashMap();
               map.put("name", gsp.getSpec().getName());
               map.put("value", gsp.getValue());
               map.put("type", "gs");
               map.put("id", gsp.getId());
               goods_property.add(map);
             } else {
               GoodsSpecProperty gsp = (GoodsSpecProperty)gsp_list.get(i);
               gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs", "member of", "or");
               Map map = new HashMap();
               map.put("name", gsp.getSpec().getName());
               map.put("value", gsp.getValue());
               map.put("type", "gs");
               map.put("id", gsp.getId());
               goods_property.add(map);
             }
           }
         }
       }
       mv.addObject("gs_ids", gs_ids);
     }
     if (!CommUtil.null2String(properties).equals("")) {
       String[] properties_list = properties.substring(1).split("\\|");
       for (int i = 0; i < properties_list.length; i++) {
         String property_info = properties_list[i];
         String[] property_info_list = property_info.split(",");
         GoodsTypeProperty gtp = this.goodsTypePropertyService.getObjById(CommUtil.null2Long(property_info_list[0]));
 
         Map p_map = new HashMap();
         p_map.put("gtp_name" + i, "%" + gtp.getName().trim() + "%");
         p_map.put("gtp_value" + i, "%" + property_info_list[1].trim() + "%");
         gqo.addQuery("and (obj.goods_property like :gtp_name" + i + " and obj.goods_property like :gtp_value" + i + ")", p_map);
         Map map = new HashMap();
         map.put("name", gtp.getName());
         map.put("value", property_info_list[1]);
         map.put("type", "properties");
         map.put("id", gtp.getId());
         goods_property.add(map);
       }
       mv.addObject("properties", properties);
     }
     Map params = new HashMap();
     params.put("common", Boolean.valueOf(true));
     List areas = this.areaService.query("select obj from Area obj where obj.common=:common order by sequence asc", params, -1, -1);
     mv.addObject("areas", areas);
 
     IPageList pList = this.goodsService.list(gqo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("gc", gc);
     mv.addObject("orderBy", orderBy1);
     mv.addObject("user_viewed_goods", request.getSession(false).getAttribute("user_viewed_goods"));
     mv.addObject("goods_property", goods_property);
     if (CommUtil.null2String(goods_view).equals("list"))
       goods_view = "list";
     else {
       goods_view = "thumb";
     }
 
     if (this.configService.getSysConfig().isZtc_status()) {
       List ztc_goods = null;
       Map ztc_map = new HashMap();
       ztc_map.put("ztc_status", Integer.valueOf(3));
       ztc_map.put("now_date", new Date());
       ztc_map.put("ztc_gold", Integer.valueOf(0));
       if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
         ztc_goods = this.goodsService.query("select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc", ztc_map, 0, 5);
       }
       if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
         ztc_map.put("gc_ids", ids);
         ztc_goods = this.goodsService.query("select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) order by obj.ztc_dredge_price desc", ztc_map, 0, 5);
       }
       mv.addObject("ztc_goods", ztc_goods);
     }
     if ((detail_property_status != null) && 
       (!detail_property_status.equals(""))) {
       mv.addObject("detail_property_status", detail_property_status);
       String[] temp_str = detail_property_status.split(",");
       Map pro_map = new HashMap();
       List pro_list = new ArrayList();
       for (String property_status : temp_str) {
         if ((property_status != null) && (!property_status.equals(""))) {
           String[] mark = property_status.split("_");
           pro_map.put(mark[0], mark[1]);
           pro_list.add(mark[0]);
         }
       }
       mv.addObject("pro_list", pro_list);
       mv.addObject("pro_map", pro_map);
     }
     mv.addObject("goods_view", goods_view);
     mv.addObject("all_property_status", all_property_status);
     return mv;
   }
 
   private Set<Long> getArrayAreaChildIds(List<Area> areas) {
     Set ids = new HashSet();
     for (Area area : areas) {
       ids.add(area.getId());
       for (Area are : area.getChilds()) {
         Set<Long> cids = getAreaChildIds(are);
         for (Long cid : cids) {
           ids.add(cid);
         }
       }
     }
     return ids;
   }
 
   @RequestMapping({"/ztc_goods_list.htm"})
   public ModelAndView ztc_goods_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String goods_view)
   {
     ModelAndView mv = new JModelAndView("ztc_goods_list.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, 
       orderType);
     gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
     gqo.addQuery("obj.ztc_status", new SysMap("ztc_status", Integer.valueOf(3)), "=");
     gqo.addQuery("obj.ztc_begin_time", 
       new SysMap("ztc_begin_time", 
       new Date()), "<=");
     gqo.addQuery("obj.ztc_gold", new SysMap("ztc_gold", Integer.valueOf(0)), ">");
     gqo.setOrderBy("ztc_dredge_price");
     gqo.setOrderType("desc");
     gqo.setPageSize(Integer.valueOf(20));
     IPageList pList = this.goodsService.list(gqo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("goods_view", goods_view);
     mv.addObject("user_viewed_goods", request.getSession(false)
       .getAttribute("user_viewed_goods"));
     return mv;
   }
 
   private Set<Long> getAreaChildIds(Area area) {
     Set ids = new HashSet();
     ids.add(area.getId());
     for (Area are : area.getChilds()) {
       Set<Long> cids = getAreaChildIds(are);
       for (Long cid : cids) {
         ids.add(cid);
       }
     }
     return ids;
   }
 
   private List<List<GoodsSpecProperty>> generic_gsp(String gs_ids) {
     List<List<GoodsSpecProperty>> list = new ArrayList<List<GoodsSpecProperty>>();
     String[] gs_id_list = gs_ids.substring(1).split("\\|");
     for (String gd_id_info : gs_id_list) {
       String[] gs_info_list = gd_id_info.split(",");
       GoodsSpecProperty gsp = this.goodsSpecPropertyService
         .getObjById(CommUtil.null2Long(gs_info_list[0]));
       boolean create = true;
       for (List<GoodsSpecProperty> gsp_list : list) {
         for (GoodsSpecProperty gsp_temp : gsp_list)
         {
           if (gsp_temp.getSpec().getId()
             .equals(gsp.getSpec().getId())) {
             gsp_list.add(gsp);
             create = false;
             break;
           }
         }
       }
       if (create) {
         List gsps = new ArrayList();
         gsps.add(gsp);
         list.add(gsps);
       }
     }
     return list;
   }
 
   @RequestMapping({"/goods_evaluation.htm"})
   public ModelAndView goods_evaluation(HttpServletRequest request, HttpServletResponse response, String id, String goods_id, String currentPage)
   {
     String template = "default";
     Store store = this.storeService.getObjById(CommUtil.null2Long(id));
     if (store != null) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(
       template + "/goods_evaluation.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, 
       "addTime", "desc");
     qo.addQuery("obj.evaluate_goods.id", 
       new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
     qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), 
       "=");
     qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), 
       "=");
     qo.setPageSize(Integer.valueOf(8));
     IPageList pList = this.evaluateService.list(qo);
     CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
       "/goods_evaluation.htm", "", "", pList, mv);
     mv.addObject("storeViewTools", this.storeViewTools);
     mv.addObject("store", store);
     Goods goods = this.goodsService
       .getObjById(CommUtil.null2Long(goods_id));
     mv.addObject("goods", goods);
     return mv;
   }
 
   @RequestMapping({"/goods_detail.htm"})
   public ModelAndView goods_detail(HttpServletRequest request, HttpServletResponse response, String id, String goods_id) {
     String template = "default";
     Store store = this.storeService.getObjById(CommUtil.null2Long(id));
     if (store != null) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/goods_detail.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     Goods goods = this.goodsService
       .getObjById(CommUtil.null2Long(goods_id));
     mv.addObject("obj", goods);
     generic_evaluate(goods.getGoods_store(), mv);
     this.userTools.query_user();
     return mv;
   }
 
   @RequestMapping({"/goods_order.htm"})
   public ModelAndView goods_order(HttpServletRequest request, HttpServletResponse response, String id, String goods_id, String currentPage)
   {
     String template = "default";
     Store store = this.storeService.getObjById(CommUtil.null2Long(id));
     if (store != null) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/goods_order.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     GoodsCartQueryObject qo = new GoodsCartQueryObject(currentPage, mv, 
       "addTime", "desc");
     qo.addQuery("obj.goods.id", 
       new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
     qo.addQuery("obj.of.order_status", new SysMap("order_status", Integer.valueOf(20)), ">=");
     qo.setPageSize(Integer.valueOf(8));
     IPageList pList = this.goodsCartService.list(qo);
     CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
       "/goods_order.htm", "", "", pList, mv);
     mv.addObject("storeViewTools", this.storeViewTools);
     return mv;
   }
 
   @RequestMapping({"/goods_consult.htm"})
   public ModelAndView goods_consult(HttpServletRequest request, HttpServletResponse response, String id, String goods_id, String currentPage)
   {
     String template = "default";
     Store store = this.storeService.getObjById(CommUtil.null2Long(id));
     if (store != null) {
       template = store.getTemplate();
     }
     ModelAndView mv = new JModelAndView(template + "/goods_consult.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv, 
       "addTime", "desc");
     qo.addQuery("obj.goods.id", 
       new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
     IPageList pList = this.consultService.list(qo);
     CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
       "/goods_consult.htm", "", "", pList, mv);
     mv.addObject("storeViewTools", this.storeViewTools);
     mv.addObject("goods_id", goods_id);
     return mv;
   }
 
   @RequestMapping({"/goods_consult_save.htm"})
   public ModelAndView goods_consult_save(HttpServletRequest request, HttpServletResponse response, String goods_id, String consult_content, String consult_email, String Anonymous, String consult_code)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/success.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String verify_code = CommUtil.null2String(request.getSession(false)
       .getAttribute("consult_code"));
     boolean visit_consult = true;
     if (!this.configService.getSysConfig().isVisitorConsult()) {
       if (SecurityUserHolder.getCurrentUser() == null) {
         visit_consult = false;
       }
       if (CommUtil.null2Boolean(Anonymous)) {
         visit_consult = false;
       }
     }
     if (visit_consult) {
       if (CommUtil.null2String(consult_code).equals(verify_code)) {
         Consult obj = new Consult();
         obj.setAddTime(new Date());
         obj.setConsult_content(consult_content);
         obj.setConsult_email(consult_email);
         if (!CommUtil.null2Boolean(Anonymous)) {
           obj.setConsult_user(SecurityUserHolder.getCurrentUser());
           mv.addObject("op_title", "咨询发布成功");
         }
         obj.setGoods(this.goodsService.getObjById(
           CommUtil.null2Long(goods_id)));
         this.consultService.save(obj);
         request.getSession(false).removeAttribute("consult_code");
       } else {
         mv = new JModelAndView("error.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "验证码错误，咨询发布失败");
       }
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "不允许游客咨询");
     }
     mv.addObject("url", CommUtil.getURL(request) + "/goods_" + goods_id + 
       ".htm");
     return mv;
   }
 
   /**
    * 根据商品规格查询商品规格价格
	 * @param request
	 * @param response
	 * @param gsp 规格
	 * @param id
	 */
	@RequestMapping({"/load_goods_gsp.htm"})
   public void load_goods_gsp(HttpServletRequest request, HttpServletResponse response, String gsp, String id) {
     Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
     Map map = new HashMap();
     int count = 0;
     double price = 0.0D;
     if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
       for (GroupGoods gg : goods.getGroup_goods_list())
         if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
           count = gg.getGg_group_count() - gg.getGg_def_count();
           price = CommUtil.null2Double(gg.getGg_price());
         }
     }
     else {
       count = goods.getGoods_inventory();
       price = CommUtil.null2Double(goods.getStore_price());
       if (goods.getInventory_type().equals("spec")) {
         List<Map> list = (List)Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
         String[] gsp_ids = gsp.split(",");
         for (Map temp : list) {
           String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
           Arrays.sort(gsp_ids);
           Arrays.sort(temp_ids);
           if (Arrays.equals(gsp_ids, temp_ids)) {
             count = CommUtil.null2Int(temp.get("count"));
             price = CommUtil.null2Double(temp.get("price"));
           }
         }
       }
     }
     map.put("count", Integer.valueOf(count));
     map.put("price", Double.valueOf(price));
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(Json.toJson(map, JsonFormat.compact()));
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @RequestMapping({"/trans_fee.htm"})
   public void trans_fee(HttpServletRequest request, HttpServletResponse response, String city_name, String goods_id) {
	   Map map = new HashMap();
       Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
       float mail_fee = 0.0F;
       float express_fee = 0.0F;
       float ems_fee = 0.0F;
       if (goods.getTransport() != null) {
           if(goods.getTransport().getTrans_mail_info()!=null) {
               mail_fee = this.transportTools.cal_goods_trans_fee(
                       CommUtil.null2String(goods.getTransport().getId()), "mail",
                       CommUtil.null2String(goods.getGoods_weight()),
                       CommUtil.null2String(goods.getGoods_volume()), city_name);
           }
           if(goods.getTransport().getTrans_express_info()!=null) {
               express_fee = this.transportTools.cal_goods_trans_fee(
                       CommUtil.null2String(goods.getTransport().getId()), "express",
                       CommUtil.null2String(goods.getGoods_weight()),
                       CommUtil.null2String(goods.getGoods_volume()), city_name);
           }
           if(goods.getTransport().getTrans_ems_info()!=null) {
               ems_fee = this.transportTools.cal_goods_trans_fee(
                       CommUtil.null2String(goods.getTransport().getId()), "ems",
                       CommUtil.null2String(goods.getGoods_weight()),
                       CommUtil.null2String(goods.getGoods_volume()), city_name);
           }
       }
       map.put("mail_fee", Float.valueOf(mail_fee));
       map.put("express_fee", Float.valueOf(express_fee));
       map.put("ems_fee", Float.valueOf(ems_fee));
       map.put("current_city_info", CommUtil.substring(city_name, 5));
       response.setContentType("text/plain");
       response.setHeader("Cache-Control", "no-cache");
       response.setCharacterEncoding("UTF-8");
       try
       {
           PrintWriter writer = response.getWriter();
           writer.print(Json.toJson(map, JsonFormat.compact()));
       }
       catch (IOException e) {
           e.printStackTrace();
       }
   }
 
   @RequestMapping({"/goods_share.htm"})
   public ModelAndView goods_share(HttpServletRequest request, HttpServletResponse response, String goods_id) {
     ModelAndView mv = new JModelAndView("goods_share.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     Goods goods = this.goodsService
       .getObjById(CommUtil.null2Long(goods_id));
     mv.addObject("obj", goods);
     return mv;
   }
 
   private Set<Long> genericIds(GoodsClass gc) {
     Set ids = new HashSet();
     ids.add(gc.getId());
     for (GoodsClass child : gc.getChilds()) {
       Set<Long> cids = genericIds(child);
       for (Long cid : cids) {
         ids.add(cid);
       }
       ids.add(child.getId());
     }
     return ids;
   }
 
   private void generic_evaluate(Store store, ModelAndView mv) {
     double description_result = 0.0D;
     double service_result = 0.0D;
     double ship_result = 0.0D;
     if (store.getSc() != null) {
       StoreClass sc = this.storeClassService.getObjById(store.getSc()
         .getId());
       float description_evaluate = CommUtil.null2Float(sc
         .getDescription_evaluate());
       float service_evaluate = CommUtil.null2Float(sc
         .getService_evaluate());
       float ship_evaluate = CommUtil.null2Float(sc.getShip_evaluate());
       if (store.getPoint() != null) {
         float store_description_evaluate = CommUtil.null2Float(store
           .getPoint().getDescription_evaluate());
         float store_service_evaluate = CommUtil.null2Float(store
           .getPoint().getService_evaluate());
         float store_ship_evaluate = CommUtil.null2Float(store
           .getPoint().getShip_evaluate());
 
         description_result = CommUtil.div(Float.valueOf(store_description_evaluate - 
           description_evaluate), Float.valueOf(description_evaluate));
         service_result = CommUtil.div(Float.valueOf(store_service_evaluate - 
           service_evaluate), Float.valueOf(service_evaluate));
         ship_result = CommUtil.div(Float.valueOf(store_ship_evaluate - ship_evaluate), 
           Float.valueOf(ship_evaluate));
       }
     }
     if (description_result > 0.0D) {
       mv.addObject("description_css", "better");
       mv.addObject("description_type", "高于");
       mv.addObject("description_result", 
         CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(description_result), Integer.valueOf(100)))) + 
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


 
 
 