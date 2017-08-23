 package com.shopping.view.web.action;
 
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.GoodsCart;
 import com.shopping.foundation.domain.Group;
 import com.shopping.foundation.domain.GroupArea;
 import com.shopping.foundation.domain.GroupGoods;
 import com.shopping.foundation.domain.GroupPriceRange;
 import com.shopping.foundation.domain.Store;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.GoodsQueryObject;
 import com.shopping.foundation.domain.query.GroupGoodsQueryObject;
 import com.shopping.foundation.domain.query.GroupQueryObject;
 import com.shopping.foundation.service.IGoodsCartService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.IGroupAreaService;
 import com.shopping.foundation.service.IGroupClassService;
 import com.shopping.foundation.service.IGroupGoodsService;
 import com.shopping.foundation.service.IGroupPriceRangeService;
 import com.shopping.foundation.service.IGroupService;
 import com.shopping.foundation.service.IOrderFormService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import com.shopping.view.web.tools.GroupViewTools;
 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.Date;
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
 public class GroupViewAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGroupService groupService;
 
   @Autowired
   private IGroupAreaService groupAreaService;
 
   @Autowired
   private IGroupPriceRangeService groupPriceRangeService;
 
   @Autowired
   private IGroupClassService groupClassService;
 
   @Autowired
   private IGroupGoodsService groupGoodsService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IOrderFormService orderFormService;
 
   @Autowired
   private IGoodsCartService goodsCartService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private GroupViewTools groupViewTools;
 
   @RequestMapping({"/group.htm"})
   public ModelAndView group(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String gc_id, String gpr_id, String ga_id)
   {
     ModelAndView mv = new JModelAndView("group.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     Map params = new HashMap();
     params.put("beginTime", new Date());
     params.put("endTime", new Date());
     List groups = this.groupService
       .query(
       "select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime", 
       params, -1, -1);
     if (groups.size() > 0) {
       GroupGoodsQueryObject ggqo = new GroupGoodsQueryObject(currentPage, 
         mv, orderBy, orderType);
       ggqo.addQuery("obj.group.id", 
         new SysMap("group_id", ((Group)groups.get(0))
         .getId()), "=");
       if ((gc_id != null) && (!gc_id.equals(""))) {
         ggqo.addQuery("obj.gg_gc.id", 
           new SysMap("gc_id", 
           CommUtil.null2Long(gc_id)), "=");
       }
       if ((ga_id != null) && (!ga_id.equals(""))) {
         ggqo.addQuery("obj.gg_ga.id", 
           new SysMap("ga_id", 
           CommUtil.null2Long(ga_id)), "=");
         mv.addObject("ga_id", ga_id);
       }
       GroupPriceRange gpr = this.groupPriceRangeService
         .getObjById(CommUtil.null2Long(gpr_id));
       if (gpr != null) {
         ggqo.addQuery("obj.gg_price", 
           new SysMap("begin_price", 
           BigDecimal.valueOf(gpr.getGpr_begin())), ">=");
         ggqo.addQuery("obj.gg_price", 
           new SysMap("end_price", 
           BigDecimal.valueOf(gpr.getGpr_end())), "<=");
       }
       ggqo.addQuery("obj.gg_status", new SysMap("gg_status", Integer.valueOf(1)), "=");
       IPageList pList = this.groupGoodsService.list(ggqo);
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
       List gcs = this.groupClassService
         .query(
         "select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc", 
         null, -1, -1);
       List gprs = this.groupPriceRangeService
         .query(
         "select obj from GroupPriceRange obj order by obj.gpr_begin asc", 
         null, -1, -1);
       mv.addObject("gprs", gprs);
       mv.addObject("gcs", gcs);
       mv.addObject("group", groups.get(0));
       if ((orderBy == null) || (orderBy.equals(""))) {
         orderBy = "addTime";
       }
       if ((orderType == null) || (orderType.equals(""))) {
         orderType = "desc";
       }
       mv.addObject("order_type", CommUtil.null2String(orderBy) + "_" + 
         CommUtil.null2String(orderType));
       mv.addObject("gc_id", gc_id);
       mv.addObject("gpr_id", gpr_id);
     }
     return mv;
   }
 
   @RequestMapping({"/group_head.htm"})
   public ModelAndView group_head(HttpServletRequest request, HttpServletResponse response, String ga_id) {
     ModelAndView mv = new JModelAndView("group_head.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     List gas = this.groupAreaService
       .query(
       "select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc", 
       null, -1, -1);
     mv.addObject("gas", gas);
     if ((ga_id != null) && (!ga_id.equals("")))
       mv.addObject("ga", this.groupAreaService.getObjById(
         CommUtil.null2Long(ga_id)).getGa_name());
     else {
       mv.addObject("ga", "全国");
     }
     return mv;
   }
 
   @RequestMapping({"/group_view.htm"})
   public ModelAndView group_view(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView("group_view.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     GroupGoods obj = this.groupGoodsService.getObjById(
       CommUtil.null2Long(id));
     User user = SecurityUserHolder.getCurrentUser();
     boolean view = false;
     if ((obj.getGroup().getBeginTime().before(new Date())) && 
       (obj.getGroup().getEndTime().after(new Date()))) {
       view = true;
     }
     if ((user != null) && (user.getUserRole().indexOf("ADMIN") >= 0)) {
       view = true;
     }
     if (view) {
       mv.addObject("obj", obj);
       Map params = new HashMap();
       params.put("beginTime", new Date());
       params.put("endTime", new Date());
       params.put("status", Integer.valueOf(0));
       List groups = this.groupService
         .query(
         "select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.status=:status", 
         params, -1, -1);
       if (groups.size() > 0) {
         GroupGoodsQueryObject ggqo = new GroupGoodsQueryObject("1", mv, 
           "gg_recommend", "desc");
         ggqo.addQuery("obj.gg_status", new SysMap("gg_status", Integer.valueOf(1)), "=");
         ggqo.addQuery("obj.group.id", 
           new SysMap("group_id", obj
           .getGroup().getId()), "=");
         ggqo.addQuery("obj.id", new SysMap("goods_id", obj.getId()), 
           "!=");
         ggqo.setPageSize(Integer.valueOf(4));
         IPageList pList = this.groupGoodsService.list(ggqo);
         mv.addObject("hot_ggs", pList.getResult());
         mv.addObject("group", groups.get(0));
       }
       GoodsQueryObject gqo = new GoodsQueryObject("1", mv, "addTime", 
         "desc");
       gqo.addQuery("obj.goods_store.id", 
         new SysMap("store_id", obj
         .getGg_goods().getGoods_store().getId()), "=");
       gqo.addQuery("obj.goods_recommend", 
         new SysMap("goods_recommend", 
         Boolean.valueOf(true)), "=");
       gqo
         .addQuery("obj.goods_status", 
         new SysMap("goods_status", Integer.valueOf(0)), "=");
       gqo.setPageSize(Integer.valueOf(2));
       mv.addObject("recommend_goods", this.goodsService.list(gqo)
         .getResult());
       params.clear();
       params.put("gg", obj);
       List<GoodsCart> gc_list = this.goodsCartService
         .query(
         "select obj from GoodsCart obj where :gg member of obj.goods.group_goods_list", 
         params, 0, 4);
       List gcs = new ArrayList();
       for (GoodsCart gc : gc_list) {
         if (!gcs.contains(gc))
           gcs.add(gc);
       }
       mv.addObject("gcs", gcs);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "团购商品参数错误");
       mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
     }
     return mv;
   }
 
   @RequestMapping({"/group_list.htm"})
   public ModelAndView group_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String time) {
     ModelAndView mv = new JModelAndView("group_list.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     GroupQueryObject gqo = new GroupQueryObject(currentPage, mv, "addTime", 
       "desc");
     if (time.equals("soon")) {
       gqo.addQuery("obj.beginTime", new SysMap("beginTime", new Date()), 
         ">");
     }
     if (time.equals("history")) {
       gqo.addQuery("obj.endTime", new SysMap("endTime", new Date()), "<");
     }
     IPageList pList = this.groupService.list(gqo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("time", time);
     mv.addObject("groupViewTools", this.groupViewTools);
     return mv;
   }
 }


 
 
 