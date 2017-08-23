 package com.shopping.manage.admin.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.PredepositCash;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.PredepositCashQueryObject;
 import com.shopping.foundation.service.IPredepositCashService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import java.math.BigDecimal;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class PredepositCashManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IPredepositCashService predepositcashService;
 
   @Autowired
   private IUserService userService;
 
   @SecurityMapping(display = false, rsequence = 0, title="提现申请列表", value="/admin/predeposit_cash.htm*", rtype="admin", rname="预存款管理", rcode="predeposit", rgroup="会员")
   @RequestMapping({"/admin/predeposit_cash.htm"})
   public ModelAndView predeposit_cash(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String q_pd_userName, String q_beginTime, String q_endTime)
   {
     ModelAndView mv = new JModelAndView("admin/blue/predeposit_cash.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isDeposit()) {
       String url = this.configService.getSysConfig().getAddress();
       if ((url == null) || (url.equals(""))) {
         url = CommUtil.getURL(request);
       }
       PredepositCashQueryObject qo = new PredepositCashQueryObject(
         currentPage, mv, orderBy, orderType);
       WebForm wf = new WebForm();
       wf.toQueryPo(request, qo, PredepositCash.class, mv);
       if (!CommUtil.null2String(q_pd_userName).equals("")) {
         qo.addQuery("obj.cash_user.userName", 
           new SysMap("cash_userName", q_pd_userName), "=");
       }
       if (!CommUtil.null2String(q_beginTime).equals("")) {
         qo.addQuery("obj.addTime", 
           new SysMap("beginTime", 
           CommUtil.formatDate(q_beginTime)), ">=");
       }
       if (!CommUtil.null2String(q_endTime).equals("")) {
         qo.addQuery("obj.addTime", 
           new SysMap("endTime", 
           CommUtil.formatDate(q_endTime)), "<=");
       }
       IPageList pList = this.predepositcashService.list(qo);
       CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
       mv.addObject("q_pd_userName", q_pd_userName);
       mv.addObject("q_beginTime", q_beginTime);
       mv.addObject("q_endTime", q_endTime);
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
 
   @SecurityMapping(display = false, rsequence = 0, title="提现申请编辑", value="/admin/predeposit_cash_edit.htm*", rtype="admin", rname="预存款管理", rcode="predeposit", rgroup="会员")
   @RequestMapping({"/admin/predeposit_cash_edit.htm"})
   public ModelAndView predeposit_cash_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/predeposit_cash_edit.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isDeposit()) {
       if ((id != null) && (!id.equals(""))) {
         PredepositCash predepositcash = this.predepositcashService
           .getObjById(Long.valueOf(Long.parseLong(id)));
         mv.addObject("obj", predepositcash);
         mv.addObject("currentPage", currentPage);
       }
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
 
   @SecurityMapping(display = false, rsequence = 0, title="提现申请编辑保存", value="/admin/predeposit_cash_save.htm*", rtype="admin", rname="预存款管理", rcode="predeposit", rgroup="会员")
   @RequestMapping({"/admin/predeposit_cash_save.htm"})
   public ModelAndView predeposit_cash_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String cmd, String list_url)
   {
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isDeposit()) {
       WebForm wf = new WebForm();
       PredepositCash obj = this.predepositcashService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       PredepositCash predepositcash = (PredepositCash)wf.toPo(request, 
         obj);
       obj.setCash_admin(SecurityUserHolder.getCurrentUser());
       this.predepositcashService.update(predepositcash);
       User user = obj.getCash_user();
       user.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(user
         .getAvailableBalance(), predepositcash.getCash_amount())));
       this.userService.update(user);
       mv.addObject("list_url", list_url);
       mv.addObject("op_title", "审核提现申请成功");
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
   @SecurityMapping(display = false, rsequence = 0, title="提现申请详情", value="/admin/predeposit_cash_view.htm*", rtype="admin", rname="预存款管理", rcode="predeposit", rgroup="会员")
   @RequestMapping({"/admin/predeposit_cash_view.htm"})
   public ModelAndView predeposit_cash_view(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "admin/blue/predeposit_cash_view.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if (this.configService.getSysConfig().isDeposit()) {
       if ((id != null) && (!id.equals(""))) {
         PredepositCash predepositcash = this.predepositcashService
           .getObjById(Long.valueOf(Long.parseLong(id)));
         mv.addObject("obj", predepositcash);
       }
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


 
 
 