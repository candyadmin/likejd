 package com.shopping.manage.seller.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.RefundLog;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.RefundLogQueryObject;
 import com.shopping.foundation.service.IRefundLogService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class RefundSellerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IRefundLogService refundLogService;
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家退款列表", value="/seller/refund.htm*", rtype="seller", rname="退款管理", rcode="refund_seller", rgroup="客户服务")
   @RequestMapping({"/seller/refund.htm"})
   public ModelAndView refund(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String data_type, String data, String beginTime, String endTime)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/refund.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     RefundLogQueryObject qo = new RefundLogQueryObject(currentPage, mv, 
       "addTime", "desc");
     qo.setPageSize(Integer.valueOf(30));
     qo.addQuery("obj.refund_user.id", 
       new SysMap("refund_user", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     if (!CommUtil.null2String(data).equals("")) {
       if (CommUtil.null2String(data_type).equals("order_id")) {
         qo.addQuery("obj.of.order_id", new SysMap("order_id", data), 
           "=");
       }
       if (CommUtil.null2String(data_type).equals("buyer_name")) {
         qo.addQuery("obj.of.user.userName", 
           new SysMap("userName", data), "=");
       }
     }
     if (!CommUtil.null2String(beginTime).equals("")) {
       qo.addQuery("obj.addTime", 
         new SysMap("beginTime", 
         CommUtil.formatDate(beginTime)), ">=");
     }
     if (!CommUtil.null2String(endTime).equals("")) {
       qo.addQuery("obj.addTime", 
         new SysMap("endTime", 
         CommUtil.formatDate(endTime)), "<=");
     }
     IPageList pList = this.refundLogService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("data_type", data_type);
     mv.addObject("data", data);
     mv.addObject("beginTime", beginTime);
     mv.addObject("endTime", endTime);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家退款列表", value="/seller/refund_view.htm*", rtype="seller", rname="退款管理", rcode="refund_seller", rgroup="客户服务")
   @RequestMapping({"/seller/refund_view.htm"})
   public ModelAndView refund_view(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/refund_view.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     RefundLog obj = this.refundLogService
       .getObjById(CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     return mv;
   }
 }


 
 
 