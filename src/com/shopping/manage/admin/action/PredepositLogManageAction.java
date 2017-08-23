 package com.shopping.manage.admin.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.query.PredepositLogQueryObject;
 import com.shopping.foundation.service.IPredepositLogService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class PredepositLogManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IPredepositLogService predepositlogService;
 
   @SecurityMapping(display = false, rsequence = 0, title="预存款明细列表", value="/admin/predepositlog_list.htm*", rtype="admin", rname="预存款明细", rcode="predeposit", rgroup="会员")
   @RequestMapping({"/admin/predepositlog_list.htm"})
   public ModelAndView list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String userName)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/predepositlog_list.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isDeposit()) {
       String url = this.configService.getSysConfig().getAddress();
       if ((url == null) || (url.equals(""))) {
         url = CommUtil.getURL(request);
       }
       String params = "";
       PredepositLogQueryObject qo = new PredepositLogQueryObject(
         currentPage, mv, orderBy, orderType);
       if (!CommUtil.null2String(userName).equals("")) {
         qo.addQuery("obj.pd_log_user.userName", 
           new SysMap("userName", 
           userName), "=");
       }
       IPageList pList = this.predepositlogService.list(qo);
       CommUtil.saveIPageList2ModelAndView(url + 
         "/admin/predepositlog_list.htm", "", params, pList, mv);
       mv.addObject("userName", userName);
     } else {
       mv = new JModelAndView("admin/blue/error.html", this.configService
         .getSysConfig(), this.userConfigService.getUserConfig(), 0, 
         request, response);
       mv.addObject("op_title", "系统未开启预存款");
       mv.addObject("list_url", CommUtil.getURL(request) + 
         "/admin/operation_base_set.htm");
     }
     return mv;
   }
 }


 
 
 