 package com.shopping.manage.seller.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Activity;
 import com.shopping.foundation.domain.ActivityGoods;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.ActivityGoodsQueryObject;
 import com.shopping.foundation.domain.query.ActivityQueryObject;
 import com.shopping.foundation.service.IActivityGoodsService;
 import com.shopping.foundation.service.IActivityService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import org.nutz.json.Json;
 import org.nutz.json.JsonFormat;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class ActivitySellerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IActivityService activityService;
 
   @Autowired
   private IActivityGoodsService activityGoodsService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @SecurityMapping(display = false, rsequence = 0, title="活动列表", value="/seller/activity.htm*", rtype="seller", rname="活动管理", rcode="activity_seller", rgroup="促销管理")
   @RequestMapping({"/seller/activity.htm"})
   public ModelAndView activity(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/activity.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     ActivityQueryObject qo = new ActivityQueryObject(currentPage, mv, 
       "addTime", "desc");
     qo.addQuery("obj.ac_status", new SysMap("ac_status", Integer.valueOf(1)), "=");
     qo.addQuery("obj.ac_begin_time", 
       new SysMap("ac_begin_time", new Date()), "<=");
     qo.addQuery("obj.ac_end_time", new SysMap("ac_end_time", new Date()), 
       ">=");
     IPageList pList = this.activityService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="申请参加活动", value="/seller/activity_apply.htm*", rtype="seller", rname="活动管理", rcode="activity_seller", rgroup="促销管理")
   @RequestMapping({"/seller/activity_apply.htm"})
   public ModelAndView activity_apply(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/activity_apply.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Activity act = this.activityService.getObjById(CommUtil.null2Long(id));
     mv.addObject("act", act);
     String activity_session = CommUtil.randomString(32);
     mv.addObject("activity_session", activity_session);
     request.getSession(false).setAttribute("activity_session", 
       activity_session);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="活动商品加载", value="/seller/activity_goods.htm*", rtype="seller", rname="活动管理", rcode="activity_seller", rgroup="促销管理")
   @RequestMapping({"/seller/activity_goods.htm"})
   public void activity_goods(HttpServletRequest request, HttpServletResponse response, String goods_name) {
     Map params = new HashMap();
     params.put("goods_name", "%" + goods_name.trim() + "%");
     params.put("goods_status", Integer.valueOf(0));
     params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
     params.put("group_buy", Integer.valueOf(0));
     params.put("activity_status", Integer.valueOf(0));
     params.put("delivery_status", Integer.valueOf(0));
     params.put("combin_status", Integer.valueOf(0));
     List<Goods> goods_list = this.goodsService
       .query(
       "select obj from Goods obj where obj.goods_name like :goods_name and obj.goods_status=:goods_status and obj.goods_store.user.id=:user_id and obj.group_buy =:group_buy and obj.activity_status =:activity_status and obj.delivery_status=:delivery_status and obj.combin_status=:combin_status order by obj.addTime desc", 
       params, -1, -1);
     List maps = new ArrayList();
     for (Goods goods : goods_list) {
       Map map = new HashMap();
       map.put("goods_name", goods.getGoods_name());
       map.put("goods_id", goods.getId());
       maps.add(map);
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(Json.toJson(maps, JsonFormat.compact()));
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="申请参加活动", value="/seller/activity_apply_save.htm*", rtype="seller", rname="活动管理", rcode="activity_seller", rgroup="促销管理")
   @RequestMapping({"/seller/activity_apply_save.htm"})
   public ModelAndView activity_apply_save(HttpServletRequest request, HttpServletResponse response, String goods_ids, String act_id, String activity_session) {
     ModelAndView mv = new JModelAndView("success.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     if ((goods_ids != null) && (!goods_ids.equals(""))) {
       String activity_session1 = CommUtil.null2String(request.getSession(
         false).getAttribute("activity_session"));
       if ((!activity_session1.equals("")) && 
         (activity_session1.equals(activity_session))) {
         request.getSession(false).removeAttribute("activity_session");
         Activity act = this.activityService.getObjById(
           CommUtil.null2Long(act_id));
 
         BigDecimal num = BigDecimal.valueOf(0.1D);
         String[] ids = goods_ids.split(",");
         for (String id : ids) {
           if (!id.equals("")) {
             ActivityGoods ag = new ActivityGoods();
             ag.setAddTime(new Date());
             Goods goods = this.goodsService.getObjById(
               CommUtil.null2Long(id));
             ag.setAg_goods(goods);
 
             goods.setActivity_status(1);
             this.goodsService.update(goods);
             ag.setAg_status(0);
             ag.setAct(act);
 
             ag.setAg_price(num.multiply(act.getAc_rebate())
               .multiply(goods.getStore_price()));
             this.activityGoodsService.save(ag);
           }
         }
         mv.addObject("op_title", "申请参加活动成功");
         mv.addObject("url", CommUtil.getURL(request) + 
           "/seller/activity_goods_list.htm");
       } else {
         mv = new JModelAndView("error.html", this.configService
           .getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "禁止重复提交活动申请");
         mv.addObject("url", CommUtil.getURL(request) + 
           "/seller/activity.htm");
       }
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "至少选择一件商品");
       mv.addObject("url", CommUtil.getURL(request) + 
         "/seller/activity.htm");
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="活动商品列表", value="/seller/activity_goods_list.htm*", rtype="seller", rname="活动管理", rcode="activity_seller", rgroup="促销管理")
   @RequestMapping({"/seller/activity_goods_list.htm"})
   public ModelAndView activity_goods_list(HttpServletRequest request, HttpServletResponse response, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/activity_goods_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     ActivityGoodsQueryObject qo = new ActivityGoodsQueryObject(currentPage, 
       mv, "addTime", "desc");
     qo.addQuery("obj.ag_goods.goods_store.user.id", 
       new SysMap("user_id", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     qo.setPageSize(Integer.valueOf(30));
     IPageList pList = this.activityGoodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
 }


 
 
 