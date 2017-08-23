 package com.shopping.manage.admin.action;
 
 import com.easyjf.beans.BeanUtils;
 import com.easyjf.beans.BeanWrapper;
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.StoreGrade;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.query.StoreGradeQueryObject;
 import com.shopping.foundation.service.IStoreGradeService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.lang.reflect.Field;
 import java.util.Date;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class StoreGradeManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IStoreGradeService storegradeService;
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺等级列表", value="/admin/storegrade_list.htm*", rtype="admin", rname="店铺等级", rcode="store_grade", rgroup="店铺")
   @RequestMapping({"/admin/storegrade_list.htm"})
   public ModelAndView storegrade_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView("admin/blue/storegrade_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     StoreGradeQueryObject qo = new StoreGradeQueryObject(currentPage, mv, 
       orderBy, orderType);
     WebForm wf = new WebForm();
     wf.toQueryPo(request, qo, StoreGrade.class, mv);
     IPageList pList = this.storegradeService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", params, pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺等级添加", value="/admin/storegrade_add.htm*", rtype="admin", rname="店铺等级", rcode="store_grade", rgroup="店铺")
   @RequestMapping({"/admin/storegrade_add.htm"})
   public ModelAndView storegrade_add(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/storegrade_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺等级编辑", value="/admin/storegrade_edit.htm*", rtype="admin", rname="店铺等级", rcode="store_grade", rgroup="店铺")
   @RequestMapping({"/admin/storegrade_edit.htm"})
   public ModelAndView storegrade_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/storegrade_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       StoreGrade storegrade = this.storegradeService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       mv.addObject("obj", storegrade);
       mv.addObject("currentPage", currentPage);
       mv.addObject("edit", Boolean.valueOf(true));
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺等级保存", value="/admin/storegrade_save.htm*", rtype="admin", rname="店铺等级", rcode="store_grade", rgroup="店铺")
   @RequestMapping({"/admin/storegrade_save.htm"})
   public ModelAndView storegrade_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String cmd, String list_url, String add_url)
   {
     WebForm wf = new WebForm();
     StoreGrade storegrade = null;
     if (id.equals("")) {
       storegrade = (StoreGrade)wf.toPo(request, StoreGrade.class);
       storegrade.setAddTime(new Date());
     } else {
       StoreGrade obj = this.storegradeService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       storegrade = (StoreGrade)wf.toPo(request, obj);
     }
 
     if (id.equals(""))
       this.storegradeService.save(storegrade);
     else
       this.storegradeService.update(storegrade);
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("list_url", list_url);
     mv.addObject("op_title", "保存店铺等级成功");
     if (add_url != null) {
       mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="店铺等级删除", value="/admin/storegrade_del.htm*", rtype="admin", rname="店铺等级", rcode="store_grade", rgroup="店铺")
   @RequestMapping({"/admin/storegrade_del.htm"})
   public String storegrade_del(HttpServletRequest request, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         StoreGrade storegrade = this.storegradeService.getObjById(
           Long.valueOf(Long.parseLong(id)));
         this.storegradeService.delete(Long.valueOf(Long.parseLong(id)));
       }
     }
     return "redirect:storegrade_list.htm?currentPage=" + currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺等级AJAX更新", value="/admin/storegrade_ajax.htm*", rtype="admin", rname="店铺等级", rcode="store_grade", rgroup="店铺")
   @RequestMapping({"/admin/storegrade_ajax.htm"})
   public void storegrade_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
     StoreGrade obj = this.storegradeService.getObjById(Long.valueOf(Long.parseLong(id)));
     Field[] fields = StoreGrade.class.getDeclaredFields();
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
     this.storegradeService.update(obj);
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
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺等级模板设置", value="/admin/storegrade_template.htm*", rtype="admin", rname="店铺等级", rcode="store_grade", rgroup="店铺")
   @RequestMapping({"/admin/storegrade_template.htm"})
   public ModelAndView storegrade_template(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/storegrade_template.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     mv.addObject("path", request.getRealPath("/"));
     mv.addObject("obj", this.storegradeService.getObjById(
       Long.valueOf(Long.parseLong(id))));
     mv.addObject("separator", File.separator);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="店铺等级模板保存", value="/admin/storegrade_template_save.htm*", rtype="admin", rname="店铺等级", rcode="store_grade", rgroup="店铺")
   @RequestMapping({"/admin/storegrade_template_save.htm"})
   public ModelAndView storegrade_template_save(HttpServletRequest request, HttpServletResponse response, String list_url, String id, String templates) {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     StoreGrade grade = this.storegradeService
       .getObjById(Long.valueOf(Long.parseLong(id)));
     grade.setTemplates(templates);
     this.storegradeService.update(grade);
     mv.addObject("list_url", list_url);
     mv.addObject("op_title", "保存店铺等级模板成功");
     return mv;
   }
 }


 
 
 