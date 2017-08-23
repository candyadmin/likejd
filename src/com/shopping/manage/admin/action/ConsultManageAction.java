 package com.shopping.manage.admin.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Consult;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.query.ConsultQueryObject;
 import com.shopping.foundation.service.IConsultService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class ConsultManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IConsultService consultService;
 
   @SecurityMapping(display = false, rsequence = 0, title="咨询列表", value="/admin/consult_list.htm*", rtype="admin", rname="咨询管理", rcode="consult_admin", rgroup="交易")
   @RequestMapping({"/admin/consult_list.htm"})
   public ModelAndView list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String consult_user_userName, String consult_content)
   {
     ModelAndView mv = new JModelAndView("admin/blue/consult_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv, 
       orderBy, orderType);
     qo.setPageSize(Integer.valueOf(1));
     if ((consult_user_userName != null) && (!consult_user_userName.equals(""))) {
       qo.addQuery("obj.consult_user.userName", 
         new SysMap("userName", 
         CommUtil.null2String(consult_user_userName).trim()), "=");
     }
     if ((consult_content != null) && (!consult_content.equals(""))) {
       qo.addQuery("obj.consult_content", 
         new SysMap("consult_content", 
         "%" + consult_content + "%"), "like");
     }
     IPageList pList = this.consultService.list(qo);
     CommUtil.saveIPageList2ModelAndView(url + "/admin/consult_list.htm", 
       "", params, pList, mv);
     mv.addObject("consult_user_userName", consult_user_userName);
     mv.addObject("consult_content", consult_content);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="咨询删除", value="/admin/consult_del.htm*", rtype="admin", rname="咨询管理", rcode="consult_admin", rgroup="交易")
   @RequestMapping({"/admin/consult_del.htm"})
   public String delete(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         Consult consult = this.consultService.getObjById(
           Long.valueOf(Long.parseLong(id)));
         this.consultService.delete(Long.valueOf(Long.parseLong(id)));
       }
     }
     return "redirect:consult_list.htm?currentPage=" + currentPage;
   }
 }


 
 
 