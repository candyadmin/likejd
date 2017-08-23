 package com.shopping.manage.buyer.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.domain.Dynamic;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.HomePage;
 import com.shopping.foundation.domain.SnsAttention;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.Visit;
 import com.shopping.foundation.domain.query.DynamicQueryObject;
 import com.shopping.foundation.domain.query.SnsAttentionQueryObject;
 import com.shopping.foundation.domain.query.SnsFriendQueryObject;
 import com.shopping.foundation.service.IDynamicService;
 import com.shopping.foundation.service.IFavoriteService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.IHomePageGoodsClassService;
 import com.shopping.foundation.service.IHomePageService;
 import com.shopping.foundation.service.IOrderFormService;
 import com.shopping.foundation.service.ISnsAttentionService;
 import com.shopping.foundation.service.ISnsFriendService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import com.shopping.foundation.service.IVisitService;
 import java.io.IOException;
 import java.io.PrintWriter;
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
 public class HomePageBuyerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IVisitService visitService;
 
   @Autowired
   private IHomePageService homePageService;
 
   @Autowired
   private IDynamicService dynamicService;
 
   @Autowired
   private ISnsAttentionService attentionService;
 
   @Autowired
   private ISnsFriendService snsFriendService;
 
   @Autowired
   private IFavoriteService favoriteService;
 
   @Autowired
   private IOrderFormService orderFormService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IHomePageGoodsClassService HomeGoodsClassService;
 
   @SecurityMapping(display = false, rsequence = 0, title="个人主页头部", value="/buyer/homepage_head.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_head.htm"})
   public ModelAndView homepage_head(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/homepage_head.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String uid = request.getParameter("uid");
     User user = new User();
     if ((uid != null) && (!uid.equals("")))
       user = this.userService.getObjById(CommUtil.null2Long(uid));
     else {
       user = SecurityUserHolder.getCurrentUser();
     }
     mv.addObject("owner", user);
     Map map = new HashMap();
     map.put("uid", user.getId());
     List HomePages = this.homePageService.query(
       "select obj from HomePage obj where obj.owner.id=:uid", map, 
       -1, -1);
     if (HomePages.size() > 0) {
       mv.addObject("homePage", HomePages.get(0));
     }
     map.clear();
     map.put("uid", user.getId());
     List fans = this.attentionService.query(
       "select obj from SnsAttention obj where obj.toUser.id=:uid", 
       map, -1, -1);
     map.clear();
     map.put("uid", user.getId());
     List attentions = this.attentionService.query(
       "select obj from SnsAttention obj where obj.fromUser.id=:uid", 
       map, -1, -1);
     mv.addObject("attentions_num", Integer.valueOf(attentions.size()));
     mv.addObject("fans_num", Integer.valueOf(fans.size()));
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="个人主页", value="/buyer/homepage.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage.htm"})
   public ModelAndView homepage(HttpServletRequest request, HttpServletResponse response, String type, String currentPage, String orderBy, String orderType, String uid, String goodclass_id)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/homepage.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     HomePage home = new HomePage();
     User user = new User();
     if ((uid != null) && (!uid.equals("")))
       user = this.userService.getObjById(CommUtil.null2Long(uid));
     else {
       user = SecurityUserHolder.getCurrentUser();
     }
     mv.addObject("owner", user);
     Map map = new HashMap();
     map.put("uid", user.getId());
     List homePages = this.homePageService.query(
       "select obj from HomePage obj where obj.owner.id=:uid", map, 
       -1, -1);
     if (homePages.size() > 0) {
       home = (HomePage)homePages.get(0);
     } else {
       home.setOwner(SecurityUserHolder.getCurrentUser());
       home.setAddTime(new Date());
       this.homePageService.save(home);
     }
 
     DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.display", new SysMap("display", Boolean.valueOf(true)), "=");
     if ((type != null) && (!type.equals(""))) {
       mv.addObject("type", type);
       if (type.equals("1")) {
         qo.addQuery("obj.user.id", 
           new SysMap("uid", home.getOwner()
           .getId()), "=");
         qo.addQuery("obj.store.id is not null", null);
         Map params = new HashMap();
         params.put("uid", home.getOwner().getId());
         List dynamics = this.dynamicService
           .query(
           "select obj from Dynamic obj where obj.store.id is not null and obj.user.id=:uid", 
           params, -1, -1);
         if (dynamics.size() > 0) {
           mv.addObject("allNum", Integer.valueOf(CommUtil.null2Int(Integer.valueOf(dynamics.size()))));
         }
       }
       if (type.equals("2")) {
         qo.addQuery("obj.user.id", 
           new SysMap("uid", home.getOwner()
           .getId()), "=");
         qo.addQuery("obj.store.id is null", null);
         qo.addQuery("obj.goods.id is null", null);
         qo.addQuery("obj.dissParent.id is null", null);
         Map params = new HashMap();
         params.put("uid", home.getOwner().getId());
         List dynamics = this.dynamicService
           .query(
           "select obj from Dynamic obj where obj.store.id is null and obj.store.id is null and obj.user.id=:uid", 
           params, -1, -1);
         if (dynamics.size() > 0)
           mv.addObject("allNum", Integer.valueOf(CommUtil.null2Int(Integer.valueOf(dynamics.size()))));
       }
     }
     else {
       qo.addQuery("obj.user.id", 
         new SysMap("uid", home.getOwner()
         .getId()), "=");
       qo.addQuery("obj.goods.id is not null", null);
 
       Map params = new HashMap();
       params.put("uid", home.getOwner().getId());
       List dynamics = this.dynamicService
         .query(
         "select obj from Dynamic obj where obj.goods.id is not null and obj.user.id=:uid", 
         params, -1, -1);
       if (dynamics.size() > 0) {
         mv.addObject("allNum", Integer.valueOf(CommUtil.null2Int(Integer.valueOf(dynamics.size()))));
       }
       if ((goodclass_id != null) && (!goodclass_id.equals(""))) {
         mv.addObject("goodclass_id", goodclass_id);
         qo.addQuery("obj.goods.gc.id", 
           new SysMap("goodClass_id", 
           CommUtil.null2Long(goodclass_id)), "=");
       }
 
       params.clear();
       params.put("uid", home.getOwner().getId());
       List hgcs = this.HomeGoodsClassService
         .query(
         "select obj from HomePageGoodsClass obj where obj.user.id=:uid ", 
         params, -1, -1);
 
       mv.addObject("hgcs", hgcs);
     }
 
     if ((uid != null) && (!uid.equals(""))) {
       List custs = home.getCustomers();
 
       if (custs.size() == 0) {
         Visit visit = new Visit();
         visit.setAddTime(new Date());
         visit.setHomepage(home);
         visit.setUser(SecurityUserHolder.getCurrentUser());
         this.visitService.save(visit);
       } else {
         map.clear();
         map.put("home_owner_id", home.getOwner().getId());
         map.put("uid", SecurityUserHolder.getCurrentUser().getId());
         List visits = this.visitService
           .query(
           "select obj from Visit obj where obj.user.id=:uid and obj.homepage.owner.id=:home_owner_id", 
           map, -1, -1);
         if (visits.size() > 0) {
           ((Visit)visits.get(0)).setAddTime(new Date());
           this.visitService.update((Visit)visits.get(0));
         } else {
           Visit visit = new Visit();
           visit.setAddTime(new Date());
           visit.setHomepage(home);
           visit.setUser(SecurityUserHolder.getCurrentUser());
           this.visitService.save(visit);
         }
       }
     }
     map.clear();
     map.put("uid", home.getOwner().getId());
     List visits = this.visitService
       .query(
       "select obj from Visit obj where obj.homepage.owner.id=:uid order by addTime desc", 
       map, -1, 10);
     mv.addObject("visits", visits);
     IPageList pList = this.dynamicService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="个人主页删除动态", value="/buyer/homepage_dynamic_del.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_dynamic_del.htm"})
   public void homepage_dynamic_del(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String orderBy, String orderType, String type)
   {
     boolean flag = false;
     if ((id != null) && (!id.equals(""))) {
       Dynamic dynamic = this.dynamicService
         .getObjById(Long.valueOf(Long.parseLong(id)));
       flag = this.dynamicService.delete(Long.valueOf(Long.parseLong(id)));
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(flag);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="个人主页新鲜事加密", value="/buyer/homepage_dynamic_lock.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_dynamic_lock.htm"})
   public void homepage_dynamic_lock(HttpServletRequest request, HttpServletResponse response, String dynamic_id)
   {
     Dynamic dynamic = this.dynamicService.getObjById(
       CommUtil.null2Long(dynamic_id));
     boolean locked = dynamic.isLocked();
     if (!locked)
       dynamic.setLocked(true);
     else {
       dynamic.setLocked(false);
     }
     this.dynamicService.update(dynamic);
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(dynamic.isLocked());
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="个人主页添加关注", value="/buyer/homepage_add_attention.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_add_attention.htm"})
   public void homepage_add_attention(HttpServletRequest request, HttpServletResponse response, String user_id)
   {
     boolean flag = false;
     Map params = new HashMap();
     params.put("uid", SecurityUserHolder.getCurrentUser().getId());
     params.put("user_id", CommUtil.null2Long(user_id));
     List SnsAttentions = this.attentionService
       .query(
       "select obj from SnsAttention obj where obj.fromUser.id=:uid and obj.toUser.id=:user_id ", 
       params, -1, -1);
     if (SnsAttentions.size() == 0) {
       User atted = this.userService.getObjById(
         CommUtil.null2Long(user_id));
       SnsAttention attention = new SnsAttention();
       attention.setAddTime(new Date());
       attention.setFromUser(SecurityUserHolder.getCurrentUser());
       attention.setToUser(atted);
       flag = this.attentionService.save(attention);
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(flag);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="个人主页添加关注", value="/buyer/homepage_remove_attention.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_remove_attention.htm"})
   public void homepage_remove_attention(HttpServletRequest request, HttpServletResponse response, String id)
   {
     boolean flag = false;
     flag = this.attentionService.delete(CommUtil.null2Long(id));
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(flag);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="好友列表", value="/buyer/homepage/myfriends.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage/myfriends.htm"})
   public ModelAndView homepage_myfriends(HttpServletRequest request, HttpServletResponse response, String uid, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/homepage_myfriends.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     SnsFriendQueryObject qo = new SnsFriendQueryObject(currentPage, mv, 
       orderBy, orderType);
     User user = new User();
     if ((uid != null) && (!uid.equals("")))
       user = this.userService.getObjById(CommUtil.null2Long(uid));
     else {
       user = SecurityUserHolder.getCurrentUser();
     }
     mv.addObject("owner", user);
     qo.addQuery("obj.fromUser.id", new SysMap("fromUser_id", user.getId()), 
       "=");
     IPageList pList = this.snsFriendService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="关注列表", value="/buyer/homepage/myattention.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage/myattention.htm"})
   public ModelAndView homepage_myattention(HttpServletRequest request, HttpServletResponse response, String uid, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/homepage_myattention.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     SnsAttentionQueryObject qo = new SnsAttentionQueryObject(currentPage, 
       mv, orderBy, orderType);
     User user = new User();
     if ((uid != null) && (!uid.equals("")))
       user = this.userService.getObjById(CommUtil.null2Long(uid));
     else {
       user = SecurityUserHolder.getCurrentUser();
     }
     mv.addObject("owner", user);
     qo
       .addQuery("obj.fromUser.id", 
       new SysMap("user_id", user.getId()), "=");
     IPageList pList = this.attentionService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="关注列表", value="/buyer/homepage/myfans.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage/myfans.htm"})
   public ModelAndView homepage_myfans(HttpServletRequest request, HttpServletResponse response, String uid, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/homepage_myfans.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     SnsAttentionQueryObject qo = new SnsAttentionQueryObject(currentPage, 
       mv, orderBy, orderType);
     User user = new User();
     if ((uid != null) && (!uid.equals("")))
       user = this.userService.getObjById(CommUtil.null2Long(uid));
     else {
       user = SecurityUserHolder.getCurrentUser();
     }
     mv.addObject("owner", user);
     qo.addQuery("obj.toUser.id", new SysMap("user_id", user.getId()), "=");
     IPageList pList = this.attentionService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="最近访客", value="/buyer/homepage_visit.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_visit.htm"})
   public ModelAndView homepage_visit(HttpServletRequest request, HttpServletResponse response, String orderBy, String orderType, String currentPage)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/homepage_visit.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String uid = request.getParameter("uid");
     User user = new User();
     if ((uid != null) && (!uid.equals("")))
       user = this.userService.getObjById(CommUtil.null2Long(uid));
     else {
       user = SecurityUserHolder.getCurrentUser();
     }
     mv.addObject("owner", user);
     Map map = new HashMap();
     map.put("uid", user.getId());
     List visits = this.visitService
       .query(
       "select obj from Visit obj where obj.homepage.owner.id=:uid order by addTime desc", 
       map, -1, 10);
     mv.addObject("visits", visits);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="删除访客ajax", value="/buyer/homepage_visit_dele.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_visit_dele.htm"})
   public void homepage_visit_dele(HttpServletRequest request, HttpServletResponse response, String visit_id)
   {
     boolean flag = false;
     Map params = new HashMap();
     params.put("custom_id", CommUtil.null2Long(visit_id));
     params.put("uid", SecurityUserHolder.getCurrentUser().getId());
     List customer = this.visitService
       .query(
       "select obj from Visit obj where obj.user.id=:custom_id and obj.homepage.owner.id=:uid", 
       params, -1, -1);
     if (customer.size() > 0) {
       flag = this.visitService.delete(((Visit)customer.get(0)).getId());
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(flag);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="查询已经购买宝贝和已经收藏宝贝", value="/buyer/homepage_query_goods.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_query_goods.htm"})
   public ModelAndView homepage_query_goods(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/homepage_query_goods.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     int fcount = 0;
     int ocount = 0;
     Map map = new HashMap();
     map.put("uid", SecurityUserHolder.getCurrentUser().getId());
     map.put("type", Integer.valueOf(0));
     List favorites = this.favoriteService
       .query(
       "select obj from Favorite obj where obj.user.id=:uid and obj.type=:type order by addTime desc", 
       map, fcount, 7);
     map.clear();
     map.put("uid", SecurityUserHolder.getCurrentUser().getId());
     map.put("type", Integer.valueOf(0));
     List Allfavorites = this.favoriteService
       .query(
       "select obj from Favorite obj where obj.user.id=:uid and obj.type=:type order by addTime desc", 
       map, -1, -1);
     mv.addObject("favorites", favorites);
     map.clear();
     map.put("uid", SecurityUserHolder.getCurrentUser().getId());
     map.put("order_status", Integer.valueOf(50));
     List orders = this.orderFormService
       .query(
       "select obj from OrderForm obj where obj.user.id=:uid and obj.order_status=:order_status order by finishTime desc", 
       map, ocount, 7);
     map.clear();
     map.put("uid", SecurityUserHolder.getCurrentUser().getId());
     map.put("order_status", Integer.valueOf(50));
     List Allorders = this.orderFormService
       .query(
       "select obj from OrderForm obj where obj.user.id=:uid and obj.order_status=:order_status order by finishTime desc", 
       map, -1, -1);
     mv.addObject("favorite_Allsize", Integer.valueOf(Allfavorites.size()));
     mv.addObject("order_Allsize", Integer.valueOf(Allorders.size()));
     mv.addObject("orders", orders);
     mv.addObject("fcurrentCount", Integer.valueOf(fcount));
     mv.addObject("ocurrentCount", Integer.valueOf(ocount));
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="查询收藏宝贝ajax分页", value="/buyer/homepage_query_goods_favorite_ajax.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_query_goods_favorite_ajax.htm"})
   public ModelAndView homepage_query_goods_favorite_ajax(HttpServletRequest request, HttpServletResponse response, String fcurrentCount)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/homepage_query_goods_favorite_ajax.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     int fcount = 0;
     if ((fcurrentCount != null) && (!fcurrentCount.equals(""))) {
       fcount = CommUtil.null2Int(fcurrentCount);
     }
     Map map = new HashMap();
     map.put("uid", SecurityUserHolder.getCurrentUser().getId());
     map.put("type", Integer.valueOf(0));
     List favorites = this.favoriteService
       .query(
       "select obj from Favorite obj where obj.user.id=:uid and obj.type=:type order by addTime desc", 
       map, fcount, 7);
     mv.addObject("favorites", favorites);
     mv.addObject("fcurrentCount", Integer.valueOf(fcount));
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="查询已经购买宝贝ajax分页", value="/buyer/homepage_query_goods_order_ajax.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_query_goods_order_ajax.htm"})
   public ModelAndView homepage_query_goods_order_ajax(HttpServletRequest request, HttpServletResponse response, String ocurrentCount)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/homepage_query_goods_order_ajax.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     int ocount = 0;
     if ((ocurrentCount != null) && (!ocurrentCount.equals(""))) {
       ocount = CommUtil.null2Int(ocurrentCount);
     }
     Map map = new HashMap();
     map.put("uid", SecurityUserHolder.getCurrentUser().getId());
     map.put("order_status", Integer.valueOf(50));
     List orders = this.orderFormService
       .query(
       "select obj from OrderForm obj where obj.user.id=:uid and obj.order_status=:order_status order by finishTime desc", 
       map, ocount, 7);
     mv.addObject("orders", orders);
     mv.addObject("ocurrentCount", Integer.valueOf(ocount));
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="查询已经收藏店铺", value="/buyer/homepage_query_stores.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_query_stores.htm"})
   public ModelAndView homepage_query_stores(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/homepage_query_stores.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     int currentCount = 0;
     Map map = new HashMap();
     map.put("uid", SecurityUserHolder.getCurrentUser().getId());
     map.put("type", Integer.valueOf(1));
     List favorites = this.favoriteService
       .query(
       "select obj from Favorite obj where obj.user.id=:uid and obj.type=:type order by addTime desc", 
       map, currentCount, 7);
     map.clear();
     map.put("uid", SecurityUserHolder.getCurrentUser().getId());
     map.put("type", Integer.valueOf(1));
     List Allfavorites = this.favoriteService
       .query(
       "select obj from Favorite obj where obj.user.id=:uid and obj.type=:type order by addTime desc", 
       map, -1, -1);
     mv.addObject("favorites", favorites);
     mv.addObject("favorite_Allsize", Integer.valueOf(Allfavorites.size()));
     mv.addObject("currentCount", Integer.valueOf(currentCount));
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="查询已关注店铺ajax分页", value="/buyer/homepage_query_stores_ajax.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_query_stores_ajax.htm"})
   public ModelAndView homepage_query_stores_ajax(HttpServletRequest request, HttpServletResponse response, String currentCount)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/homepage_query_stores_ajax.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     int count = 0;
     if ((currentCount != null) && (!currentCount.equals(""))) {
       count = CommUtil.null2Int(currentCount);
     }
     Map map = new HashMap();
     map.put("uid", SecurityUserHolder.getCurrentUser().getId());
     map.put("type", Integer.valueOf(1));
     List favorites = this.favoriteService
       .query(
       "select obj from Favorite obj where obj.user.id=:uid and obj.type=:type order by addTime desc", 
       map, count, 7);
     mv.addObject("favorites", favorites);
     mv.addObject("currentCount", Integer.valueOf(count));
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="个人主页添加关注", value="/buyer/homepage_goods_url_add.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/buyer/homepage_goods_url_add.htm"})
   public void homepage_goods_url_add(HttpServletRequest request, HttpServletResponse response, String url)
   {
     boolean flag = true;
     Goods goods = null;
     String str = null;
     String address = CommUtil.getURL(request) + "/goods";
     String[] urls = url.split("_");
     if (urls.length == 2) {
       if (!address.equals(urls[0])) {
         flag = false;
       }
       String[] ids = urls[1].split("\\.");
       if (ids.length == 2) {
         if (!ids[1].equals("htm")) {
           flag = false;
         }
         if (flag) {
           goods = this.goodsService.getObjById(
             CommUtil.null2Long(ids[0]));
         }
       }
     }
     if (goods != null) {
       String img_url = CommUtil.getURL(request) + "/" + 
         goods.getGoods_main_photo().getPath() + "/" + 
         goods.getGoods_main_photo().getName() + "_small" + "." + 
         goods.getGoods_main_photo().getExt();
       str = img_url + "," + goods.getId();
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(str);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 }


 
 
 