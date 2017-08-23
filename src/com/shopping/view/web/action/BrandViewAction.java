 package com.shopping.view.web.action;
 
 import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.GoodsBrand;
import com.shopping.foundation.domain.query.GoodsQueryObject;
import com.shopping.foundation.service.IGoodsBrandCategoryService;
import com.shopping.foundation.service.IGoodsBrandService;
import com.shopping.foundation.service.IGoodsClassService;
import com.shopping.foundation.service.IGoodsService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.IUserConfigService;
import com.shopping.view.web.tools.StoreViewTools;
 
 @Controller
 public class BrandViewAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGoodsClassService goodsClassService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IGoodsBrandService goodsBrandService;
 
   @Autowired
   private IGoodsBrandCategoryService goodsBrandCategorySerivce;
 
   @Autowired
   private StoreViewTools storeViewTools;
 
   @RequestMapping({"/brand.htm"})
   public ModelAndView brand(HttpServletRequest request, HttpServletResponse response, String gbc_id)
   {
     ModelAndView mv = new JModelAndView("brand.html", this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     
     String shopping_view_type = CommUtil.null2String( request.getSession( false ).getAttribute( "shopping_view_type" ) );
     
	 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
		 mv = new JModelAndView("wap/brand.html", this.configService.getSysConfig(), 
			       this.userConfigService.getUserConfig(), 1, request, response);
	 }
     List gbcs = this.goodsBrandCategorySerivce.query("select obj from GoodsBrandCategory obj  order by obj.addTime asc", null, -1, -1);
     Map params = new HashMap();
     params.put("recommend", Boolean.valueOf(true));
     params.put("audit", Integer.valueOf(1));
     List gbs = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.recommend=:recommend and obj.audit=:audit order by obj.sequence asc", params, 0, 10);
     mv.addObject("gbs", gbs);
     mv.addObject("gbcs", gbcs);
     List<GoodsBrand> brands = new ArrayList();
     if ((gbc_id != null) && (!gbc_id.equals(""))) {
       mv.addObject("gbc_id", gbc_id);
       params.clear();
       params.put("gbc_id", CommUtil.null2Long(gbc_id));
       params.put("audit", Integer.valueOf(1));
       brands = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.category.id=:gbc_id and obj.audit=:audit order by obj.sequence asc", params, -1, -1);
     } else {
       params.clear();
       params.put("audit", Integer.valueOf(1));
       brands = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.audit=:audit order by obj.sequence asc", params, -1, -1);
     }
     List all_list = new ArrayList();
     String list_word = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
     String[] words = list_word.split(",");
     for (String word : words) {
       Map brand_map = new HashMap();
       List brand_list = new ArrayList();
       for (GoodsBrand gb : brands) {
         if ((CommUtil.null2String(gb.getFirst_word()).equals("")) || 
           (!word.equals(gb.getFirst_word().toUpperCase()))) continue;
         brand_list.add(gb);
       }
 
       brand_map.put("brand_list", brand_list);
       brand_map.put("word", word);
       all_list.add(brand_map);
     }
     mv.addObject("all_list", all_list);
     return mv;
   }
 
   @RequestMapping({"/brand_goods.htm"})
   public ModelAndView brand_view(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String orderBy, String orderType, String store_price_begin, String store_price_end, String op, String goods_name)
   {
     ModelAndView mv = new JModelAndView("brand_goods.html", this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 1, request, response);
     
     String shopping_view_type = CommUtil.null2String( request.getSession( false ).getAttribute( "shopping_view_type" ) );
     
	 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
		 mv = new JModelAndView("wap/brand_goods.html", this.configService.getSysConfig(), 
			       this.userConfigService.getUserConfig(), 1, request, response);
	 }
     if ((op != null) && (!op.equals(""))) {
       mv.addObject("op", op);
     }
     GoodsBrand gb = this.goodsBrandService.getObjById(CommUtil.null2Long(id));
     mv.addObject("gb", gb);
     Map params = new HashMap();
     params.put("recommend", Boolean.valueOf(true));
     params.put("audit", Integer.valueOf(1));
     List gbs = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.recommend=:recommend and obj.audit=:audit order by obj.sequence asc", params, 0, 10);
     mv.addObject("gbs", gbs);
     mv.addObject("storeViewTools", this.storeViewTools);
     GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
     if ((store_price_begin != null) && (!store_price_begin.equals(""))) {
       gqo.addQuery("obj.store_price", new SysMap("store_price_begin", BigDecimal.valueOf(CommUtil.null2Float(store_price_begin))), ">=");
       mv.addObject("store_price_begin", store_price_begin);
     }
     if ((store_price_end != null) && (!store_price_end.equals(""))) {
       gqo.addQuery("obj.store_price", new SysMap("store_price_end", BigDecimal.valueOf(CommUtil.null2Float(store_price_end))), "<=");
       mv.addObject("store_price_end", store_price_end);
     }
     if ((goods_name != null) && (!goods_name.equals(""))) {
       gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + goods_name.trim() + "%"), "like");
       mv.addObject("goods_name", goods_name);
     }
     gqo.addQuery("obj.goods_brand.id", new SysMap("goods_brand_id", gb.getId()), "=");
     gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
     gqo.setPageSize(Integer.valueOf(20));
     IPageList pList = this.goodsService.list(gqo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
   
    /**
     * wap根据品牌查询商品
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param store_price_begin
	 * @param store_price_end
	 * @param op
	 * @param goods_name
	 * @return
	 */
	@RequestMapping({"/brand_goods_ajax.htm"})
   public void brand_view_ajax(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String orderBy, String orderType, String store_price_begin, String store_price_end, String op, String goods_name)
   {
	 Map<String, Object> map = new HashMap<String, Object>();
	 if ((op != null) && (!op.equals(""))) {
		 map.put("op", op);
	 }
     GoodsBrand gb = this.goodsBrandService.getObjById(CommUtil.null2Long(id));
     
     GoodsQueryObject gqo = new GoodsQueryObject(currentPage, map, orderBy, orderType);
     
     if ((store_price_begin != null) && (!store_price_begin.equals(""))) {
       gqo.addQuery("obj.store_price", new SysMap("store_price_begin", BigDecimal.valueOf(CommUtil.null2Float(store_price_begin))), ">=");
       map.put("store_price_begin", store_price_begin);
     }
     
     if ((store_price_end != null) && (!store_price_end.equals(""))) {
       gqo.addQuery("obj.store_price", new SysMap("store_price_end", BigDecimal.valueOf(CommUtil.null2Float(store_price_end))), "<=");
       map.put("store_price_end", store_price_end);
     }
     
     if ((goods_name != null) && (!goods_name.equals(""))) {
       gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + goods_name.trim() + "%"), "like");
       map.put("goods_name", goods_name);
     }
     
     gqo.addQuery("obj.goods_brand.id", new SysMap("goods_brand_id", gb.getId()), "=");
     gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
     gqo.setPageSize(Integer.valueOf(12));
     IPageList pList = this.goodsService.list(gqo);
     
     CommUtil.saveWebPaths(map, this.configService.getSysConfig(), request);
     map.put("show", "brand_goods");
     CommUtil.saveIPageList2Map("", "", "", pList, map);
     
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
 
 
 }


 
 
 