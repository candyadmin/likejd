 package com.shopping.manage.admin.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.DeliveryGoods;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.Navigation;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.query.DeliveryGoodsQueryObject;
 import com.shopping.foundation.service.IAccessoryService;
 import com.shopping.foundation.service.IDeliveryGoodsService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.INavigationService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
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
 public class DeliveryManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IDeliveryGoodsService deliveryGoodsService;
 
   @Autowired
   private INavigationService navigationService;
 
   @SecurityMapping(display = false, rsequence = 0, title="买就送设置", value="/admin/set_delivery.htm*", rtype="admin", rname="买就送", rcode="delivery_admin", rgroup="运营")
   @RequestMapping({"/admin/set_delivery.htm"})
   public ModelAndView set_delivery(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/set_delivery.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
 
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="买就送设置保存", value="/admin/set_delivery_save.htm*", rtype="admin", rname="买就送", rcode="delivery_admin", rgroup="运营")
   @RequestMapping({"/admin/set_delivery_save.htm"})
   public ModelAndView set_delivery_save(HttpServletRequest request, HttpServletResponse response, String id) {
     SysConfig obj = this.configService.getSysConfig();
     WebForm wf = new WebForm();
     SysConfig sysConfig = null;
     if (id.equals("")) {
       sysConfig = (SysConfig)wf.toPo(request, SysConfig.class);
       sysConfig.setAddTime(new Date());
     } else {
       sysConfig = (SysConfig)wf.toPo(request, obj);
     }
     if (id.equals(""))
       this.configService.save(sysConfig);
     else {
       this.configService.update(sysConfig);
     }
     if (sysConfig.getDelivery_status() == 1) {
       Map params = new HashMap();
       params.put("url", "delivery.htm");
       List navs = this.navigationService.query(
         "select obj from Navigation obj where obj.url=:url", 
         params, -1, -1);
       if (navs.size() == 0) {
         Navigation nav = new Navigation();
         nav.setAddTime(new Date());
         nav.setDisplay(true);
         nav.setLocation(0);
         nav.setNew_win(1);
         nav.setSequence(6);
         nav.setSysNav(true);
         nav.setTitle("买就送");
         nav.setType("diy");
         nav.setUrl("delivery.htm");
         nav.setOriginal_url("delivery.htm");
         this.navigationService.save(nav);
       }
     } else {
       Map params = new HashMap();
       params.put("url", "delivery.htm");
       List<Navigation> navs = this.navigationService.query(
         "select obj from Navigation obj where obj.url=:url", 
         params, -1, -1);
       for (Navigation nav : navs) {
         this.navigationService.delete(nav.getId());
       }
     }
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
 
     mv.addObject("op_title", "买就送设置成功");
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/set_delivery.htm");
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="买就送商品列表", value="/admin/delivery_goods_list.htm*", rtype="admin", rname="买就送", rcode="delivery_admin", rgroup="运营")
   @RequestMapping({"/admin/delivery_goods_list.htm"})
   public ModelAndView delivery_goods_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String goods_name, String d_status) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/delivery_goods_list.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     DeliveryGoodsQueryObject qo = new DeliveryGoodsQueryObject(currentPage, 
       mv, orderBy, orderType);
     if (!CommUtil.null2String(d_status).equals("")) {
       qo.addQuery("obj.d_status", 
         new SysMap("d_status", 
         Integer.valueOf(CommUtil.null2Int(d_status))), "=");
     }
     if (!CommUtil.null2String(goods_name).equals("")) {
       qo.addQuery("obj.d_goods.goods_name", 
         new SysMap("goods_name", "%" + 
         goods_name.trim() + "goods_name"), "=");
     }
     IPageList pList = this.deliveryGoodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("d_status", d_status);
     mv.addObject("goods_name", goods_name);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="买就送商品审核", value="/admin/delivery_goods_audit.htm*", rtype="admin", rname="买就送", rcode="delivery_admin", rgroup="运营")
   @RequestMapping({"/admin/delivery_goods_audit.htm"})
   public String delivery_goods_audit(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!CommUtil.null2String(id).equals("")) {
         DeliveryGoods obj = this.deliveryGoodsService
           .getObjById(CommUtil.null2Long(id));
         obj.setD_admin_user(SecurityUserHolder.getCurrentUser());
         obj.setD_status(1);
         obj.setD_audit_time(new Date());
         this.deliveryGoodsService.update(obj);
         Goods goods = obj.getD_goods();
         goods.setDelivery_status(2);
         this.goodsService.update(goods);
       }
     }
     return "redirect:delivery_goods_list.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="买就送拒绝", value="/admin/delivery_goods_refuse.htm*", rtype="admin", rname="买就送", rcode="delivery_admin", rgroup="运营")
   @RequestMapping({"/admin/delivery_goods_refuse.htm"})
   public String delivery_goods_refuse(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!CommUtil.null2String(id).equals("")) {
         DeliveryGoods obj = this.deliveryGoodsService
           .getObjById(CommUtil.null2Long(id));
         obj.setD_admin_user(SecurityUserHolder.getCurrentUser());
         obj.setD_status(-1);
         obj.setD_refuse_time(new Date());
         this.deliveryGoodsService.update(obj);
       }
     }
     return "redirect:delivery_goods_list.htm?currentPage=" + currentPage;
   }
 }


 
 
 