 package com.shopping.manage.admin.action;
 
 import com.easyjf.beans.BeanUtils;
 import com.easyjf.beans.BeanWrapper;
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.GoodsClass;
 import com.shopping.foundation.domain.GoodsClassStaple;
 import com.shopping.foundation.domain.GoodsType;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.query.GoodsClassQueryObject;
 import com.shopping.foundation.service.IAccessoryService;
 import com.shopping.foundation.service.IGoodsClassService;
 import com.shopping.foundation.service.IGoodsClassStapleService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.IGoodsTypeService;
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
 public class GoodsClassManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGoodsClassService goodsClassService;
 
   @Autowired
   private IGoodsTypeService goodsTypeService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IGoodsClassStapleService goodsClassStapleService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @SecurityMapping(display = false, rsequence = 0, title="商品分类列表", value="/admin/goods_class_list.htm*", rtype="admin", rname="分类管理", rcode="goods_class", rgroup="商品")
   @RequestMapping({"/admin/goods_class_list.htm"})
   public ModelAndView goods_class_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView("admin/blue/goods_class_list.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GoodsClassQueryObject qo = new GoodsClassQueryObject(currentPage, mv, 
       "sequence", "asc");
     qo.addQuery("obj.parent.id is null", null);
     WebForm wf = new WebForm();
     wf.toQueryPo(request, qo, GoodsClass.class, mv);
     IPageList pList = this.goodsClassService.list(qo);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     CommUtil.saveIPageList2ModelAndView(
       url + "/admin/goods_class_list.htm", "", "", pList, mv);
 
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品分类添加", value="/admin/goods_class_add.htm*", rtype="admin", rname="分类管理", rcode="goods_class", rgroup="商品")
   @RequestMapping({"/admin/goods_class_add.htm"})
   public ModelAndView goods_class_add(HttpServletRequest request, HttpServletResponse response, String pid)
   {
     ModelAndView mv = new JModelAndView("admin/blue/goods_class_add.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     List gcs = this.goodsClassService.query(
       "select obj from GoodsClass obj where obj.parent.id is null", 
       null, -1, -1);
     List gts = this.goodsTypeService.query(
       "select obj from GoodsType obj", null, -1, -1);
     if ((pid != null) && (!pid.equals(""))) {
       GoodsClass obj = new GoodsClass();
       GoodsClass parent = this.goodsClassService.getObjById(
         Long.valueOf(Long.parseLong(pid)));
       obj.setParent(parent);
       obj.setDisplay(true);
       obj.setRecommend(true);
       mv.addObject("obj", obj);
     }
     mv.addObject("gcs", gcs);
     mv.addObject("gts", gts);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品分类编辑", value="/admin/goods_class_edit.htm*", rtype="admin", rname="分类管理", rcode="goods_class", rgroup="商品")
   @RequestMapping({"/admin/goods_class_edit.htm"})
   public ModelAndView goods_class_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/goods_class_add.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       GoodsClass goodsClass = this.goodsClassService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       mv.addObject("obj", goodsClass);
       List gcs = this.goodsClassService
         .query("select obj from GoodsClass obj where obj.parent.id is null", 
         null, -1, -1);
       List gts = this.goodsTypeService.query(
         "select obj from GoodsType obj", null, -1, -1);
       mv.addObject("gcs", gcs);
       mv.addObject("gts", gts);
       mv.addObject("currentPage", currentPage);
       mv.addObject("edit", Boolean.valueOf(true));
     }
 
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品分类保存", value="/admin/goods_class_save.htm*", rtype="admin", rname="分类管理", rcode="goods_class", rgroup="商品")
   @RequestMapping({"/admin/goods_class_save.htm"})
   public ModelAndView goods_class_save(HttpServletRequest request, HttpServletResponse response, String id, String pid, String goodsTypeId, String currentPage, String list_url, String add_url, String child_link)
   {
     WebForm wf = new WebForm();
     GoodsClass goodsClass = null;
     if (id.equals("")) {
       goodsClass = (GoodsClass)wf.toPo(request, GoodsClass.class);
       goodsClass.setAddTime(new Date());
     } else {
       GoodsClass obj = this.goodsClassService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       goodsClass = (GoodsClass)wf.toPo(request, obj);
     }
     GoodsClass parent = this.goodsClassService.getObjById(
       CommUtil.null2Long(pid));
     if (parent != null) {
       goodsClass.setParent(parent);
       goodsClass.setLevel(parent.getLevel() + 1);
     }
     GoodsType goodsType = this.goodsTypeService.getObjById(
       CommUtil.null2Long(goodsTypeId));
     goodsClass.setGoodsType(goodsType);
     Set<Long> ids = genericIds(goodsClass);
 
     if (CommUtil.null2Boolean(child_link)) {
       for (Long gc_id : ids) {
         if (gc_id != null) {
           GoodsClass gc = this.goodsClassService.getObjById(gc_id);
           gc.setGoodsType(goodsType);
           this.goodsClassService.update(gc);
         }
       }
 
     }
 
     String uploadFilePath = this.configService.getSysConfig()
       .getUploadFilePath();
     String saveFilePathName = request.getSession().getServletContext()
       .getRealPath("/") + 
       uploadFilePath + File.separator + "class_icon";
     Map map = new HashMap();
     try {
       String fileName = goodsClass.getIcon_acc() == null ? "" : 
         goodsClass.getIcon_acc().getName();
       map = CommUtil.saveFileToServer(request, "icon_acc", 
         saveFilePathName, fileName, null);
       if (fileName.equals("")) {
         if (map.get("fileName") != "") {
           Accessory photo = new Accessory();
           photo.setName(CommUtil.null2String(map.get("fileName")));
           photo.setExt(CommUtil.null2String(map.get("mime")));
           photo.setSize(CommUtil.null2Float(map.get("fileSize")));
           photo.setPath(uploadFilePath + "/class_icon");
           photo.setWidth(CommUtil.null2Int(map.get("width")));
           photo.setHeight(CommUtil.null2Int(map.get("height")));
           photo.setAddTime(new Date());
           this.accessoryService.save(photo);
           goodsClass.setIcon_acc(photo);
         }
       }
       else if (map.get("fileName") != "") {
         Accessory photo = goodsClass.getIcon_acc();
         photo.setName(CommUtil.null2String(map.get("fileName")));
         photo.setExt(CommUtil.null2String(map.get("mime")));
         photo.setSize(CommUtil.null2Float(map.get("fileSize")));
         photo.setPath(uploadFilePath + "/class_icon");
         photo.setWidth(CommUtil.null2Int(map.get("width")));
         photo.setHeight(CommUtil.null2Int(map.get("height")));
         photo.setAddTime(new Date());
         this.accessoryService.update(photo);
       }
 
     }
     catch (IOException e)
     {
       e.printStackTrace();
     }
     if (id.equals(""))
       this.goodsClassService.save(goodsClass);
     else {
       this.goodsClassService.update(goodsClass);
     }
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     mv.addObject("op_title", "保存商品分类成功");
     mv.addObject("list_url", list_url);
     if (add_url != null) {
       mv.addObject("add_url", add_url + "?pid=" + pid);
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="商品分类下级加载", value="/admin/goods_class_data.htm*", rtype="admin", rname="分类管理", rcode="goods_class", rgroup="商品")
   @RequestMapping({"/admin/goods_class_data.htm"})
   public ModelAndView goods_class_data(HttpServletRequest request, HttpServletResponse response, String pid, String currentPage) {
     ModelAndView mv = new JModelAndView("admin/blue/goods_class_data.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Map map = new HashMap();
     map.put("pid", Long.valueOf(Long.parseLong(pid)));
     List gcs = this.goodsClassService.query(
       "select obj from GoodsClass obj where obj.parent.id =:pid", 
       map, -1, -1);
     mv.addObject("gcs", gcs);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品分类Ajax更新", value="/admin/goods_class_ajax.htm*", rtype="admin", rname="分类管理", rcode="goods_class", rgroup="商品")
   @RequestMapping({"/admin/goods_class_ajax.htm"})
   public void goods_class_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
     GoodsClass gc = this.goodsClassService.getObjById(Long.valueOf(Long.parseLong(id)));
     Field[] fields = GoodsClass.class.getDeclaredFields();
     BeanWrapper wrapper = new BeanWrapper(gc);
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
     this.goodsClassService.update(gc);
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
 
   private Set<Long> genericIds(GoodsClass gc)
   {
     Set<Long> ids = new HashSet<Long>();
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
   @SecurityMapping(display = false, rsequence = 0, title="商品分类批量推荐", value="/admin/goods_class_recommend.htm*", rtype="admin", rname="分类管理", rcode="goods_class", rgroup="商品")
   @RequestMapping({"/admin/goods_class_recommend.htm"})
   public String goods_class_recommend(HttpServletRequest request, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         GoodsClass gc = this.goodsClassService.getObjById(
           Long.valueOf(Long.parseLong(id)));
         gc.setRecommend(true);
         this.goodsClassService.update(gc);
       }
     }
     return "redirect:goods_class_list.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="商品分类批量删除", value="/admin/goods_class_del.htm*", rtype="admin", rname="分类管理", rcode="goods_class", rgroup="商品")
   @RequestMapping({"/admin/goods_class_del.htm"})
   public String goods_class_del(HttpServletRequest request, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         Set list = genericIds(this.goodsClassService
           .getObjById(Long.valueOf(Long.parseLong(id))));
         Map params = new HashMap();
         params.put("ids", list);
         List<GoodsClass> gcs = this.goodsClassService
           .query("select obj from GoodsClass obj where obj.id in (:ids) order by obj.level desc", 
           params, -1, -1);
         for (GoodsClass gc : gcs) {
           for (Goods goods : gc.getGoods_list()) {
             goods.setGc(null);
             this.goodsService.update(goods);
           }
           for (GoodsClassStaple gsc : gc.getGcss()) {
             this.goodsClassStapleService.delete(gsc.getId());
           }
           GoodsType type = gc.getGoodsType();
           if (type != null) {
             type.getGcs().remove(gc);
             this.goodsTypeService.update(type);
           }
           gc.setParent(null);
           this.goodsClassService.delete(gc.getId());
         }
       }
     }
     return "redirect:goods_class_list.htm?currentPage=" + currentPage;
   }
 
   @RequestMapping({"/admin/goods_class_verify.htm"})
   public void goods_class_verify(HttpServletRequest request, HttpServletResponse response, String className, String id, String pid)
   {
     boolean ret = true;
     Map params = new HashMap();
     params.put("className", className);
     params.put("id", CommUtil.null2Long(id));
     params.put("pid", CommUtil.null2Long(pid));
     List gcs = this.goodsClassService
       .query("select obj from GoodsClass obj where obj.className=:className and obj.id!=:id and obj.parent.id !=:pid", 
       params, -1, -1);
     if ((gcs != null) && (gcs.size() > 0)) {
       ret = false;
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(ret);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 }


 
 
 