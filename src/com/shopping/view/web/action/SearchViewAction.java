 package com.shopping.view.web.action;
 
 import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.Area;
import com.shopping.foundation.domain.Goods;
import com.shopping.foundation.domain.GoodsClass;
import com.shopping.foundation.domain.StoreClass;
import com.shopping.foundation.domain.query.StoreQueryObject;
import com.shopping.foundation.service.IAreaService;
import com.shopping.foundation.service.IGoodsService;
import com.shopping.foundation.service.IStoreClassService;
import com.shopping.foundation.service.IStoreGradeService;
import com.shopping.foundation.service.IStoreService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.IUserConfigService;
import com.shopping.lucene.LuceneResult;
import com.shopping.lucene.LuceneUtil;
import com.shopping.lucene.LuceneVo;
import com.shopping.view.web.tools.StoreViewTools;
 
 @Controller
 public class SearchViewAction
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
   private StoreViewTools storeViewTools;
 
   @Autowired
   private IStoreGradeService storeGradeService;
 
   @Autowired
   private IAreaService areaService;
 
   @RequestMapping({"/search.htm"})
   public ModelAndView search(HttpServletRequest request, HttpServletResponse response, String type, String keyword, String currentPage, String orderBy, String orderType, String store_price_begin, String store_price_end, String view_type, String sc_id, String storeGrade_id, String checkbox_id, String storepoint, String area_id, String area_name, String goods_view)
   {
     ModelAndView mv = new JModelAndView("search_goods_list.html", 
       this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     if ((type == null) || (type.equals("")))
       type = "goods";
     keyword = CommUtil.decode(keyword);
     
     String shopping_view_type = CommUtil.null2String(request.getSession(false).getAttribute("shopping_view_type"));
		
	 if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("wap"))) {
		 mv = new JModelAndView("wap/search.html", 
			       this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
	 }
	 
     if (type.equals("store")) {
       mv = new JModelAndView("store_list.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, response);
       
       if ((shopping_view_type != null) && (!shopping_view_type.equals("")) && (shopping_view_type.equals("wap"))) {
  		 mv = new JModelAndView("wap/store_list.html", 
  			       this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
  	   }
       StoreQueryObject sqo = new StoreQueryObject(currentPage, mv, "addTime", "desc");
       if ((keyword != null) && (!keyword.equals(""))) {
         sqo.addQuery("obj.store_name", new SysMap("store_name", "%" + keyword + "%"), "like");
         mv.addObject("store_name", keyword);
       }
       if ((sc_id != null) && (!sc_id.equals(""))) {
         StoreClass storeclass = this.storeClassService.getObjById(CommUtil.null2Long(sc_id));
         Set ids = getStoreClassChildIds(storeclass);
         Map map = new HashMap();
         map.put("ids", ids);
         sqo.addQuery("obj.sc.id in (:ids)", map);
         mv.addObject("sc_id", sc_id);
       }
       if ((storeGrade_id != null) && (!storeGrade_id.equals(""))) {
         sqo.addQuery("obj.grade.id", new SysMap("grade_id", CommUtil.null2Long(storeGrade_id)), "=");
         mv.addObject("storeGrade_id", storeGrade_id);
       }
       if ((orderBy != null) && (!orderBy.equals(""))) {
         sqo.setOrderBy(orderBy);
         if (orderBy.equals("addTime"))
           orderType = "asc";
         else {
           orderType = "desc";
         }
         sqo.setOrderType(orderType);
         mv.addObject("orderBy", orderBy);
         mv.addObject("orderType", orderType);
       }
       if ((checkbox_id != null) && (!checkbox_id.equals(""))) {
         sqo.addQuery("obj." + checkbox_id, new SysMap("obj_checkbox_id", Boolean.valueOf(true)), "=");
         mv.addObject("checkbox_id", checkbox_id);
       }
       if ((storepoint != null) && (!storepoint.equals(""))) {
         sqo.addQuery("obj.sp.store_evaluate1", new SysMap("sp_store_evaluate1", new BigDecimal(storepoint)), ">=");
         mv.addObject("storepoint", storepoint);
       }
       if ((area_id != null) && (!area_id.equals(""))) {
         mv.addObject("area_id", area_id);
         Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
         Set area_ids = getAreaChildIds(area);
         Map params = new HashMap();
         params.put("ids", area_ids);
         sqo.addQuery("obj.area.id in (:ids)", params);
       }
       if ((area_name != null) && (!area_name.equals(""))) {
         mv.addObject("area_name", area_name);
         sqo.addQuery("obj.area.areaName", new SysMap("areaName", "%" + area_name.trim() + "%"), "like");
         sqo.addQuery("obj.area.parent.areaName", new SysMap("areaName", "%" + area_name.trim() + "%"), "like", "or");
         sqo.addQuery("obj.area.parent.parent.areaName", new SysMap("areaName", "%" + area_name.trim() + "%"), "like", "or");
       }
       sqo.addQuery("obj.store_status", new SysMap("store_status", Integer.valueOf(2)), "=");
       sqo.setPageSize(Integer.valueOf(20));
       IPageList pList = this.storeService.list(sqo);
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
       List scs = this.storeClassService.query("select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
 
       Map map = new HashMap();
       map.put("common", Boolean.valueOf(true));
       List areas = this.areaService.query("select obj from Area obj where obj.common =:common order by sequence asc", map, -1, -1);
       mv.addObject("areas", areas);
       mv.addObject("storeViewTools", this.storeViewTools);
       mv.addObject("scs", scs);
       List storeGrades = this.storeGradeService.query("select obj from StoreGrade obj order by sequence asc", null, -1, -1);
       mv.addObject("storeGrades", storeGrades);
     }
     if ((type.equals("goods")) && (!CommUtil.null2String(keyword).equals(""))) {
       String path = System.getProperty("user.dir") + File.separator + "luence" + File.separator + "goods";
       LuceneUtil lucene = LuceneUtil.instance();
       LuceneUtil.setIndex_path(path);
       boolean order_type = true;
       String order_by = "";
       if (CommUtil.null2String(orderType).equals("asc")) {
         order_type = false;
       }
       if (CommUtil.null2String(orderType).equals("")) {
         orderType = "desc";
       }
       if (CommUtil.null2String(orderBy).equals("store_price")) {
         order_by = "store_price";
       }
       if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
         order_by = "goods_salenum";
       }
       if (CommUtil.null2String(orderBy).equals("goods_collect")) {
         order_by = "goods_collect";
       }
       if (CommUtil.null2String(orderBy).equals("goods_addTime")) {
         order_by = "addTime";
       }
       Sort sort = null;
       if (!CommUtil.null2String(order_by).equals("")) {
         sort = new Sort(new SortField(order_by, 7, order_type));
       }
       LuceneResult pList = lucene.search(keyword, 
         CommUtil.null2Int(currentPage), 
         CommUtil.null2Int(store_price_begin), 
         CommUtil.null2Int(store_price_end), null, sort);
       for (LuceneVo vo : pList.getVo_list()) {
         Goods goods = this.goodsService.getObjById(vo.getVo_id());
         pList.getGoods_list().add(goods);
       }
       CommUtil.saveLucene2ModelAndView("goods", pList, mv);
       GoodsClass gc = new GoodsClass();
       gc.setClassName("商品搜索结果");
       mv.addObject("gc", gc);
       mv.addObject("store_price_end", store_price_end);
       mv.addObject("store_price_begin", store_price_begin);
       mv.addObject("keyword", keyword);
       mv.addObject("orderBy", orderBy);
       mv.addObject("orderType", orderType);
       if (CommUtil.null2String(goods_view).equals("list"))
         goods_view = "list";
       else {
         goods_view = "thumb";
       }

       if (this.configService.getSysConfig().isZtc_status()) {
         Object ztc_map = new HashMap();
         ((Map)ztc_map).put("ztc_status", Integer.valueOf(3));
         ((Map)ztc_map).put("now_date", new Date());
         ((Map)ztc_map).put("ztc_gold", Integer.valueOf(0));
         List ztc_goods = this.goodsService
           .query("select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc", (Map)ztc_map, 0, 5);
         mv.addObject("ztc_goods", ztc_goods);
       }
       mv.addObject("goods_view", goods_view);
     }
     if (CommUtil.null2String(view_type).equals("")) {
       view_type = "list";
     }
     mv.addObject("view_type", view_type);
     mv.addObject("type", type);
     return (ModelAndView)mv;
   }
   
   @RequestMapping({"/search_ajax.htm"})
   public void searchAjax(HttpServletRequest request, HttpServletResponse response, String keyword, String currentPage, String orderBy, String orderType, String store_price_begin, String store_price_end, String view_type, String sc_id, String storeGrade_id, String checkbox_id, String storepoint)
   {
	   Map<String, Object> map = new HashMap<String, Object>();
	   
       keyword = CommUtil.decode(keyword);
       
       String path = System.getProperty("user.dir") + File.separator + "luence" + File.separator + "goods";
       LuceneUtil lucene = LuceneUtil.instance();
       LuceneUtil.setIndex_path(path);
       boolean order_type = true;
       String order_by = "";
       if (CommUtil.null2String(orderType).equals("asc")) {
         order_type = false;
       }
       if (CommUtil.null2String(orderType).equals("")) {
         orderType = "desc";
       }
       if (CommUtil.null2String(orderBy).equals("store_price")) {
         order_by = "store_price";
       }
       if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
         order_by = "goods_salenum";
       }
       if (CommUtil.null2String(orderBy).equals("goods_collect")) {
         order_by = "goods_collect";
       }
       if (CommUtil.null2String(orderBy).equals("goods_addTime")) {
         order_by = "addTime";
       }
       
       Sort sort = null;
       
       if (!CommUtil.null2String(order_by).equals("")) {
         sort = new Sort(new SortField(order_by, 7, order_type));
       }
       
       LuceneResult pList = lucene.search(keyword, CommUtil.null2Int(currentPage), 
         CommUtil.null2Int(store_price_begin), CommUtil.null2Int(store_price_end), null, sort);
       
       for (LuceneVo vo : pList.getVo_list()) {
         Goods goods = this.goodsService.getObjById(vo.getVo_id());
         pList.getGoods_list().add(goods);
       }
       map.put("store_price_end", store_price_end);
       map.put("store_price_begin", store_price_begin);
       map.put("keyword", keyword);
       map.put("orderBy", orderBy);
       map.put("orderType", orderType);
       
       CommUtil.saveWebPaths(map, this.configService.getSysConfig(), request);
       
       CommUtil.saveLucene2Map("goods", pList, map);
       
       /*if (this.configService.getSysConfig().isZtc_status()) {
         Object ztc_map = new HashMap();
         ((Map)ztc_map).put("ztc_status", Integer.valueOf(3));
         ((Map)ztc_map).put("now_date", new Date());
         ((Map)ztc_map).put("ztc_gold", Integer.valueOf(0));
         List ztc_goods = this.goodsService.query("select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc", (Map)ztc_map, 0, 5);
         mv.addObject("ztc_goods", ztc_goods);
       }*/
       
       if (CommUtil.null2String(view_type).equals("")) {
    	   view_type = "list";
       }
       map.put("view_type", view_type);
       
       String ret = Json.toJson(map, JsonFormat.compact());
       response.setContentType("text/plain");
       response.setHeader("Cache-Control", "no-cache");
       response.setCharacterEncoding("UTF-8");
       try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
       } catch (IOException e) {
			e.printStackTrace();
       }
   }
   
   
 
   private Set<Long> getStoreClassChildIds(StoreClass sc) {
     Set ids = new HashSet();
     ids.add(sc.getId());
     for (StoreClass storeclass : sc.getChilds()) {
       Set<Long> cids = getStoreClassChildIds(storeclass);
       for (Long cid : cids) {
         ids.add(cid);
       }
     }
     return ids;
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
 }


 
 
 