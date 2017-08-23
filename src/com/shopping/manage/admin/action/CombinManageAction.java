 package com.shopping.manage.admin.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.query.GoodsQueryObject;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import java.util.Date;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class CombinManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @SecurityMapping(display = false, rsequence = 0, title="组合销售设置", value="/admin/set_combin.htm*", rtype="admin", rname="组合销售", rcode="combin_admin", rgroup="运营")
   @RequestMapping({"/admin/set_combin.htm"})
   public ModelAndView set_combin(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/set_combin.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="组合销售设置保存", value="/admin/set_combin_save.htm*", rtype="admin", rname="组合销售", rcode="combin_admin", rgroup="运营")
   @RequestMapping({"/admin/set_combin_save.htm"})
   public ModelAndView set_combin_save(HttpServletRequest request, HttpServletResponse response, String id) {
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
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
 
     mv.addObject("op_title", "组合销售设置成功");
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/set_combin.htm");
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="组合销售设置", value="/admin/combin_goods.htm*", rtype="admin", rname="组合销售", rcode="combin_admin", rgroup="运营")
   @RequestMapping({"/admin/combin_goods.htm"})
   public ModelAndView combin_goods(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String goods_name, String combin_status) {
     ModelAndView mv = new JModelAndView("admin/blue/combin_goods.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, 
       orderType);
     qo.addQuery("obj.combin_status", new SysMap("combin_status", Integer.valueOf(0)), ">");
     qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
     if (!CommUtil.null2String(goods_name).equals("")) {
       qo.addQuery("obj.goods_name", 
         new SysMap("goods_name", "%" + 
         goods_name.trim() + "%"), "like");
       mv.addObject("goods_name", goods_name);
     }
     if (!CommUtil.null2String(combin_status).equals("")) {
       qo.addQuery("obj.combin_status", 
         new SysMap("combin_status", 
         Integer.valueOf(CommUtil.null2Int(combin_status))), "=");
       mv.addObject("combin_status", combin_status);
     }
     IPageList pList = this.goodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="组合销售商品审核", value="/admin/combin_goods_audit.htm*", rtype="admin", rname="组合销售", rcode="combin_admin", rgroup="运营")
   @RequestMapping({"/admin/combin_goods_audit.htm"})
   public String combin_goods_audit(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!CommUtil.null2String(id).equals("")) {
         Goods goods = this.goodsService.getObjById(
           CommUtil.null2Long(id));
         goods.setCombin_status(2);
         this.goodsService.update(goods);
       }
     }
     return "redirect:combin_goods.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="组合销售商品拒绝", value="/admin/combin_goods_refuse.htm*", rtype="admin", rname="组合销售", rcode="combin_admin", rgroup="运营")
   @RequestMapping({"/admin/combin_goods_refuse.htm"})
   public String combin_goods_refuse(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!CommUtil.null2String(id).equals("")) {
         Goods goods = this.goodsService.getObjById(
           CommUtil.null2Long(id));
         goods.setCombin_status(-1);
         this.goodsService.update(goods);
       }
     }
     return "redirect:combin_goods.htm?currentPage=" + currentPage;
   }
 }


 
 
 