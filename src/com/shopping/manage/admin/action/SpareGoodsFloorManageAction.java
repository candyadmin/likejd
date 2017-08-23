 package com.shopping.manage.admin.action;
 
 import com.easyjf.beans.BeanUtils;
 import com.easyjf.beans.BeanWrapper;
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.domain.AdvertPosition;
 import com.shopping.foundation.domain.SpareGoods;
 import com.shopping.foundation.domain.SpareGoodsClass;
 import com.shopping.foundation.domain.SpareGoodsFloor;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.query.SpareGoodsClassQueryObject;
 import com.shopping.foundation.domain.query.SpareGoodsFloorQueryObject;
 import com.shopping.foundation.domain.query.SpareGoodsQueryObject;
 import com.shopping.foundation.service.IAccessoryService;
 import com.shopping.foundation.service.IAdvertPositionService;
 import com.shopping.foundation.service.IAdvertService;
 import com.shopping.foundation.service.ISpareGoodsClassService;
 import com.shopping.foundation.service.ISpareGoodsFloorService;
 import com.shopping.foundation.service.ISpareGoodsService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.lang.reflect.Field;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.servlet.ServletContext;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class SpareGoodsFloorManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private ISpareGoodsFloorService sparegoodsfloorService;
 
   @Autowired
   private ISpareGoodsClassService sparegoodsclassService;
 
   @Autowired
   private ISpareGoodsService sparegoodsService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @Autowired
   private IAdvertService advertService;
 
   @Autowired
   private IAdvertPositionService advertPositionService;
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品楼层列表", value="/admin/sparegoodsfloor_list.htm*", rtype="admin", rname="闲置楼层", rcode="sparegoodsfloor_admin", rgroup="闲置")
   @RequestMapping({"/admin/sparegoodsfloor_list.htm"})
   public ModelAndView floor_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/sparegoodsfloor_list.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     SpareGoodsFloorQueryObject qo = new SpareGoodsFloorQueryObject(
       currentPage, mv, orderBy, orderType);
     qo.setOrderBy("sequence");
     qo.setOrderType("asc");
     IPageList pList = this.sparegoodsfloorService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品楼层添加", value="/admin/sparegoodsfloor_add.htm*", rtype="admin", rname="闲置楼层", rcode="sparegoodsfloor_admin", rgroup="闲置")
   @RequestMapping({"/admin/sparegoodsfloor_add.htm"})
   public ModelAndView floor_add(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/sparegoodsfloor_add.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     List sgcs = this.sparegoodsclassService
       .query(
       "select obj from SpareGoodsClass obj where obj.parent.id is null", 
       null, -1, -1);
     Map params = new HashMap();
     params.put("width", Integer.valueOf(200));
     params.put("height", Integer.valueOf(240));
     params.put("ap_type", "img");
     List advertposition = this.advertPositionService
       .query(
       "select obj from AdvertPosition obj where obj.ap_width=:width and obj.ap_height=:height and obj.ap_type=:ap_type order by addTime asc", 
       params, -1, -1);
     mv.addObject("advertposition", advertposition);
     mv.addObject("sgcs", sgcs);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品楼层编辑", value="/admin/sparegoodsfloor_edit.htm*", rtype="admin", rname="闲置楼层", rcode="sparegoodsfloor_admin", rgroup="闲置")
   @RequestMapping({"/admin/sparegoodsfloor_edit.htm"})
   public ModelAndView floor_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/sparegoodsfloor_add.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       SpareGoodsFloor sparegoodsfloor = this.sparegoodsfloorService
         .getObjById(Long.valueOf(Long.parseLong(id)));
       mv.addObject("obj", sparegoodsfloor);
       mv.addObject("currentPage", currentPage);
       mv.addObject("edit", Boolean.valueOf(true));
     }
     List sgcs = this.sparegoodsclassService
       .query(
       "select obj from SpareGoodsClass obj where obj.parent.id is null", 
       null, -1, -1);
     Map params = new HashMap();
     params.put("width", Integer.valueOf(200));
     params.put("height", Integer.valueOf(250));
     params.put("ap_type", "img");
     List advertposition = this.advertPositionService
       .query(
       "select obj from AdvertPosition obj where obj.ap_width=:width and obj.ap_height=:height and obj.ap_type=:ap_type order by addTime asc", 
       params, -1, -1);
     mv.addObject("advertposition", advertposition);
     mv.addObject("sgcs", sgcs);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品楼层保存", value="/admin/sparegoodsfloor_save.htm*", rtype="admin", rname="闲置楼层", rcode="sparegoodsfloor_admin", rgroup="闲置")
   @RequestMapping({"/admin/sparegoodsfloor_save.htm"})
   public ModelAndView floor_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String cmd, String list_url, String add_url, String cid, String advert_url, String ap_id, String advert_type)
   {
     WebForm wf = new WebForm();
     SpareGoodsFloor sparegoodsfloor = null;
     if (id.equals("")) {
       sparegoodsfloor = (SpareGoodsFloor)wf.toPo(request, SpareGoodsFloor.class);
       sparegoodsfloor.setAddTime(new Date());
     } else {
       SpareGoodsFloor obj = this.sparegoodsfloorService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       sparegoodsfloor = (SpareGoodsFloor)wf.toPo(request, obj);
     }
     SpareGoodsClass sgc = this.sparegoodsclassService.getObjById(
       CommUtil.null2Long(cid));
     sparegoodsfloor.setSgc(sgc);
     if ((ap_id != null) && (!ap_id.equals(""))) {
       AdvertPosition adp = this.advertPositionService.getObjById(
         CommUtil.null2Long(ap_id));
       sparegoodsfloor.setAdp(adp);
     }
     if ((advert_url != null) && (!advert_url.equals(""))) {
       sparegoodsfloor.setAdvert_url(advert_url);
     }
     String uploadFilePath = this.configService.getSysConfig()
       .getUploadFilePath();
     String saveFilePathName = request.getSession().getServletContext()
       .getRealPath("/") + 
       uploadFilePath + File.separator + "advert";
     Map map = new HashMap();
     String fileName = "";
     if (sparegoodsfloor.getAdvert_img() != null)
       fileName = sparegoodsfloor.getAdvert_img().getName();
     try
     {
       map = CommUtil.saveFileToServer(request, "advert_img", 
         saveFilePathName, fileName, null);
       Accessory advert_img = null;
       if (fileName.equals("")) {
         if (map.get("fileName") != "") {
           advert_img = new Accessory();
           advert_img.setName(
             CommUtil.null2String(map.get("fileName")));
           advert_img.setExt(CommUtil.null2String(map.get("mime")));
           advert_img
             .setSize(CommUtil.null2Float(map.get("fileSize")));
           advert_img.setPath(uploadFilePath + "/advert");
           advert_img.setWidth(CommUtil.null2Int(map.get("width")));
           advert_img.setHeight(CommUtil.null2Int(map.get("height")));
           advert_img.setAddTime(new Date());
           this.accessoryService.save(advert_img);
           sparegoodsfloor.setAdvert_img(advert_img);
         }
       }
       else if (map.get("fileName") != "") {
         advert_img = sparegoodsfloor.getAdvert_img();
         advert_img.setName(
           CommUtil.null2String(map.get("fileName")));
         advert_img.setExt(CommUtil.null2String(map.get("mime")));
         advert_img
           .setSize(CommUtil.null2Float(map.get("fileSize")));
         advert_img.setPath(uploadFilePath + "/advert");
         advert_img.setWidth(CommUtil.null2Int(map.get("width")));
         advert_img.setHeight(CommUtil.null2Int(map.get("height")));
         advert_img.setAddTime(new Date());
         this.accessoryService.update(advert_img);
       }
     }
     catch (IOException e)
     {
       e.printStackTrace();
     }
 
     if (id.equals(""))
       this.sparegoodsfloorService.save(sparegoodsfloor);
     else
       this.sparegoodsfloorService.update(sparegoodsfloor);
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("list_url", list_url);
     mv.addObject("op_title", "楼层保存成功");
     if (add_url != null) {
       mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
     }
 
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品楼层ajax更新", value="/admin/sparegoodsfloor_ajax.htm*", rtype="admin", rname="闲置楼层", rcode="sparegoodsfloor_admin", rgroup="闲置")
   @RequestMapping({"/admin/sparegoodsfloor_ajax.htm"})
   public void floor_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
     SpareGoodsFloor obj = this.sparegoodsfloorService.getObjById(
       Long.valueOf(Long.parseLong(id)));
     Field[] fields = SpareGoodsFloor.class.getDeclaredFields();
     BeanWrapper wrapper = new BeanWrapper(obj);
     Object val = null;
     for (Field field : fields) {
       if (field.getName().equals(fieldName)) {
         Class clz = Class.forName("java.lang.String");
         if (field.getType().getName().equals("int")) {
           clz = Class.forName("java.lang.Integer");
         }
         if (field.getType().getName().equals("boolean")) {
           clz = Class.forName("java.lang.Boolean");
         }
         if (!value.equals(""))
           val = BeanUtils.convertType(value, clz);
         else {
           val = Boolean.valueOf(
             !CommUtil.null2Boolean(wrapper
             .getPropertyValue(fieldName)));
         }
         wrapper.setPropertyValue(fieldName, val);
       }
     }
     this.sparegoodsfloorService.update(obj);
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(val.toString());
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品楼层删除", value="/admin/sparegoodsfloor_del.htm*", rtype="admin", rname="闲置楼层", rcode="sparegoodsfloor_admin", rgroup="闲置")
   @RequestMapping({"/admin/sparegoodsfloor_del.htm"})
   public String floor_delete(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         SpareGoodsFloor sparegoodsfloor = this.sparegoodsfloorService
           .getObjById(Long.valueOf(Long.parseLong(id)));
         this.sparegoodsfloorService.delete(Long.valueOf(Long.parseLong(id)));
       }
     }
     return "redirect:sparegoodsfloor_list.htm?currentPage=" + currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品楼层分类设置", value="/admin/sparegoodsfloor_setclass.htm*", rtype="admin", rname="闲置楼层", rcode="sparegoodsfloor_admin", rgroup="闲置")
   @RequestMapping({"/admin/sparegoodsfloor_setclass.htm"})
   public ModelAndView floor_setclass(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String fid)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/sparegoodsfloor_setclass.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     SpareGoodsClassQueryObject qo = new SpareGoodsClassQueryObject(
       currentPage, mv, orderBy, orderType);
     SpareGoodsFloor sgf = this.sparegoodsfloorService.getObjById(
       CommUtil.null2Long(fid));
     Set ids = getSpareGoodsClassChildIds(sgf.getSgc());
     Map map = new HashMap();
     map.put("ids", ids);
     qo.addQuery("obj.id in(:ids)", map);
     qo.addQuery("obj.id", new SysMap("obj_id", sgf.getSgc().getId()), "!=");
     qo.setOrderBy("level");
     qo.setOrderType("asc");
     qo.setPageSize(Integer.valueOf(15));
     IPageList pList = this.sparegoodsclassService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
     mv.addObject("fid", fid);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品楼层分类ajax更新", value="/admin/sparegoodsfloor_class_ajax.htm*", rtype="admin", rname="闲置楼层", rcode="sparegoodsfloor_admin", rgroup="闲置")
   @RequestMapping({"/admin/sparegoodsfloor_class_ajax.htm"})
   public void floor_class_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
     SpareGoodsClass obj = this.sparegoodsclassService.getObjById(
       Long.valueOf(Long.parseLong(id)));
     Field[] fields = SpareGoodsClass.class.getDeclaredFields();
     BeanWrapper wrapper = new BeanWrapper(obj);
     Object val = null;
     for (Field field : fields) {
       if (field.getName().equals(fieldName)) {
         Class clz = Class.forName("java.lang.String");
         if (field.getType().getName().equals("int")) {
           clz = Class.forName("java.lang.Integer");
         }
         if (field.getType().getName().equals("boolean")) {
           clz = Class.forName("java.lang.Boolean");
         }
         if (!value.equals(""))
           val = BeanUtils.convertType(value, clz);
         else {
           val = Boolean.valueOf(
             !CommUtil.null2Boolean(wrapper
             .getPropertyValue(fieldName)));
         }
         wrapper.setPropertyValue(fieldName, val);
       }
     }
     this.sparegoodsclassService.update(obj);
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(val.toString());
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品楼层分类设置是否显示", value="/admin/sparegoodsfloor_setclass_switch.htm*", rtype="admin", rname="闲置楼层", rcode="sparegoodsfloor_admin", rgroup="闲置")
   @RequestMapping({"/admin/sparegoodsfloor_setclass_switch.htm"})
   public String floor_setclass_switch(HttpServletRequest request, HttpServletResponse response, String currentPage, String fid, String mulitId, String type) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if ((id != null) && (!id.equals(""))) {
         SpareGoodsClass sgc = this.sparegoodsclassService
           .getObjById(CommUtil.null2Long(id));
         if ((type != null) && (!type.equals(""))) {
           if (type.equals("show"))
             sgc.setViewInFloor(true);
           else {
             sgc.setViewInFloor(false);
           }
         }
         else if (!sgc.isViewInFloor())
           sgc.setViewInFloor(true);
         else {
           sgc.setViewInFloor(false);
         }
 
         this.sparegoodsclassService.update(sgc);
       }
     }
     return "redirect:sparegoodsfloor_setclass.htm?currentPage=" + 
       currentPage + "&fid=" + fid;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品楼层商品设置", value="/admin/sparegoodsfloor_setgoods.htm*", rtype="admin", rname="闲置楼层", rcode="sparegoodsfloor_admin", rgroup="闲置")
   @RequestMapping({"/admin/sparegoodsfloor_setgoods.htm"})
   public ModelAndView floor_setgoods(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String fid)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/sparegoodsfloor_setgoods.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     SpareGoodsFloor sgf = this.sparegoodsfloorService.getObjById(
       CommUtil.null2Long(fid));
     Set ids = getSpareGoodsClassChildIds(sgf.getSgc());
     Map map = new HashMap();
     map.put("ids", ids);
     SpareGoodsQueryObject qo = new SpareGoodsQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.addQuery("obj.status", new SysMap("obj_status", Integer.valueOf(0)), "=");
     qo.addQuery("obj.down", new SysMap("obj_down", Integer.valueOf(0)), "=");
     qo.addQuery("obj.spareGoodsClass.id in (:ids)", map);
     IPageList pList = this.sparegoodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
     mv.addObject("fid", fid);
     map.clear();
     map.put("ids", ids);
     List<SpareGoods> sgs = this.sparegoodsService
       .query(
       "select obj from SpareGoods obj where obj.sgf.id is null and  obj.spareGoodsClass.id in (:ids)", 
       map, -1, -1);
     for (SpareGoods sg : sgs) {
       sg.setSgf(sgf);
       this.sparegoodsService.update(sg);
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="闲置商品楼层商品设置是否显示", value="/admin/sparegoodsfloor_setgoods_switch.htm*", rtype="admin", rname="闲置楼层", rcode="sparegoodsfloor_admin", rgroup="闲置")
   @RequestMapping({"/admin/sparegoodsfloor_setgoods_switch.htm"})
   public String floor_setgoods_switch(HttpServletRequest request, HttpServletResponse response, String currentPage, String mulitId, String fid, String type) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if ((id != null) && (!id.equals(""))) {
         SpareGoods sg = this.sparegoodsService.getObjById(
           CommUtil.null2Long(id));
         if ((type != null) && (!type.equals(""))) {
           if (type.equals("show"))
             sg.setViewInFloor(true);
           else {
             sg.setViewInFloor(false);
           }
         }
         else if (!sg.isViewInFloor())
           sg.setViewInFloor(true);
         else {
           sg.setViewInFloor(false);
         }
 
         this.sparegoodsService.update(sg);
       }
     }
     return "redirect:sparegoodsfloor_setgoods.htm?currentPage=" + 
       currentPage + "&fid=" + fid;
   }
 
   private Set<Long> getSpareGoodsClassChildIds(SpareGoodsClass obj) {
     Set ids = new HashSet();
     ids.add(obj.getId());
     if (obj.getChilds().size() > 0) {
       for (SpareGoodsClass child : obj.getChilds()) {
         Set<Long> cids = getSpareGoodsClassChildIds(child);
         for (Long cid : cids) {
           ids.add(cid);
         }
         ids.add(child.getId());
       }
     }
     return ids;
   }
 }


 
 
 