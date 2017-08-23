 package com.shopping.manage.admin.action;
 
 import com.easyjf.beans.BeanUtils;
 import com.easyjf.beans.BeanWrapper;
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.core.tools.database.DatabaseTools;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.domain.Album;
 import com.shopping.foundation.domain.Area;
 import com.shopping.foundation.domain.Evaluate;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.GoodsCart;
 import com.shopping.foundation.domain.Message;
 import com.shopping.foundation.domain.OrderForm;
 import com.shopping.foundation.domain.Store;
 import com.shopping.foundation.domain.StoreClass;
 import com.shopping.foundation.domain.StoreGradeLog;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.StoreGradeLogQueryObject;
 import com.shopping.foundation.domain.query.StoreQueryObject;
 import com.shopping.foundation.service.IAccessoryService;
 import com.shopping.foundation.service.IAlbumService;
 import com.shopping.foundation.service.IAreaService;
 import com.shopping.foundation.service.IConsultService;
 import com.shopping.foundation.service.IEvaluateService;
 import com.shopping.foundation.service.IGoodsCartService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.IMessageService;
 import com.shopping.foundation.service.IOrderFormLogService;
 import com.shopping.foundation.service.IOrderFormService;
 import com.shopping.foundation.service.IRoleService;
 import com.shopping.foundation.service.IStoreClassService;
 import com.shopping.foundation.service.IStoreGradeLogService;
 import com.shopping.foundation.service.IStoreGradeService;
 import com.shopping.foundation.service.IStoreService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.ITemplateService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import com.shopping.manage.admin.tools.AreaManageTools;
 import com.shopping.manage.admin.tools.StoreTools;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.OutputStreamWriter;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.lang.reflect.Field;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.apache.velocity.VelocityContext;
 import org.apache.velocity.app.Velocity;
 import org.nutz.json.Json;
 import org.nutz.json.JsonFormat;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class StoreManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IStoreService storeService;
 
   @Autowired
   private IStoreGradeService storeGradeService;
 
   @Autowired
   private IStoreClassService storeClassService;
 
   @Autowired
   private IAreaService areaService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IRoleService roleService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IConsultService consultService;
 
   @Autowired
   private AreaManageTools areaManageTools;
 
   @Autowired
   private StoreTools storeTools;
 
   @Autowired
   private DatabaseTools databaseTools;
 
   @Autowired
   private ITemplateService templateService;
 
   @Autowired
   private IMessageService messageService;
 
   @Autowired
   private IStoreGradeLogService storeGradeLogService;
 
   @Autowired
   private IEvaluateService evaluateService;
 
   @Autowired
   private IGoodsCartService goodsCartService;
 
   @Autowired
   private IOrderFormService orderFormService;
 
   @Autowired
   private IOrderFormLogService orderFormLogService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @Autowired
   private IAlbumService albumService;
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺列表", value="/admin/store_list.htm*", rtype="admin", rname="店铺管理", rcode="admin_store_set", rgroup="店铺")
   @RequestMapping({"/admin/store_list.htm"})
   public ModelAndView store_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView("admin/blue/store_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     StoreQueryObject qo = new StoreQueryObject(currentPage, mv, orderBy, 
       orderType);
     WebForm wf = new WebForm();
     wf.toQueryPo(request, qo, Store.class, mv);
     IPageList pList = this.storeService.list(qo);
     CommUtil.saveIPageList2ModelAndView(url + "/admin/store_list.htm", "", 
       params, pList, mv);
     List grades = this.storeGradeService.query(
       "select obj from StoreGrade obj order by obj.sequence asc", 
       null, -1, -1);
     mv.addObject("grades", grades);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺添加1", value="/admin/store_add.htm*", rtype="admin", rname="店铺管理", rcode="admin_store_set", rgroup="店铺")
   @RequestMapping({"/admin/store_add.htm"})
   public ModelAndView store_add(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/store_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺添加2", value="/admin/store_new.htm*", rtype="admin", rname="店铺管理", rcode="admin_store_set", rgroup="店铺")
   @RequestMapping({"/admin/store_new.htm"})
   public ModelAndView store_new(HttpServletRequest request, HttpServletResponse response, String currentPage, String userName, String list_url, String add_url)
   {
     ModelAndView mv = new JModelAndView("admin/blue/store_new.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     User user = this.userService.getObjByProperty("userName", userName);
     Store store = null;
     if (user != null)
       store = this.storeService.getObjByProperty("user.id", user.getId());
     if (user == null) {
       mv = new JModelAndView("admin/blue/tip.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_tip", "不存在该用户");
       mv.addObject("list_url", list_url);
     }
     else if (store == null) {
       List scs = this.storeClassService
         .query(
         "select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc", 
         null, -1, -1);
       List areas = this.areaService.query(
         "select obj from Area obj where obj.parent.id is null", 
         null, -1, -1);
       List grades = this.storeGradeService
         .query(
         "select obj from StoreGrade obj order by obj.sequence asc", 
         null, -1, -1);
       mv.addObject("grades", grades);
       mv.addObject("areas", areas);
       mv.addObject("scs", scs);
       mv.addObject("currentPage", currentPage);
       mv.addObject("user", user);
     } else {
       mv = new JModelAndView("admin/blue/tip.html", this.configService
         .getSysConfig(), 
         this.userConfigService.getUserConfig(), 0, request, 
         response);
       mv.addObject("op_tip", "该用户已经开通店铺");
       mv.addObject("list_url", add_url);
     }
 
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺编辑", value="/admin/store_edit.htm*", rtype="admin", rname="店铺管理", rcode="admin_store_set", rgroup="店铺")
   @RequestMapping({"/admin/store_edit.htm"})
   public ModelAndView store_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/store_edit.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
       List scs = this.storeClassService
         .query(
         "select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc", 
         null, -1, -1);
       List areas = this.areaService.query(
         "select obj from Area obj where obj.parent.id is null", 
         null, -1, -1);
       mv.addObject("areas", areas);
       mv.addObject("scs", scs);
       mv.addObject("obj", store);
       mv.addObject("currentPage", currentPage);
       mv.addObject("edit", Boolean.valueOf(true));
       if (store.getArea() != null) {
         mv.addObject("area_info", this.areaManageTools
           .generic_area_info(store.getArea()));
       }
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺保存", value="/admin/store_save.htm*", rtype="admin", rname="店铺管理", rcode="admin_store_set", rgroup="店铺")
   @RequestMapping({"/admin/store_save.htm"})
   public ModelAndView store_save(HttpServletRequest request, HttpServletResponse response, String id, String area_id, String sc_id, String grade_id, String user_id, String store_status, String currentPage, String cmd, String list_url, String add_url)
     throws Exception
   {
     WebForm wf = new WebForm();
     Store store = null;
     if (id.equals("")) {
       store = (Store)wf.toPo(request, Store.class);
       store.setAddTime(new Date());
     } else {
       Store obj = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
       store = (Store)wf.toPo(request, obj);
     }
     Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
     store.setArea(area);
     StoreClass sc = this.storeClassService
       .getObjById(Long.valueOf(Long.parseLong(sc_id)));
     store.setSc(sc);
     store.setTemplate("default");
     if ((grade_id != null) && (!grade_id.equals(""))) {
       store.setGrade(this.storeGradeService.getObjById(
         Long.valueOf(Long.parseLong(grade_id))));
     }
     if ((store_status != null) && (!store_status.equals("")))
       store.setStore_status(CommUtil.null2Int(store_status));
     else
       store.setStore_status(2);
     if (store.isStore_recommend())
       store.setStore_recommend_time(new Date());
     else
       store.setStore_recommend_time(null);
     if (id.equals(""))
       this.storeService.save(store);
     else
       this.storeService.update(store);
     if ((user_id != null) && (!user_id.equals(""))) {
       User user = this.userService.getObjById(Long.valueOf(Long.parseLong(user_id)));
       user.setStore(store);
       user.setUserRole("BUYER_SELLER");
 
       Map params = new HashMap();
       params.put("type", "SELLER");
       List roles = this.roleService.query(
         "select obj from Role obj where obj.type=:type", params, 
         -1, -1);
       user.getRoles().addAll(roles);
       this.userService.update(user);
     }
 
     if ((!id.equals("")) && (store.getStore_status() == 3)) {
       send_site_msg(request, "msg_toseller_store_closed_notify", 
         store);
     }
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("list_url", list_url);
     mv.addObject("op_title", "保存店铺成功");
     if (add_url != null) {
       mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
     }
     return mv;
   }
 
   private void send_site_msg(HttpServletRequest request, String mark, Store store) throws Exception
   {
     com.shopping.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
     if (template.isOpen()) {
       String path = request.getRealPath("/") + "/vm/";
       PrintWriter pwrite = new PrintWriter(
         new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
       pwrite.print(template.getContent());
       pwrite.flush();
       pwrite.close();
 
       Properties p = new Properties();
       p.setProperty("file.resource.loader.path", request
         .getRealPath("/") + 
         "vm" + File.separator);
       p.setProperty("input.encoding", "UTF-8");
       p.setProperty("output.encoding", "UTF-8");
       Velocity.init(p);
       org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", 
         "UTF-8");
       VelocityContext context = new VelocityContext();
       context.put("reason", store.getViolation_reseaon());
       context.put("user", store.getUser());
       context.put("config", this.configService.getSysConfig());
       context.put("send_time", CommUtil.formatLongDate(new Date()));
       StringWriter writer = new StringWriter();
       blank.merge(context, writer);
       String content = writer.toString();
       User fromUser = this.userService.getObjByProperty("userName", 
         "admin");
       Message msg = new Message();
       msg.setAddTime(new Date());
       msg.setContent(content);
       msg.setFromUser(fromUser);
       msg.setTitle(template.getTitle());
       msg.setToUser(store.getUser());
       msg.setType(0);
       this.messageService.save(msg);
       CommUtil.deleteFile(path + "msg.vm");
       writer.flush();
       writer.close();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺删除", value="/admin/store_del.htm*", rtype="admin", rname="店铺管理", rcode="admin_store_set", rgroup="店铺")
   @RequestMapping({"/admin/store_del.htm"})
   public String store_del(HttpServletRequest request, String mulitId) throws Exception {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
         if (store.getUser() != null)
           store.getUser().setStore(null);
         List<GoodsCart> goodCarts;
         for (Goods goods : store.getGoods_list()) {
           Map map = new HashMap();
           map.put("gid", goods.getId());
           goodCarts = this.goodsCartService
             .query(
             "select obj from GoodsCart obj where obj.goods.id = :gid", 
             map, -1, -1);
           Long ofid = null;
           Map map2;
           List goodCarts2;
           for (GoodsCart gc : goodCarts) {
             gc.getGsps().clear();
             this.goodsCartService.delete(gc.getId());
             if (gc.getOf() != null) {
               map2 = new HashMap();
               ofid = gc.getOf().getId();
               map2.put("ofid", ofid);
               goodCarts2 = this.goodsCartService
                 .query(
                 "select obj from GoodsCart obj where obj.of.id = :ofid", 
                 map2, -1, -1);
               if (goodCarts2.size() == 0) {
                 this.orderFormService.delete(ofid);
               }
             }
           }
 
           List<Evaluate> evaluates = goods.getEvaluates();
           for (Evaluate e : evaluates) {
             this.evaluateService.delete(e.getId());
           }
           goods.getGoods_ugcs().clear();
           Accessory acc = goods.getGoods_main_photo();
           if (acc != null) {
             acc.setAlbum(null);
             Album album = acc.getCover_album();
             if (album != null) {
               album.setAlbum_cover(null);
               this.albumService.update(album);
             }
             this.accessoryService.update(acc);
           }
           for (Accessory acc1 : goods.getGoods_photos()) {
             if (acc1 != null) {
               acc1.setAlbum(null);
               Album album = acc1.getCover_album();
               if (album != null) {
                 album.setAlbum_cover(null);
                 this.albumService.update(album);
               }
               acc1.setCover_album(null);
               this.accessoryService.update(acc1);
             }
           }
           goods.getCombin_goods().clear();
           this.goodsService.delete(goods.getId());
         }
         for (OrderForm of : store.getOfs()) {
           for (GoodsCart gc : of.getGcs()) {
             gc.getGsps().clear();
             this.goodsCartService.delete(gc.getId());
           }
           this.orderFormService.delete(of.getId());
         }
         this.storeService.delete(CommUtil.null2Long(id));
         send_site_msg(request, 
           "msg_toseller_goods_delete_by_admin_notify", store);
       }
     }
     return "redirect:store_list.htm";
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺AJAX更新", value="/admin/store_ajax.htm*", rtype="admin", rname="店铺管理", rcode="admin_store_set", rgroup="店铺")
   @RequestMapping({"/admin/store_ajax.htm"})
   public void store_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
     Store obj = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
     Field[] fields = Store.class.getDeclaredFields();
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
     if (fieldName.equals("store_recommend")) {
       if (obj.isStore_recommend())
         obj.setStore_recommend_time(new Date());
       else {
         obj.setStore_recommend_time(null);
       }
     }
     this.storeService.update(obj);
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
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家信用", value="/admin/store_base.htm*", rtype="admin", rname="基本设置", rcode="admin_store_base", rgroup="店铺")
   @RequestMapping({"/admin/store_base.htm"})
   public ModelAndView store_base(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView("admin/blue/store_base_set.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家信用保存", value="/admin/store_set_save.htm*", rtype="admin", rname="基本设置", rcode="admin_store_base", rgroup="店铺")
   @RequestMapping({"/admin/store_set_save.htm"})
   public ModelAndView store_set_save(HttpServletRequest request, HttpServletResponse response, String id, String list_url, String store_allow) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     SysConfig sc = this.configService.getSysConfig();
     sc.setStore_allow(CommUtil.null2Boolean(store_allow));
     Map map = new HashMap();
     for (int i = 0; i <= 29; i++) {
       map.put("creditrule" + i, Integer.valueOf(CommUtil.null2Int(request
         .getParameter("creditrule" + i))));
     }
     String creditrule = Json.toJson(map, JsonFormat.compact());
     sc.setCreditrule(creditrule);
     if (id.equals(""))
       this.configService.save(sc);
     else
       this.configService.update(sc);
     mv.addObject("list_url", list_url);
     mv.addObject("op_title", "保存店铺设置成功");
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺模板", value="/admin/store_template.htm*", rtype="admin", rname="店铺模板", rcode="admin_store_template", rgroup="店铺")
   @RequestMapping({"/admin/store_template.htm"})
   public ModelAndView store_template(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView("admin/blue/store_template.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("path", request.getRealPath("/"));
     mv.addObject("separator", File.separator);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺模板增加", value="/admin/store_template_add.htm*", rtype="admin", rname="店铺模板", rcode="admin_store_template", rgroup="店铺")
   @RequestMapping({"/admin/store_template_add.htm"})
   public ModelAndView store_template_add(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/store_template_add.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺模板保存", value="/admin/store_template_save.htm*", rtype="admin", rname="店铺模板", rcode="admin_store_template", rgroup="店铺")
   @RequestMapping({"/admin/store_template_save.htm"})
   public ModelAndView store_template_save(HttpServletRequest request, HttpServletResponse response, String id, String list_url, String templates) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     SysConfig sc = this.configService.getSysConfig();
     sc.setTemplates(templates);
     if (id.equals(""))
       this.configService.save(sc);
     else
       this.configService.update(sc);
     mv.addObject("list_url", list_url);
     mv.addObject("op_title", "店铺模板设置成功");
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺升级列表", value="/admin/store_gradelog_list.htm*", rtype="admin", rname="店铺管理", rcode="admin_store_set", rgroup="店铺")
   @RequestMapping({"/admin/store_gradelog_list.htm"})
   public ModelAndView store_gradelog_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String store_name, String grade_id, String store_grade_status)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/store_gradelog_list.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     StoreGradeLogQueryObject qo = new StoreGradeLogQueryObject(currentPage, 
       mv, orderBy, orderType);
     if (!CommUtil.null2String(store_name).equals("")) {
       qo.addQuery("obj.store.store_name", 
         new SysMap("store_name", "%" + 
         store_name + "%"), "like");
       mv.addObject("store_name", store_name);
     }
     if (CommUtil.null2Long(grade_id).longValue() != -1L) {
       qo.addQuery("obj.store.update_grade.id", 
         new SysMap("grade_id", 
         CommUtil.null2Long(grade_id)), "=");
       mv.addObject("grade_id", grade_id);
     }
     if (!CommUtil.null2String(store_grade_status).equals("")) {
       qo.addQuery("obj.store_grade_status", 
         new SysMap("store_grade_status", 
         Integer.valueOf(CommUtil.null2Int(store_grade_status))), "=");
       mv.addObject("store_grade_status", store_grade_status);
     }
     IPageList pList = this.storeGradeLogService.list(qo);
     CommUtil.saveIPageList2ModelAndView(url + "/admin/store_list.htm", "", 
       params, pList, mv);
     List grades = this.storeGradeService.query(
       "select obj from StoreGrade obj order by obj.sequence asc", 
       null, -1, -1);
     mv.addObject("grades", grades);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="店铺升级编辑", value="/admin/store_gradelog_edit.htm*", rtype="admin", rname="店铺管理", rcode="admin_store_set", rgroup="店铺")
   @RequestMapping({"/admin/store_gradelog_edit.htm"})
   public ModelAndView store_gradelog_edit(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/store_gradelog_edit.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     StoreGradeLog obj = this.storeGradeLogService.getObjById(
       CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺升级保存", value="/admin/store_gradelog_save.htm*", rtype="admin", rname="店铺管理", rcode="admin_store_set", rgroup="店铺")
   @RequestMapping({"/admin/store_gradelog_save.htm"})
   public ModelAndView store_gradelog_save(HttpServletRequest request, HttpServletResponse response, String currentPage, String id, String cmd, String list_url) throws Exception {
     WebForm wf = new WebForm();
     StoreGradeLog obj = this.storeGradeLogService.getObjById(
       CommUtil.null2Long(id));
     StoreGradeLog log = (StoreGradeLog)wf.toPo(request, obj);
     log.setLog_edit(true);
     log.setAddTime(new Date());
     boolean ret = this.storeGradeLogService.update(log);
     if (ret) {
       Store store = log.getStore();
       if (log.getStore_grade_status() == 1) {
         store.setGrade(store.getUpdate_grade());
       }
       store.setUpdate_grade(null);
       this.storeService.update(store);
     }
 
     if (log.getStore_grade_status() == 1) {
       send_site_msg(request, 
         "msg_toseller_store_update_allow_notify", log.getStore());
     }
     if (log.getStore_grade_status() == -1) {
       send_site_msg(request, 
         "msg_toseller_store_update_refuse_notify", log.getStore());
     }
     send_site_msg(request, "msg_toseller_store_update_allow_notify", 
       log.getStore());
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("list_url", list_url);
     mv.addObject("op_title", "保存店铺成功");
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="店铺升级日志查看", value="/admin/store_gradelog_view.htm*", rtype="admin", rname="店铺管理", rcode="admin_store_set", rgroup="店铺")
   @RequestMapping({"/admin/store_gradelog_view.htm"})
   public ModelAndView store_gradelog_view(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/store_gradelog_view.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     StoreGradeLog obj = this.storeGradeLogService.getObjById(
       CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 }


 
 
 