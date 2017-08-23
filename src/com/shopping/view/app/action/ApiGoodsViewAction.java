package com.shopping.view.app.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.Accessory;
import com.shopping.foundation.domain.Goods;
import com.shopping.foundation.domain.GoodsSpecProperty;
import com.shopping.foundation.domain.GoodsSpecification;
import com.shopping.foundation.domain.Group;
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
import com.shopping.view.web.tools.StoreViewTools;

@Controller
public class ApiGoodsViewAction {

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
	
	/**
	 * @param gc_id 		商品类型
	 * @param store_id		店铺ID
	 * @param beginCount	开始记录数
	 * @param maxCount		查询记录数
	 * @param orderBy		排序
	 * @param orderType		排序类型
	 * @param begin_price	开始价格
	 * @param end_price		结束价格
	 */
	@RequestMapping({"/app/goods_list.htm"})
	public void goods_list(HttpServletRequest request, HttpServletResponse response, String gc_id, String store_id, String beginCount, String maxCount, String orderBy, String orderType, String begin_price, String end_price)
	{
		boolean verify = true;
	    Map json_map = new HashMap();
	    if ((orderBy == null) || (orderBy.equals(""))) {
	      orderBy = "goods_salenum";
	    }
	    if ((orderType == null) || (orderType.equals(""))) {
	      orderType = "desc";
	    }
	    if ((verify) && (orderBy != null) && (!orderBy.equals("")))
	    {
	      List<Goods> goods_list = null;
	      Map params = new HashMap();
	      params.put("goods_status", Integer.valueOf(0));
	      
	      String query = "select obj from Goods obj where 1=1 and obj.goods_status=:goods_status ";
	      
	      if ((gc_id != null) && (!gc_id.equals("")))
	      {
	        params.put("gc_id", CommUtil.null2Long(gc_id));
	        query = query + "and obj.gc.id=:gc_id   ";
	      }
	      /*if ((gb_id != null) && (!gb_id.equals("")))
	      {
	        params.put("gb_id", CommUtil.null2Long(gb_id));
	        query = query + "and obj.goods_brand.id=:gb_id  ";
	      }*/
	      /*if ((keyword != null) && (!keyword.equals("")))
	      {
	        params.put("keyword", "%" + keyword + "%");
	        query = query + "and obj.goods_name like:keyword  ";
	      }*/
	      if ((store_id != null) && (!store_id.equals("")))
	      {
	        params.put("store_id", CommUtil.null2Long(store_id));
	        query = query + "and obj.goods_store.id=:store_id";
	      }
	      
	      query = query + " order by ";
	      
	      String url = CommUtil.getURL(request);
	      if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
	        url = this.configService.getSysConfig().getImageWebServer();
	      }
	      
	      goods_list = this.goodsService.query(query + orderBy + " " + 
	    		  orderType, params, CommUtil.null2Int(beginCount), CommUtil.null2Int(maxCount));
	      List map_list = new ArrayList();
	      for (Goods obj : goods_list)
	      {
	        Map goods_map = new HashMap();
	        goods_map.put("id", obj.getId());
	        goods_map.put("goods_name", obj.getGoods_name());
	        goods_map.put("goods_current_price", CommUtil.null2String(obj.getGoods_current_price()));
	        goods_map.put("goods_salenum", Integer.valueOf(obj.getGoods_salenum()));
	        String goods_main_photo = url + "/" + this.configService.getSysConfig().getGoodsImage().getPath() 
	        		+ "/" + this.configService.getSysConfig().getGoodsImage().getName();
	        if (obj.getGoods_main_photo() != null) {
	          goods_main_photo = url + "/" + obj.getGoods_main_photo().getPath() + "/" + obj.getGoods_main_photo().getName() 
	            + "_middle." + obj.getGoods_main_photo().getExt();
	        }
	        goods_map.put("goods_main_photo", goods_main_photo);
	        
	        map_list.add(goods_map);
	      }
	      json_map.put("goods_list", map_list);
	    }
	    json_map.put("code", CommUtil.null2String(Boolean.valueOf(verify)));
	    String json = JSON.toJSONString(json_map);
	    response.setContentType("text/plain");
	    response.setHeader("Cache-Control", "no-cache");
	    response.setCharacterEncoding("UTF-8");
	    try
	    {
	      PrintWriter writer = response.getWriter();
	      writer.print(json);
	    }
	    catch (IOException e)
	    {
	      e.printStackTrace();
	    }
	}
	
	/**
	 * @param request
	 * @param response
	 * @param id	商品ID
	 */
	@RequestMapping({"/app/goods.htm"})
	public void goods(HttpServletRequest request, HttpServletResponse response, String id)
	  {
	    Map goods_map = new HashMap();
	    Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
	    if ((obj.getGroup() != null) && (obj.getGroup_buy() == 2))
	    {
	      Group group = obj.getGroup();
	      if (group.getEndTime().before(new Date()))
	      {
	        obj.setGroup(null);
	        obj.setGroup_buy(0);
	        obj.setGoods_current_price(obj.getStore_price());
	      }
	    }
	    
	    this.goodsService.update(obj);
	    
	    goods_map.put("id", obj.getId());
	    goods_map.put("favorite", "false");
	    //商品名称
	    goods_map.put("goods_name", obj.getGoods_name());
	    //商品详情
	    goods_map.put("goods_details", obj.getGoods_details());
	    //商品分类ID
	    goods_map.put("goods_class_id", obj.getGc().getId());
	    //商品分类名称
	    goods_map.put("goods_class_name", obj.getGc().getClassName());
	    //商品价格
	    goods_map.put("goods_price", CommUtil.null2String(obj.getGoods_price()));
	    goods_map.put("goods_current_price", CommUtil.null2String(obj.getGoods_current_price()));
	    goods_map.put("goods_inventory", Integer.valueOf(obj.getGoods_inventory()));
	    goods_map.put("inventory_type", obj.getInventory_type());
	    goods_map.put("goods_salenum", Integer.valueOf(obj.getGoods_salenum()));
	    goods_map.put("goods_fee", obj.getGoods_fee());
	    
	    List<GoodsSpecification> specs = new ArrayList<GoodsSpecification>();
	    for (GoodsSpecProperty gsp : obj.getGoods_specs()) {
	         GoodsSpecification spec = gsp.getSpec();
	         if (!specs.contains(spec)) {
	           specs.add(spec);
	         }
	    }
	    Collections.sort(specs, new Comparator() {
	       public int compare(Object gs1, Object gs2) {
	    	   return (((GoodsSpecification)gs1).getSequence()) - (((GoodsSpecification)gs2).getSequence());
	       }
		});
	    List listspecs = new ArrayList();
	    for(GoodsSpecification spec : specs){
	    	Map map = new HashMap();
	    	map.put("specname", spec.getName());
	    	List<String> listvalue = new ArrayList<String>();
	    	for(GoodsSpecProperty gsp : obj.getGoods_specs()) {
	    		if(gsp.getSpec().getId()==spec.getId()){
	    			listvalue.add(gsp.getValue());
	    		}
	    	}
	    	map.put("specvalues", listvalue);
	    	listspecs.add(map);
	    }
	    //商品规格
	    goods_map.put("spec_list", listspecs);
	    
	    goods_map.put("evaluate_count", Integer.valueOf(obj.getEvaluates().size()));
	    
	    String url = CommUtil.getURL(request);
	    if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
	      url = this.configService.getSysConfig().getImageWebServer();
	    }
	    List photo_list = new ArrayList();
	    photo_list.add(url + "/" + obj.getGoods_main_photo().getPath() + "/" + obj.getGoods_main_photo().getName());
	    for (Accessory acc : obj.getGoods_photos()) {
	      photo_list.add(url + "/" + acc.getPath() + "/" + acc.getName());
	    }
	    goods_map.put("goods_photos", photo_list);
	    goods_map.put("goods_photos_small", url + "/" + 
	      obj.getGoods_main_photo().getPath() + "/" + 
	      obj.getGoods_main_photo().getName() + "_small." + 
	      obj.getGoods_main_photo().getExt());
	    
	    //goods_map.put("ret", "true");
	    String json = JSON.toJSONString(goods_map);
	    response.setContentType("text/plain");
	    response.setHeader("Cache-Control", "no-cache");
	    response.setCharacterEncoding("UTF-8");
	    try
	    {
	      PrintWriter writer = response.getWriter();
	      writer.print(json);
	    }
	    catch (IOException e)
	    {
	      e.printStackTrace();
	    }
	  }

	@RequestMapping({"/app/goods_specs.htm"})
	public void goods_specs(HttpServletRequest request, HttpServletResponse response, String id) {
	    Map map = new HashMap();
	    List specs = new ArrayList();
	     if ((id != null) && (!id.equals(""))) {
	       Goods goods = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
	       for (GoodsSpecProperty gsp : goods.getGoods_specs()) {
	         GoodsSpecification spec = gsp.getSpec();
	         if (!specs.contains(spec)) {
	           specs.add(spec);
	         }
	       }
	     }
	     Collections.sort(specs, new Comparator() {
	       public int compare(Object gs1, Object gs2) {
	         return (((GoodsSpecification)gs1).getSequence()) - (((GoodsSpecification)gs2).getSequence());
	       }
	    });
	    map.put("spec_list", specs);
	    map.put("ret", "true");
	    String json = JSON.toJSONString(map);
	    response.setContentType("text/plain");
	    response.setHeader("Cache-Control", "no-cache");
	    response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter writer = response.getWriter();
	      writer.print(json);
	    }
	    catch (IOException e) {
	      e.printStackTrace();
	    }
	}
}
