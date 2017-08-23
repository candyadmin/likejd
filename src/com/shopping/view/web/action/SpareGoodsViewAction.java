 package com.shopping.view.web.action;
 
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Area;
 import com.shopping.foundation.domain.SpareGoods;
 import com.shopping.foundation.domain.SpareGoodsClass;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.SpareGoodsQueryObject;
 import com.shopping.foundation.service.IAreaService;
 import com.shopping.foundation.service.INavigationService;
 import com.shopping.foundation.service.ISpareGoodsClassService;
 import com.shopping.foundation.service.ISpareGoodsFloorService;
 import com.shopping.foundation.service.ISpareGoodsService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.view.web.tools.SpareGoodsViewTools;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class SpareGoodsViewAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private ISpareGoodsClassService sparegoodsclassService;
 
   @Autowired
   private ISpareGoodsFloorService sparegoodsfloorService;
 
   @Autowired
   private ISpareGoodsService sparegoodsService;
 
   @Autowired
   private IAreaService areaService;
 
   @Autowired
   private SpareGoodsViewTools SpareGoodsTools;
 
   @Autowired
   private INavigationService navService;
 
   @RequestMapping({"/sparegoods_head.htm"})
   public ModelAndView sparegoods_head(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView("sparegoods_head.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 1, request, response);
     return mv;
   }
 
   @RequestMapping({"/sparegoods_nav.htm"})
   public ModelAndView sparegoods_nav(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView("sparegoods_nav.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 1, request, response);
     List sgcs = this.sparegoodsclassService
       .query(
       "select obj from SpareGoodsClass obj where obj.parent.id is null order by sequence asc", 
       null, -1, -1);
     mv.addObject("sgcs", sgcs);
     if ((SecurityUserHolder.getCurrentUser() != null) && 
       (!SecurityUserHolder.getCurrentUser().equals("")))
     {
       Map params = new HashMap();
       params.put("status", Integer.valueOf(0));
       params.put("down", Integer.valueOf(0));
       params.put("uid", SecurityUserHolder.getCurrentUser().getId());
       List sps = this.sparegoodsService
         .query(
         "select obj from SpareGoods obj where obj.status=:status and obj.down=:down and obj.user.id=:uid", 
         params, -1, -1);
 
       params.clear();
       params.put("status", Integer.valueOf(-1));
       params.put("uid", SecurityUserHolder.getCurrentUser().getId());
       List drops = this.sparegoodsService
         .query(
         "select obj from SpareGoods obj where obj.status=:status and obj.user.id=:uid", 
         params, -1, -1);
 
       params.clear();
       params.put("down", Integer.valueOf(-1));
       params.put("uid", SecurityUserHolder.getCurrentUser().getId());
       List down = this.sparegoodsService
         .query(
         "select obj from SpareGoods obj where obj.down=:down and obj.user.id=:uid", 
         params, -1, -1);
       mv.addObject("selling", Integer.valueOf(sps.size()));
       mv.addObject("drops", Integer.valueOf(drops.size()));
       mv.addObject("down", Integer.valueOf(down.size()));
     }
     Map map = new HashMap();
     map.put("type", "sparegoods");
     map.put("display", Boolean.valueOf(true));
     List navs = this.navService
       .query(
       "select obj from Navigation obj where obj.type=:type and obj.display=:display order by sequence asc", 
       map, -1, -1);
     mv.addObject("navs", navs);
     return mv;
   }
 
   @RequestMapping({"/sparegoods_nav2.htm"})
   public ModelAndView sparegoods_nav2(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView("sparegoods_nav2.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 1, request, response);
     List sgcs = this.sparegoodsclassService
       .query(
       "select obj from SpareGoodsClass obj where obj.parent.id is null order by sequence asc", 
       null, -1, -1);
     mv.addObject("sgcs", sgcs);
     if ((SecurityUserHolder.getCurrentUser() != null) && 
       (!SecurityUserHolder.getCurrentUser().equals("")))
     {
       Map params = new HashMap();
       params.put("status", Integer.valueOf(0));
       params.put("down", Integer.valueOf(0));
       params.put("uid", SecurityUserHolder.getCurrentUser().getId());
       List sps = this.sparegoodsService
         .query(
         "select obj from SpareGoods obj where obj.status=:status and obj.down=:down and obj.user.id=:uid", 
         params, -1, -1);
 
       params.clear();
       params.put("status", Integer.valueOf(-1));
       params.put("uid", SecurityUserHolder.getCurrentUser().getId());
       List drops = this.sparegoodsService
         .query(
         "select obj from SpareGoods obj where obj.status=:status and obj.user.id=:uid", 
         params, -1, -1);
 
       params.clear();
       params.put("down", Integer.valueOf(-1));
       params.put("uid", SecurityUserHolder.getCurrentUser().getId());
       List down = this.sparegoodsService
         .query(
         "select obj from SpareGoods obj where obj.down=:down and obj.user.id=:uid", 
         params, -1, -1);
       mv.addObject("selling", Integer.valueOf(sps.size()));
       mv.addObject("drops", Integer.valueOf(drops.size()));
       mv.addObject("down", Integer.valueOf(down.size()));
     }
     Map map = new HashMap();
     map.put("type", "sparegoods");
     map.put("display", Boolean.valueOf(true));
     List navs = this.navService
       .query(
       "select obj from Navigation obj where obj.type=:type and obj.display=:display order by sequence asc", 
       map, -1, -1);
     mv.addObject("navs", navs);
     return mv;
   }
 
   @RequestMapping({"/sparegoods.htm"})
   public ModelAndView sparegoods(HttpServletRequest request, HttpServletResponse response, String currentPage) {
     ModelAndView mv = new JModelAndView("sparegoods.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     Map map = new HashMap();
     map.put("display", Boolean.valueOf(true));
     List floors = this.sparegoodsfloorService
       .query(
       "select obj from SpareGoodsFloor obj where obj.display=:display order By sequence asc", 
       map, -1, -1);
     List sgcs = this.sparegoodsclassService
       .query(
       "select obj from SpareGoodsClass obj where obj.parent.id is null order by sequence asc", 
       null, -1, -1);
     mv.addObject("sgcs", sgcs);
     mv.addObject("floors", floors);
     mv.addObject("SpareGoodsTools", this.SpareGoodsTools);
     return mv;
   }
 
   @RequestMapping({"/sparegoods_detail.htm"})
   public ModelAndView sparegoods_detail(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView("sparegoods_detail.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 1, request, response);
     SpareGoods obj = this.sparegoodsService.getObjById(
       CommUtil.null2Long(id));
     if (obj.getStatus() == 0) {
       mv.addObject("obj", obj);
     }
     if (obj.getStatus() == -1) {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("url", CommUtil.getURL(request) + "/sparegoods.htm");
       mv.addObject("op_title", "该商品已下架!");
     }
     if (obj.getDown() == -1) {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("url", CommUtil.getURL(request) + "/sparegoods.htm");
       mv.addObject("op_title", "该商品因违规已下架!");
     }
 
     return mv;
   }
 
   @RequestMapping({"/sparegoods_search.htm"})
   public ModelAndView sparegoods_search(HttpServletRequest request, HttpServletResponse response, String cid, String orderBy, String orderType, String currentPage, String price_begin, String price_end, String keyword, String area_id)
   {
     ModelAndView mv = new JModelAndView("sparegoods_search.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 1, request, response);
     if ((orderType != null) && (!orderType.equals(""))) {
       if (orderType.equals("asc"))
         orderType = "desc";
       else
         orderType = "asc";
     }
     else {
       orderType = "desc";
     }
     if ((orderBy != null) && (!orderBy.equals(""))) {
       if (orderBy.equals("addTime"))
         orderType = "desc";
     }
     else {
       orderBy = "addTime";
     }
     SpareGoodsQueryObject qo = new SpareGoodsQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.status", new SysMap("status", Integer.valueOf(0)), "=");
     qo.addQuery("obj.down", new SysMap("down", Integer.valueOf(0)), "=");
     if ((cid != null) && (!cid.equals(""))) {
       SpareGoodsClass sgc = this.sparegoodsclassService
         .getObjById(CommUtil.null2Long(cid));
       Set ids = genericIds(sgc);
       Map map = new HashMap();
       map.put("ids", ids);
       qo.addQuery("obj.spareGoodsClass.id in (:ids)", map);
       mv.addObject("cid", cid);
       mv.addObject("sgc", sgc);
     }
     if ((orderBy != null) && (!orderBy.equals(""))) {
       if (orderBy.equals("recommend")) {
         qo.addQuery("obj.recommend", new SysMap("obj_recommend", Boolean.valueOf(true)), 
           "=");
       }
       if ((price_begin != null) && (!price_begin.equals(""))) {
         qo.addQuery("obj.goods_price", 
           new SysMap("goods_price", 
           Integer.valueOf(CommUtil.null2Int(price_begin))), ">=");
       }
       if ((price_end != null) && (!price_end.equals(""))) {
         qo.addQuery("obj.goods_price", 
           new SysMap("goods_end", 
           Integer.valueOf(CommUtil.null2Int(price_end))), "<=");
       }
     }
     if ((keyword != null) && (!keyword.equals(""))) {
       qo.addQuery("obj.title", 
         new SysMap("obj_title", "%" + 
         keyword.trim() + "%"), "like");
     }
     if ((area_id != null) && (!area_id.equals(""))) {
       qo.addQuery("obj.area.parent.id", 
         new SysMap("obj_area_id", 
         CommUtil.null2Long(area_id)), "=");
       Area area = this.areaService
         .getObjById(CommUtil.null2Long(area_id));
       mv.addObject("area", area);
     }
     IPageList pList = this.sparegoodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     List citys = this.areaService.query(
       "select obj from Area obj where obj.parent.id is null", null, 
       -1, -1);
     mv.addObject("citys", citys);
     mv.addObject("area_id", area_id);
     mv.addObject("keyword", keyword);
     mv.addObject("price_begin", price_begin);
     mv.addObject("price_end", price_end);
     mv.addObject("allCount", Integer.valueOf(pList.getRowCount()));
     mv.addObject("SpareGoodsTools", this.SpareGoodsTools);
     return mv;
   }
 
   private Set<Long> genericIds(SpareGoodsClass gc) {
     Set ids = new HashSet();
     ids.add(gc.getId());
     for (SpareGoodsClass child : gc.getChilds()) {
       Set<Long> cids = genericIds(child);
       for (Long cid : cids) {
         ids.add(cid);
       }
       ids.add(child.getId());
     }
     return ids;
   }
 }


 
 
 