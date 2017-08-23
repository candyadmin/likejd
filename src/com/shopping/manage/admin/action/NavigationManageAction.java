 package com.shopping.manage.admin.action;
 
 import com.easyjf.beans.BeanUtils;
 import com.easyjf.beans.BeanWrapper;
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.Navigation;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.query.NavigationQueryObject;
 import com.shopping.foundation.service.IActivityService;
 import com.shopping.foundation.service.IArticleClassService;
 import com.shopping.foundation.service.IGoodsClassService;
 import com.shopping.foundation.service.INavigationService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.lang.reflect.Field;
 import java.util.Date;
 import java.util.List;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class NavigationManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private INavigationService navigationService;
 
   @Autowired
   private IArticleClassService articleClassService;
 
   @Autowired
   private IGoodsClassService goodsClassService;
 
   @Autowired
   private IActivityService activityService;
 
   @SecurityMapping(display = false, rsequence = 0, title="页面导航列表", value="/admin/navigation_list.htm*", rtype="admin", rname="页面导航", rcode="nav_manage", rgroup="网站")
   @RequestMapping({"/admin/navigation_list.htm"})
   public ModelAndView list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String title)
   {
     ModelAndView mv = new JModelAndView("admin/blue/navigation_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     NavigationQueryObject qo = new NavigationQueryObject(currentPage, mv, 
       orderBy, orderType);
     WebForm wf = new WebForm();
     wf.toQueryPo(request, qo, Navigation.class, mv);
     if (!CommUtil.null2String(title).equals(""))
       qo.addQuery("obj.title", new SysMap("title", "%" + title + "%"), 
         "like");
     IPageList pList = this.navigationService.list(qo);
     CommUtil.saveIPageList2ModelAndView(url + "/admin/navigation_list.htm", 
       "", params, pList, mv);
     mv.addObject("title", title);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="页面导航添加", value="/admin/navigation_add.htm*", rtype="admin", rname="页面导航", rcode="nav_manage", rgroup="网站")
   @RequestMapping({"/admin/navigation_add.htm"})
   public ModelAndView add(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/navigation_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     List gcs = this.goodsClassService.query(
       "select obj from GoodsClass obj where obj.parent.id is null", 
       null, -1, -1);
     List acs = this.articleClassService
       .query(
       "select obj from ArticleClass obj where obj.parent is null order by obj.sequence asc", 
       null, -1, -1);
     List activitys = this.activityService.query(
       "select obj from Activity obj order by obj.addTime desc", null, 
       -1, -1);
     Navigation obj = new Navigation();
     obj.setDisplay(true);
     obj.setType("diy");
     obj.setNew_win(1);
     obj.setLocation(0);
     mv.addObject("obj", obj);
     mv.addObject("currentPage", currentPage);
     mv.addObject("gcs", gcs);
     mv.addObject("acs", acs);
     mv.addObject("activitys", activitys);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="页面导航编辑", value="/admin/navigation_edit.htm*", rtype="admin", rname="页面导航", rcode="nav_manage", rgroup="网站")
   @RequestMapping({"/admin/navigation_edit.htm"})
   public ModelAndView edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/navigation_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       Navigation navigation = this.navigationService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       List gcs = this.goodsClassService
         .query(
         "select obj from GoodsClass obj where obj.parent.id is null", 
         null, -1, -1);
       List acs = this.articleClassService
         .query(
         "select obj from ArticleClass obj where obj.parent is null order by obj.sequence asc", 
         null, -1, -1);
       List activitys = this.activityService.query(
         "select obj from Activity obj order by obj.addTime desc", 
         null, -1, -1);
       mv.addObject("gcs", gcs);
       mv.addObject("acs", acs);
       mv.addObject("activitys", activitys);
       mv.addObject("obj", navigation);
       mv.addObject("currentPage", currentPage);
       mv.addObject("edit", Boolean.valueOf(true));
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="页面导航保存", value="/admin/navigation_save.htm*", rtype="admin", rname="页面导航", rcode="nav_manage", rgroup="网站")
   @RequestMapping({"/admin/navigation_save.htm"})
   public ModelAndView save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String cmd, String list_url, String add_url, String goodsclass_id, String articleclass_id, String activity_id)
   {
     WebForm wf = new WebForm();
     Navigation nav = null;
     if (id.equals("")) {
       nav = (Navigation)wf.toPo(request, Navigation.class);
       nav.setAddTime(new Date());
     } else {
       Navigation obj = this.navigationService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       nav = (Navigation)wf.toPo(request, obj);
     }
     nav.setOriginal_url(nav.getUrl());
     if (nav.getType().equals("goodsclass")) {
       nav.setType_id(CommUtil.null2Long(goodsclass_id));
       nav.setUrl("store_goods_list_" + goodsclass_id + ".htm");
       nav.setOriginal_url("store_goods_list.htm?gc_id=" + goodsclass_id);
     }
     if (nav.getType().equals("articleclass")) {
       nav.setType_id(CommUtil.null2Long(articleclass_id));
       nav.setUrl("articlelist_" + articleclass_id + ".htm");
       nav.setOriginal_url("articlelist.htm?param=" + articleclass_id);
     }
     if (nav.getType().equals("activity")) {
       nav.setType_id(CommUtil.null2Long(activity_id));
       nav.setUrl("activity_" + activity_id + ".htm");
       nav.setOriginal_url("activity.htm?id=" + activity_id);
     }
     if (id.equals(""))
       this.navigationService.save(nav);
     else
       this.navigationService.update(nav);
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("list_url", list_url);
     mv.addObject("op_title", "保存页面导航成功");
     if (add_url != null) {
       mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="页面导航删除", value="/admin/navigation_del.htm*", rtype="admin", rname="页面导航", rcode="nav_manage", rgroup="网站")
   @RequestMapping({"/admin/navigation_del.htm"})
   public String delete(HttpServletRequest request, String mulitId) { String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         Navigation navigation = this.navigationService.getObjById(
           Long.valueOf(Long.parseLong(id)));
         this.navigationService.delete(Long.valueOf(Long.parseLong(id)));
       }
     }
     return "redirect:navigation_list.htm"; }
 
   @SecurityMapping(display = false, rsequence = 0, title="页面导航AJAX更新", value="/admin/navigation_ajax.htm*", rtype="admin", rname="页面导航", rcode="nav_manage", rgroup="网站")
   @RequestMapping({"/admin/navigation_ajax.htm"})
   public void ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException
   {
     Navigation obj = this.navigationService.getObjById(Long.valueOf(Long.parseLong(id)));
     Field[] fields = Navigation.class.getDeclaredFields();
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
     this.navigationService.update(obj);
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
 }


 
 
 