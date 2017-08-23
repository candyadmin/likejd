 package com.shopping.manage.admin.action;
 
 import com.easyjf.beans.BeanUtils;
 import com.easyjf.beans.BeanWrapper;
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.StoreClass;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.query.StoreClassQueryObject;
 import com.shopping.foundation.service.IStoreClassService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.lang.reflect.Field;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class StoreClassManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IStoreClassService storeclassService;
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺分类列表", value="/admin/storeclass_list.htm*", rtype="admin", rname="店铺分类", rcode="store_class", rgroup="店铺")
   @RequestMapping({"/admin/storeclass_list.htm"})
   public ModelAndView list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView("admin/blue/storeclass_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     StoreClassQueryObject qo = new StoreClassQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.setOrderBy("sequence");
     qo.setOrderType("asc");
     qo.addQuery("obj.parent is null", null);
     IPageList pList = this.storeclassService.list(qo);
     CommUtil.saveIPageList2ModelAndView(url + "/admin/storeclass_list.htm", 
       "", params, pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺分类添加", value="/admin/storeclass_add.htm*", rtype="admin", rname="店铺分类", rcode="store_class", rgroup="店铺")
   @RequestMapping({"/admin/storeclass_add.htm"})
   public ModelAndView add(HttpServletRequest request, HttpServletResponse response, String pid)
   {
     ModelAndView mv = new JModelAndView("admin/blue/storeclass_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     List scs = this.storeclassService
       .query(
       "select obj from StoreClass obj where obj.parent is null order by obj.sequence asc", 
       null, -1, -1);
     mv.addObject("scs", scs);
     if ((pid != null) && (!pid.equals(""))) {
       StoreClass obj = new StoreClass();
       obj.setParent(this.storeclassService
         .getObjById(Long.valueOf(Long.parseLong(pid))));
       mv.addObject("obj", obj);
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺分类编辑", value="/admin/storeclass_edit.htm*", rtype="admin", rname="店铺分类", rcode="store_class", rgroup="店铺")
   @RequestMapping({"/admin/storeclass_edit.htm"})
   public ModelAndView edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/storeclass_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       StoreClass storeclass = this.storeclassService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       List scs = this.storeclassService
         .query(
         "select obj from StoreClass obj where obj.parent is null order by obj.sequence asc", 
         null, -1, -1);
       mv.addObject("scs", scs);
       mv.addObject("obj", storeclass);
       mv.addObject("currentPage", currentPage);
       mv.addObject("edit", Boolean.valueOf(true));
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺分类保存", value="/admin/storeclass_save.htm*", rtype="admin", rname="店铺分类", rcode="store_class", rgroup="店铺")
   @RequestMapping({"/admin/storeclass_save.htm"})
   public ModelAndView save(HttpServletRequest request, HttpServletResponse response, String id, String pid, String currentPage, String cmd, String list_url, String add_url)
   {
     WebForm wf = new WebForm();
     StoreClass storeclass = null;
     if (id.equals("")) {
       storeclass = (StoreClass)wf.toPo(request, StoreClass.class);
       storeclass.setAddTime(new Date());
     } else {
       StoreClass obj = this.storeclassService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       storeclass = (StoreClass)wf.toPo(request, obj);
     }
     if ((pid != null) && (!pid.equals(""))) {
       StoreClass parent = this.storeclassService.getObjById(
         Long.valueOf(Long.parseLong(pid)));
       storeclass.setParent(parent);
       storeclass.setLevel(parent.getLevel() + 1);
     }
     if (id.equals(""))
       this.storeclassService.save(storeclass);
     else
       this.storeclassService.update(storeclass);
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("list_url", list_url);
     mv.addObject("op_title", "保存店铺分类成功");
     if (add_url != null) {
       mv.addObject("add_url", add_url + "?currentPage=" + currentPage + 
         "&pid=" + pid);
     }
     return mv;
   }
 
   private Set<Long> genericIds(StoreClass sc) {
     Set ids = new HashSet();
     ids.add(sc.getId());
     for (StoreClass child : sc.getChilds()) {
       Set<Long> cids = genericIds(child);
       for (Long cid : cids) {
         ids.add(cid);
       }
       ids.add(child.getId());
     }
     return ids;
   }
   @SecurityMapping(display = false, rsequence = 0, title="店铺分类删除", value="/admin/storeclass_del.htm*", rtype="admin", rname="店铺分类", rcode="store_class", rgroup="店铺")
   @RequestMapping({"/admin/storeclass_del.htm"})
   public String delete(HttpServletRequest request, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         Set list = genericIds(this.storeclassService
           .getObjById(Long.valueOf(Long.parseLong(id))));
         Map params = new HashMap();
         params.put("ids", list);
         List<StoreClass> scs = this.storeclassService
           .query(
           "select obj from StoreClass obj where obj.id in (:ids) order by obj.level desc", 
           params, -1, -1);
         for (StoreClass sc : scs) {
           sc.setParent(null);
           this.storeclassService.delete(sc.getId());
         }
       }
     }
     return "redirect:storeclass_list.htm?currentPage=" + currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺分类AJAX保存", value="/admin/storeclass_ajax.htm*", rtype="admin", rname="店铺分类", rcode="store_class", rgroup="店铺")
   @RequestMapping({"/admin/storeclass_ajax.htm"})
   public void ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
     StoreClass obj = this.storeclassService.getObjById(Long.valueOf(Long.parseLong(id)));
     Field[] fields = StoreClass.class.getDeclaredFields();
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
     this.storeclassService.update(obj);
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
 
   @RequestMapping({"/admin/storeclass_verify.htm"})
   public void storeclass_verify(HttpServletRequest request, HttpServletResponse response, String className, String id)
   {
     boolean ret = true;
     Map params = new HashMap();
     params.put("className", className);
     params.put("id", CommUtil.null2Long(id));
     List gcs = this.storeclassService
       .query(
       "select obj from StoreClass obj where obj.className=:className and obj.id!=:id", 
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
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺分类下级数据加载", value="/admin/storeclass_data.htm*", rtype="admin", rname="店铺分类", rcode="store_class", rgroup="店铺")
   @RequestMapping({"/admin/storeclass_data.htm"})
   public ModelAndView storeclass_data(HttpServletRequest request, HttpServletResponse response, String pid, String currentPage) {
     ModelAndView mv = new JModelAndView("admin/blue/storeclass_data.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     Map map = new HashMap();
     map.put("pid", Long.valueOf(Long.parseLong(pid)));
     List gcs = this.storeclassService.query(
       "select obj from StoreClass obj where obj.parent.id =:pid", 
       map, -1, -1);
     mv.addObject("gcs", gcs);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 }


 
 
 