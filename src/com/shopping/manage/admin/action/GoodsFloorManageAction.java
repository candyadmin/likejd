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
 import com.shopping.foundation.domain.GoodsClass;
 import com.shopping.foundation.domain.GoodsFloor;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.query.GoodsBrandQueryObject;
 import com.shopping.foundation.domain.query.GoodsFloorQueryObject;
 import com.shopping.foundation.domain.query.GoodsQueryObject;
 import com.shopping.foundation.service.IAccessoryService;
 import com.shopping.foundation.service.IAdvertPositionService;
 import com.shopping.foundation.service.IGoodsBrandService;
 import com.shopping.foundation.service.IGoodsClassService;
 import com.shopping.foundation.service.IGoodsFloorService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.manage.admin.tools.GoodsFloorTools;
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.lang.reflect.Field;
 import java.util.ArrayList;
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
 import org.nutz.json.Json;
 import org.nutz.json.JsonFormat;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class GoodsFloorManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGoodsFloorService goodsfloorService;
 
   @Autowired
   private IGoodsClassService goodsClassService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IGoodsBrandService goodsBrandService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @Autowired
   private IAdvertPositionService advertPositionService;
 
   @Autowired
   private GoodsFloorTools gf_tools;
 
   @SecurityMapping(display = false, rsequence = 0, title="楼层分类列表", value="/admin/goods_floor_list.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_list.htm"})
   public ModelAndView goods_floor_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView("admin/blue/goods_floor_list.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     GoodsFloorQueryObject qo = new GoodsFloorQueryObject(currentPage, mv, 
       "gf_sequence", "asc");
     qo.addQuery("obj.gf_level", new SysMap("gf_level", Integer.valueOf(0)), "=");
     IPageList pList = this.goodsfloorService.list(qo);
     CommUtil.saveIPageList2ModelAndView(
       url + "/admin/goods_floor_list.htm", "", params, pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="楼层分类添加", value="/admin/goods_floor_add.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_add.htm"})
   public ModelAndView goods_floor_add(HttpServletRequest request, HttpServletResponse response, String currentPage, String pid)
   {
     ModelAndView mv = new JModelAndView("admin/blue/goods_floor_add.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     mv.addObject("currentPage", currentPage);
     Map params = new HashMap();
     params.put("gf_level", Integer.valueOf(0));
     List gfs = this.goodsfloorService.query(
       "select obj from GoodsFloor obj where obj.gf_level=:gf_level", 
       params, -1, -1);
     mv.addObject("gfs", gfs);
     GoodsFloor obj = new GoodsFloor();
     GoodsFloor parent = this.goodsfloorService.getObjById(
       CommUtil.null2Long(pid));
     obj.setParent(parent);
     if (parent != null)
       obj.setGf_level(parent.getGf_level() + 1);
     obj.setGf_display(true);
     mv.addObject("obj", obj);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="楼层分类编辑", value="/admin/goods_floor_edit.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_edit.htm"})
   public ModelAndView goods_floor_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/goods_floor_add.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       GoodsFloor goodsfloor = this.goodsfloorService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       Map params = new HashMap();
       params.put("gf_level", Integer.valueOf(0));
       List gfs = this.goodsfloorService
         .query("select obj from GoodsFloor obj where obj.gf_level=:gf_level", 
         params, -1, -1);
       mv.addObject("gfs", gfs);
       mv.addObject("obj", goodsfloor);
       mv.addObject("currentPage", currentPage);
       mv.addObject("edit", Boolean.valueOf(true));
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="楼层分类保存", value="/admin/goods_floor_save.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_save.htm"})
   public ModelAndView goods_floor_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String pid, String list_url, String add_url)
   {
     WebForm wf = new WebForm();
     GoodsFloor goodsfloor = null;
     if (id.equals("")) {
       goodsfloor = (GoodsFloor)wf.toPo(request, GoodsFloor.class);
       goodsfloor.setAddTime(new Date());
     } else {
       GoodsFloor obj = this.goodsfloorService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       goodsfloor = (GoodsFloor)wf.toPo(request, obj);
     }
     GoodsFloor parent = this.goodsfloorService.getObjById(
       CommUtil.null2Long(pid));
     if (parent != null) {
       goodsfloor.setParent(parent);
       goodsfloor.setGf_level(parent.getGf_level() + 1);
     }
     if (id.equals(""))
       this.goodsfloorService.save(goodsfloor);
     else
       this.goodsfloorService.update(goodsfloor);
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     mv.addObject("list_url", list_url);
     mv.addObject("op_title", "保存首页楼层成功");
     if (add_url != null) {
       mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="楼层分类删除", value="/admin/goods_floor_del.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_del.htm"})
   public String goods_floor_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         GoodsFloor goodsfloor = this.goodsfloorService.getObjById(
           Long.valueOf(Long.parseLong(id)));
         this.goodsfloorService.delete(Long.valueOf(Long.parseLong(id)));
       }
     }
     return "redirect:goods_floor_list.htm?currentPage=" + currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="楼层分类Ajax更新", value="/admin/goods_floor_ajax.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_ajax.htm"})
   public void goods_floor_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
     GoodsFloor obj = this.goodsfloorService.getObjById(Long.valueOf(Long.parseLong(id)));
     Field[] fields = GoodsFloor.class.getDeclaredFields();
     BeanWrapper wrapper = new BeanWrapper(obj);
     Object val = null;
     for (Field field : fields)
     {
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
     this.goodsfloorService.update(obj);
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
 
   @SecurityMapping(display = false, rsequence = 0, title="楼层分类下级加载", value="/admin/goods_floor_data.htm*", rtype="admin", rname="分类管理", rcode="goods_class", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_data.htm"})
   public ModelAndView goods_floor_data(HttpServletRequest request, HttpServletResponse response, String pid, String currentPage) {
     ModelAndView mv = new JModelAndView("admin/blue/goods_floor_data.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Map map = new HashMap();
     map.put("pid", Long.valueOf(Long.parseLong(pid)));
     List gfs = this.goodsfloorService.query(
       "select obj from GoodsFloor obj where obj.parent.id =:pid", 
       map, -1, -1);
     mv.addObject("gfs", gfs);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板编辑", value="/admin/goods_floor_template.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_template.htm"})
   public ModelAndView goods_floor_template(HttpServletRequest request, HttpServletResponse response, String currentPage, String id, String tab) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/goods_floor_template.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     mv.addObject("gf_tools", this.gf_tools);
     mv.addObject("currentPage", currentPage);
     mv.addObject("tab", tab);
     mv.addObject("url", CommUtil.getURL(request));
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板商品分类编辑", value="/admin/goods_floor_class.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_class.htm"})
   public ModelAndView goods_floor_class(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/goods_floor_class.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     List gcs = this.goodsClassService
       .query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc", 
       null, -1, -1);
     mv.addObject("gcs", gcs);
     mv.addObject("obj", obj);
     mv.addObject("gf_tools", this.gf_tools);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板商品分类加载", value="/admin/goods_floor_class_load.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_class_load.htm"})
   public ModelAndView goods_floor_class_load(HttpServletRequest request, HttpServletResponse response, String currentPage, String gc_id) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/goods_floor_class_load.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GoodsClass gc = this.goodsClassService.getObjById(
       CommUtil.null2Long(gc_id));
     mv.addObject("gc", gc);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板商品分类保存", value="/admin/goods_floor_class_save.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_class_save.htm"})
   public String goods_floor_class_save(HttpServletRequest request, HttpServletResponse response, String id, String ids, String gf_name) {
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     obj.setGf_name(gf_name);
     List gf_gc_list = new ArrayList();
     String[] id_list = ids.split(",pid:");
     for (String t_id : id_list) {
       String[] c_id_list = t_id.split(",");
       Map map = new HashMap();
       for (int i = 0; i < c_id_list.length; i++) {
         String c_id = c_id_list[i];
         if (c_id.indexOf("cid") < 0)
           map.put("pid", c_id);
         else {
           map.put("gc_id" + i, c_id.substring(4));
         }
       }
       map.put("gc_count", Integer.valueOf(c_id_list.length - 1));
       if (!map.get("pid").toString().equals("")) {
         gf_gc_list.add(map);
       }
     }
     obj.setGf_gc_list(Json.toJson(gf_gc_list, JsonFormat.compact()));
     this.goodsfloorService.update(obj);
     return "redirect:goods_floor_template.htm?id=" + id;
   }
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板分类商品编辑", value="/admin/goods_floor_gc_goods.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_gc_goods.htm"})
   public ModelAndView goods_floor_gc_goods(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/goods_floor_gc_goods.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     List gcs = this.goodsClassService
       .query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc", 
       null, -1, -1);
     mv.addObject("gcs", gcs);
     mv.addObject("obj", obj);
     mv.addObject("gf_tools", this.gf_tools);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板分类商品保存", value="/admin/goods_floor_gc_goods_save.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_gc_goods_save.htm"})
   public String goods_floor_gc_goods_save(HttpServletRequest request, HttpServletResponse response, String gf_name, String id, String ids) {
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     obj.setGf_name(gf_name);
     String[] id_list = ids.split(",");
     Map map = new HashMap();
     for (int i = 0; i < id_list.length; i++) {
       if (!id_list[i].equals("")) {
         map.put("goods_id" + i, id_list[i]);
       }
     }
 
     obj.setGf_gc_goods(Json.toJson(map, JsonFormat.compact()));
     this.goodsfloorService.update(obj);
     return "redirect:goods_floor_template.htm?id=" + 
       obj.getParent().getId() + "&tab=" + id;
   }
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板右侧商品列表编辑", value="/admin/goods_floor_list_goods.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_list_goods.htm"})
   public ModelAndView goods_floor_list_goods(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/goods_floor_list_goods.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     List gcs = this.goodsClassService
       .query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc", 
       null, -1, -1);
     mv.addObject("gcs", gcs);
     mv.addObject("obj", obj);
     mv.addObject("gf_tools", this.gf_tools);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板右侧商品列表保存", value="/admin/goods_floor_list_goods_save.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_list_goods_save.htm"})
   public String goods_floor_list_goods_save(HttpServletRequest request, HttpServletResponse response, String list_title, String id, String ids) {
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     String[] id_list = ids.split(",");
     Map map = new HashMap();
     map.put("list_title", list_title);
     for (int i = 0; i < id_list.length; i++) {
       if (!id_list[i].equals("")) {
         map.put("goods_id" + i, id_list[i]);
       }
     }
 
     obj.setGf_list_goods(Json.toJson(map, JsonFormat.compact()));
     this.goodsfloorService.update(obj);
     return "redirect:goods_floor_template.htm?id=" + obj.getId();
   }
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板左下方广告编辑", value="/admin/goods_floor_left_adv.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_left_adv.htm"})
   public ModelAndView goods_floor_left_adv(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/goods_floor_left_adv.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     Map params = new HashMap();
     params.put("ap_status", Integer.valueOf(1));
     params.put("ap_width", Integer.valueOf(156));
     params.put("ap_type", "img");
     List aps = this.advertPositionService
       .query("select obj from AdvertPosition obj where obj.ap_status=:ap_status and obj.ap_width=:ap_width and obj.ap_type=:ap_type order by obj.addTime desc", 
       params, -1, -1);
     mv.addObject("aps", aps);
     mv.addObject("obj", obj);
     mv.addObject("gf_tools", this.gf_tools);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板左下方广告保存", value="/admin/goods_floor_left_adv_save.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_left_adv_save.htm"})
   public String goods_floor_left_adv_save(HttpServletRequest request, HttpServletResponse response, String type, String id, String adv_url, String adv_id) {
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     Map map = new HashMap();
     if (type.equals("user"))
     {
       String uploadFilePath = this.configService.getSysConfig()
         .getUploadFilePath();
       String saveFilePathName = request.getSession().getServletContext()
         .getRealPath("/") + 
         uploadFilePath + File.separator + "advert";
       Map json_map = new HashMap();
       try {
         map = CommUtil.saveFileToServer(request, "img", 
           saveFilePathName, "", null);
         if (map.get("fileName") != "") {
           Accessory acc = new Accessory();
           acc.setName(CommUtil.null2String(map.get("fileName")));
           acc.setExt(CommUtil.null2String(map.get("mime")));
           acc.setSize(CommUtil.null2Float(map.get("fileSize")));
           acc.setPath(uploadFilePath + "/advert");
           acc.setWidth(CommUtil.null2Int(map.get("width")));
           acc.setHeight(CommUtil.null2Int(map.get("height")));
           acc.setAddTime(new Date());
           this.accessoryService.save(acc);
           json_map.put("acc_id", acc.getId());
         }
       }
       catch (IOException e)
       {
         e.printStackTrace();
       }
       json_map.put("acc_url", adv_url);
       json_map.put("adv_id", "");
       System.out.println(Json.toJson(json_map, JsonFormat.compact()));
       obj.setGf_left_adv(Json.toJson(json_map, JsonFormat.compact()));
     }
     if (type.equals("adv")) {
       Map json_map = new HashMap();
       json_map.put("acc_id", "");
       json_map.put("acc_url", "");
       json_map.put("adv_id", adv_id);
       System.out.println(Json.toJson(json_map, JsonFormat.compact()));
       obj.setGf_left_adv(Json.toJson(json_map, JsonFormat.compact()));
     }
     this.goodsfloorService.update(obj);
     return "redirect:goods_floor_template.htm?id=" + obj.getId();
   }
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板右下方广告编辑", value="/admin/goods_floor_right_adv.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_right_adv.htm"})
   public ModelAndView goods_floor_right_adv(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/goods_floor_right_adv.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     Map params = new HashMap();
     params.put("ap_status", Integer.valueOf(1));
     params.put("ap_width", Integer.valueOf(205));
     params.put("ap_type", "img");
     List aps = this.advertPositionService
       .query("select obj from AdvertPosition obj where obj.ap_status=:ap_status and obj.ap_width=:ap_width and obj.ap_type=:ap_type order by obj.addTime desc", 
       params, -1, -1);
     mv.addObject("aps", aps);
     mv.addObject("obj", obj);
     mv.addObject("gf_tools", this.gf_tools);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板右下方广告保存", value="/admin/goods_floor_right_adv_save.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_right_adv_save.htm"})
   public String goods_floor_right_adv_save(HttpServletRequest request, HttpServletResponse response, String type, String id, String adv_url, String adv_id) {
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     Map map = new HashMap();
     if (type.equals("user"))
     {
       String uploadFilePath = this.configService.getSysConfig()
         .getUploadFilePath();
       String saveFilePathName = request.getSession().getServletContext()
         .getRealPath("/") + 
         uploadFilePath + File.separator + "advert";
       Map json_map = new HashMap();
       try {
         map = CommUtil.saveFileToServer(request, "img", 
           saveFilePathName, "", null);
         if (map.get("fileName") != "") {
           Accessory acc = new Accessory();
           acc.setName(CommUtil.null2String(map.get("fileName")));
           acc.setExt(CommUtil.null2String(map.get("mime")));
           acc.setSize(CommUtil.null2Float(map.get("fileSize")));
           acc.setPath(uploadFilePath + "/advert");
           acc.setWidth(CommUtil.null2Int(map.get("width")));
           acc.setHeight(CommUtil.null2Int(map.get("height")));
           acc.setAddTime(new Date());
           this.accessoryService.save(acc);
           json_map.put("acc_id", acc.getId());
         }
       }
       catch (IOException e)
       {
         e.printStackTrace();
       }
       json_map.put("acc_url", adv_url);
       json_map.put("adv_id", "");
       System.out.println(Json.toJson(json_map, JsonFormat.compact()));
       obj.setGf_right_adv(Json.toJson(json_map, JsonFormat.compact()));
     }
     if (type.equals("adv")) {
       Map json_map = new HashMap();
       json_map.put("acc_id", "");
       json_map.put("acc_url", "");
       json_map.put("adv_id", adv_id);
       System.out.println(Json.toJson(json_map, JsonFormat.compact()));
       obj.setGf_right_adv(Json.toJson(json_map, JsonFormat.compact()));
     }
     this.goodsfloorService.update(obj);
     return "redirect:goods_floor_template.htm?id=" + obj.getId();
   }
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板品牌编辑", value="/admin/goods_floor_brand.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_brand.htm"})
   public ModelAndView goods_floor_brand(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/goods_floor_brand.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     GoodsBrandQueryObject qo = new GoodsBrandQueryObject("1", mv, 
       "sequence", "asc");
     qo.addQuery("obj.audit", new SysMap("audit", Integer.valueOf(1)), "=");
     IPageList pList = this.goodsBrandService.list(qo);
     CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
       "/admin/goods_floor_brand_load.htm", "", "", pList, mv);
     mv.addObject("obj", obj);
     mv.addObject("gf_tools", this.gf_tools);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板品牌保存", value="/admin/goods_floor_brand_save.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_brand_save.htm"})
   public String goods_floor_brand_save(HttpServletRequest request, HttpServletResponse response, String id, String ids) {
     GoodsFloor obj = this.goodsfloorService.getObjById(
       CommUtil.null2Long(id));
     String[] id_list = ids.split(",");
     Map map = new HashMap();
     for (int i = 0; i < id_list.length; i++) {
       if (!id_list[i].equals("")) {
         map.put("brand_id" + i, id_list[i]);
       }
     }
     System.out.println(Json.toJson(map, JsonFormat.compact()));
     obj.setGf_brand_list(Json.toJson(map, JsonFormat.compact()));
     this.goodsfloorService.update(obj);
     return "redirect:goods_floor_template.htm?id=" + obj.getId();
   }
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板品牌加载", value="/admin/goods_floor_brand_load.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_brand_load.htm"})
   public ModelAndView goods_floor_brand_load(HttpServletRequest request, HttpServletResponse response, String currentPage, String name) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/goods_floor_brand_load.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GoodsBrandQueryObject qo = new GoodsBrandQueryObject(currentPage, mv, 
       "sequence", "asc");
     qo.addQuery("obj.audit", new SysMap("audit", Integer.valueOf(1)), "=");
     if (!CommUtil.null2String(name).equals("")) {
       qo.addQuery("obj.name", 
         new SysMap("name", "%" + name.trim() + "%"), "like");
     }
     IPageList pList = this.goodsBrandService.list(qo);
     CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
       "/admin/goods_floor_brand_load.htm", "", 
       "&name=" + CommUtil.null2String(name), pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="楼层模板分类商品编辑", value="/admin/goods_floor_list_goods_load.htm*", rtype="admin", rname="首页楼层", rcode="goods_floor", rgroup="商品")
   @RequestMapping({"/admin/goods_floor_list_goods_load.htm"})
   public ModelAndView goods_floor_list_goods_load(HttpServletRequest request, HttpServletResponse response, String currentPage, String gc_id, String goods_name) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/goods_floor_list_goods_load.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, "addTime", 
       "desc");
     if (!CommUtil.null2String(gc_id).equals("")) {
       Set ids = genericIds(this.goodsClassService
         .getObjById(CommUtil.null2Long(gc_id)));
       Map paras = new HashMap();
       paras.put("ids", ids);
       qo.addQuery("obj.gc.id in (:ids)", paras);
     }
     if (!CommUtil.null2String(goods_name).equals("")) {
       qo.addQuery("obj.goods_name", 
         new SysMap("goods_name", "%" + 
         goods_name + "%"), "like");
     }
     qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
     IPageList pList = this.goodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + 
       "/admin/goods_floor_list_goods_load.htm", "", "&gc_id=" + 
       gc_id + "&goods_name=" + goods_name, pList, mv);
     return mv;
   }
 
   private Set<Long> genericIds(GoodsClass gc) {
     Set ids = new HashSet();
     ids.add(gc.getId());
     for (GoodsClass child : gc.getChilds()) {
       Set<Long> cids = genericIds(child);
       for (Long cid : cids) {
         ids.add(cid);
       }
       ids.add(child.getId());
     }
     return ids;
   }
 }


 
 
 