 package com.shopping.manage.seller.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.domain.Area;
 import com.shopping.foundation.domain.SpareGoods;
 import com.shopping.foundation.domain.SpareGoodsClass;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.SpareGoodsQueryObject;
 import com.shopping.foundation.service.IAccessoryService;
 import com.shopping.foundation.service.IAreaService;
 import com.shopping.foundation.service.ISpareGoodsClassService;
 import com.shopping.foundation.service.ISpareGoodsService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.view.web.tools.StoreViewTools;
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.servlet.ServletContext;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import org.nutz.json.Json;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class SpareGoodsSellerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private ISpareGoodsService sparegoodsService;
 
   @Autowired
   private ISpareGoodsClassService sparegoodsclassService;
 
   @Autowired
   private IAreaService areaService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @Autowired
   private StoreViewTools storeViewTools;
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品列表", value="/seller/spare_goods.htm*", rtype="seller", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/seller/spare_goods.htm"})
   public ModelAndView spare_goods(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String type)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/spare_goods.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     SpareGoodsQueryObject qo = new SpareGoodsQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.user.id", 
       new SysMap("obj_user_id", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     if ((type != null) && (!type.equals(""))) {
       qo.addQuery("obj.down", 
         new SysMap("obj_type", Integer.valueOf(CommUtil.null2Int(type))), "=");
       mv.addObject("type", type);
     }
     qo.setPageSize(Integer.valueOf(15));
     IPageList pList = this.sparegoodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="发布闲置商品", value="/seller/add_spare_goods.htm*", rtype="seller", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/seller/add_spare_goods.htm"})
   public ModelAndView spare_goods_add(HttpServletRequest request, HttpServletResponse response, String id)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/add_spare_goods.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Map map = new HashMap();
     map.put("level", Integer.valueOf(2));
     List level2 = this.sparegoodsclassService
       .query("select obj from SpareGoodsClass obj where obj.level=:level order by sequence asc", 
       map, -1, -1);
     List areas = this.areaService.query(
       "select obj from Area obj where obj.parent.id is null", null, 
       -1, -1);
     String spare_goods_session = CommUtil.randomString(32);
     request.getSession(false).setAttribute("spare_goods_session", 
       spare_goods_session);
     mv.addObject("spare_goods_session", spare_goods_session);
     mv.addObject("imageSuffix", this.storeViewTools
       .genericImageSuffix(this.configService.getSysConfig()
       .getImageSuffix()));
     mv.addObject("areas", areas);
     mv.addObject("level2", level2);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品信息编辑", value="/seller/spare_goods_edit.htm*", rtype="seller", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/seller/spare_goods_edit.htm"})
   public ModelAndView spare_goods_edit(HttpServletRequest request, HttpServletResponse response, String id)
   {
     ModelAndView mv = null;
     SpareGoods obj = this.sparegoodsService.getObjById(
       CommUtil.null2Long(id));
 
     if (obj.getUser().getId()
       .equals(SecurityUserHolder.getCurrentUser().getId())) {
       mv = new JModelAndView(
         "user/default/usercenter/add_spare_goods.html", 
         this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 0, request, 
         response);
 
       List sgcs = this.sparegoodsclassService
         .query("select obj from SpareGoodsClass obj where obj.parent.id is null order by sequence asc", 
         null, -1, -1);
       Map map = new HashMap();
       map.put("level", Integer.valueOf(2));
       List level2 = this.sparegoodsclassService
         .query("select obj from SpareGoodsClass obj where obj.level=:level order by sequence asc", 
         map, -1, -1);
       List areas = this.areaService.query(
         "select obj from Area obj where obj.parent.id is null", 
         null, -1, -1);
       mv.addObject("obj", obj);
       mv.addObject("areas", areas);
       mv.addObject("level2", level2);
       mv.addObject("sgcs", sgcs);
       mv.addObject("imageSuffix", this.storeViewTools
         .genericImageSuffix(this.configService.getSysConfig()
         .getImageSuffix()));
       String spare_goods_session = CommUtil.randomString(32);
       request.getSession(false).setAttribute("spare_goods_session", 
         spare_goods_session);
       mv.addObject("spare_goods_session", spare_goods_session);
       mv.addObject("imageSuffix", this.storeViewTools
         .genericImageSuffix(this.configService.getSysConfig()
         .getImageSuffix()));
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("url", CommUtil.getURL(request) + 
         "/seller/spare_goods.htm");
       mv.addObject("op_title", "您所访问的地址不存在!");
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品信息删除", value="/seller/spare_goods_dele.htm*", rtype="seller", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/seller/spare_goods_dele.htm"})
   public ModelAndView spare_goods_dele(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/success.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     SpareGoods obj = this.sparegoodsService.getObjById(
       CommUtil.null2Long(id));
     if ((obj != null) && (!obj.equals("")))
     {
       if (obj.getUser().getId()
         .equals(SecurityUserHolder.getCurrentUser().getId())) {
         this.sparegoodsService.delete(CommUtil.null2Long(id));
         mv.addObject("op_title", "删除闲置商品成功!");
       } else {
         mv = new JModelAndView("error.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 1, request, 
           response);
         mv.addObject("op_title", "您所访问的地址不存在!");
       }
       mv.addObject("url", CommUtil.getURL(request) + 
         "/seller/spare_goods.htm?currentPage=" + currentPage);
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("op_title", "您所访问的地址不存在!");
       mv.addObject("url", CommUtil.getURL(request) + 
         "/seller/spare_goods.htm?currentPage=" + currentPage);
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品下架上架操作", value="/seller/spare_goods_updown.htm*", rtype="seller", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/seller/spare_goods_updown.htm"})
   public ModelAndView spare_goods_updown(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String type)
   {
     ModelAndView mv = new JModelAndView("error", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     mv.addObject("op_title", "您所访问的地址不存在!");
     mv.addObject("url", CommUtil.getURL(request) + 
       "/seller/spare_goods.htm?currentPage=" + currentPage);
     SpareGoods obj = this.sparegoodsService.getObjById(
       CommUtil.null2Long(id));
     if ((obj != null) && (!obj.equals("")))
     {
       if (obj.getUser().getId()
         .equals(SecurityUserHolder.getCurrentUser().getId())) {
         obj.setDown(CommUtil.null2Int(type));
         this.sparegoodsService.update(obj);
         mv = new JModelAndView("user/default/usercenter/success.html", 
           this.configService.getSysConfig(), 
           this.userConfigService.getUserConfig(), 0, request, 
           response);
         mv.addObject("op_title", "操作成功!");
         mv.addObject("url", CommUtil.getURL(request) + 
           "/seller/spare_goods.htm?currentPage=" + currentPage + 
           "&&type=" + type);
       }
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品保存", value="/seller/spare_goods_save.htm*", rtype="seller", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/seller/spare_goods_save.htm"})
   public ModelAndView spare_goods_save(HttpServletRequest request, HttpServletResponse response, String id, String class_id, String oldAndnew, String area_id, String img1_id, String img2_id, String img3_id, String img4_id, String img5_id, String main_img_id, String spare_goods_session)
   {
     ModelAndView mv = null;
     String spare_goods_session1 = CommUtil.null2String(request.getSession(
       false).getAttribute("spare_goods_session"));
     if (spare_goods_session.equals(spare_goods_session1))
     {
       WebForm wf = new WebForm();
       SpareGoods sparegoods = null;
       SpareGoodsClass goodsClass = this.sparegoodsclassService
         .getObjById(CommUtil.null2Long(class_id));
       if (id.equals("")) {
         sparegoods = (SpareGoods)wf.toPo(request, SpareGoods.class);
         sparegoods.setAddTime(new Date());
         sparegoods.setUser(SecurityUserHolder.getCurrentUser());
       } else {
         SpareGoods obj = this.sparegoodsService.getObjById(
           Long.valueOf(Long.parseLong(id)));
         sparegoods = (SpareGoods)wf.toPo(request, obj);
       }
       Area area = this.areaService
         .getObjById(CommUtil.null2Long(area_id));
       sparegoods.setArea(area);
       if ((img1_id != null) && (!img1_id.equals(""))) {
         Accessory img1 = this.accessoryService.getObjById(
           CommUtil.null2Long(img1_id));
         sparegoods.setImg1(img1);
       }
       if ((img2_id != null) && (!img2_id.equals(""))) {
         Accessory img2 = this.accessoryService.getObjById(
           CommUtil.null2Long(img2_id));
         sparegoods.setImg2(img2);
       }
       if ((img3_id != null) && (!img3_id.equals(""))) {
         Accessory img3 = this.accessoryService.getObjById(
           CommUtil.null2Long(img3_id));
         sparegoods.setImg3(img3);
       }
       if ((img4_id != null) && (!img4_id.equals(""))) {
         Accessory img4 = this.accessoryService.getObjById(
           CommUtil.null2Long(img4_id));
         sparegoods.setImg4(img4);
       }
       if ((img5_id != null) && (!img5_id.equals(""))) {
         Accessory img5 = this.accessoryService.getObjById(
           CommUtil.null2Long(img5_id));
         sparegoods.setImg5(img5);
       }
       if ((main_img_id != null) && (!main_img_id.equals(""))) {
         Accessory main_img = this.accessoryService.getObjById(
           CommUtil.null2Long(main_img_id));
         sparegoods.setMain_img(main_img);
       } else {
         Accessory img1 = this.accessoryService.getObjById(
           CommUtil.null2Long(img1_id));
         sparegoods.setMain_img(img1);
       }
       sparegoods.setSpareGoodsClass(goodsClass);
       sparegoods.setOldAndnew(CommUtil.null2Int(oldAndnew));
       if (id.equals(""))
         this.sparegoodsService.save(sparegoods);
       else
         this.sparegoodsService.update(sparegoods);
       mv = new JModelAndView("user/default/usercenter/success.html", 
         this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 0, request, 
         response);
       mv.addObject("url", CommUtil.getURL(request) + 
         "/seller/spare_goods.htm?type=0");
       mv.addObject("op_title", "闲置商品发布成功!");
     } else {
       mv = new JModelAndView("error.html", this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 1, request, 
         response);
       mv.addObject("url", CommUtil.getURL(request) + 
         "/seller/spare_goods.htm");
       mv.addObject("op_title", "禁止重复发布商品!");
     }
     request.getSession(false).removeAttribute("spare_goods_session");
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品发布页Ajax加载下级地区信息", value="/seller/sparegoods_area_data.htm*", rtype="seller", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/seller/sparegoods_area_data.htm"})
   public ModelAndView sparegoods_area_data(HttpServletRequest request, HttpServletResponse response, String parent_id, String area_mark)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/sparegoods_area_data.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Map map = new HashMap();
     map.put("parent_id", CommUtil.null2Long(parent_id));
     List childs = this.areaService.query(
       "select obj from Area obj where obj.parent.id=:parent_id", map, 
       -1, -1);
     if (childs.size() > 0) {
       mv.addObject("childs", childs);
     }
     if (area_mark.equals("privence")) {
       mv.addObject("area_mark", "city");
     }
 
     if (area_mark.equals("city")) {
       mv.addObject("area_mark", "last");
     }
     return mv;
   }
 
   @RequestMapping({"/seller/sparegoods_swf_upload.htm"})
   public void sparegoods_swf_upload(HttpServletRequest request, HttpServletResponse response, String special_id)
   {
     String uploadFilePath = this.configService.getSysConfig()
       .getUploadFilePath();
     String saveFilePathName = request.getSession().getServletContext()
       .getRealPath("/") + 
       uploadFilePath + File.separator + "spare_goods";
     Map json_map = new HashMap();
     Map map = new HashMap();
     try {
       String fileName = "";
       map = CommUtil.saveFileToServer(request, "imgFile", 
         saveFilePathName, fileName, null);
       Accessory accessory = new Accessory();
       accessory.setName(CommUtil.null2String(map.get("fileName")));
       accessory.setExt(CommUtil.null2String(map.get("mime")));
       accessory.setSize(CommUtil.null2Float(map.get("fileSize")));
       accessory.setPath(uploadFilePath + "/spare_goods");
       accessory.setWidth(CommUtil.null2Int(map.get("width")));
       accessory.setHeight(CommUtil.null2Int(map.get("height")));
       accessory.setAddTime(new Date());
       this.accessoryService.save(accessory);
       json_map.put("url", accessory.getPath() + "/" + accessory.getName());
       json_map.put("id", accessory.getId());
     }
     catch (IOException e) {
       e.printStackTrace();
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(Json.toJson(json_map));
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="图片删除", value="/seller/sparegoods_removeimg.htm*", rtype="seller", rname="用户中心", rcode="user_center", rgroup="用户中心")
   @RequestMapping({"/seller/sparegoods_removeimg.htm"})
   public void sparegoods_img_dele(HttpServletRequest request, HttpServletResponse response, String count, String sp_id, String img_id)
   {
     SpareGoods sp = null;
     Accessory img = this.accessoryService.getObjById(
       CommUtil.null2Long(img_id));
     if ((sp_id != null) && (!sp_id.equals(""))) {
       sp = this.sparegoodsService.getObjById(CommUtil.null2Long(sp_id));
       if ((count.equals("1")) && 
         (sp.getImg1() != null) && (!sp.getImg1().equals(""))) {
         sp.setImg1(null);
       }
 
       if ((count.equals("2")) && 
         (sp.getImg2() != null) && (!sp.getImg2().equals(""))) {
         sp.setImg2(null);
       }
 
       if ((count.equals("3")) && 
         (sp.getImg3() != null) && (!sp.getImg3().equals(""))) {
         sp.setImg3(null);
       }
 
       if ((count.equals("4")) && 
         (sp.getImg4() != null) && (!sp.getImg4().equals(""))) {
         sp.setImg4(null);
       }
 
       if ((count.equals("5")) && 
         (sp.getImg5() != null) && (!sp.getImg5().equals(""))) {
         sp.setImg5(null);
       }
 
       this.sparegoodsService.update(sp);
     }
     boolean flag = false;
     flag = this.accessoryService.delete(img.getId());
     if (flag) {
       CommUtil.del_acc(request, img);
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
 }


 
 
 