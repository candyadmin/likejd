 package com.shopping.manage.seller.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Consult;
 import com.shopping.foundation.domain.Store;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.ConsultQueryObject;
 import com.shopping.foundation.service.IConsultService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.ITemplateService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import com.shopping.manage.admin.tools.MsgTools;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.OutputStreamWriter;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.util.Date;
 import java.util.Properties;
 import javax.servlet.ServletContext;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import org.apache.velocity.VelocityContext;
 import org.apache.velocity.app.Velocity;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class ConsultSellerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IConsultService consultService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private ITemplateService templateService;
 
   @Autowired
   private MsgTools msgTools;
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家咨询列表", value="/seller/consult.htm*", rtype="seller", rname="咨询管理", rcode="consult_seller", rgroup="客户服务")
   @RequestMapping({"/seller/consult.htm"})
   public ModelAndView consult(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/consult.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv, 
       "addTime", "desc");
     if (!CommUtil.null2String(reply).equals("")) {
       qo.addQuery("obj.reply", 
         new SysMap("reply", 
         Boolean.valueOf(CommUtil.null2Boolean(reply))), "=");
     }
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     qo.addQuery("obj.goods.goods_store.id", 
       new SysMap("store_id", user
       .getStore().getId()), "=");
     IPageList pList = this.consultService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("reply", CommUtil.null2String(reply));
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家咨询回复", value="/seller/consult_reply.htm*", rtype="seller", rname="咨询管理", rcode="consult_seller", rgroup="客户服务")
   @RequestMapping({"/seller/consult_reply.htm"})
   public ModelAndView consult_reply(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/consult_reply.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Consult obj = this.consultService.getObjById(CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家咨询回复保存", value="/seller/consult_reply_save.htm*", rtype="seller", rname="咨询管理", rcode="consult_seller", rgroup="客户服务")
   @RequestMapping({"/seller/consult_reply_save.htm"})
   public String consult_reply_save(HttpServletRequest request, HttpServletResponse response, String id, String consult_reply, String currentPage) throws Exception {
     Consult obj = this.consultService.getObjById(CommUtil.null2Long(id));
     obj.setConsult_reply(consult_reply);
     obj.setReply_time(new Date());
     obj.setReply_user(SecurityUserHolder.getCurrentUser());
     obj.setReply(true);
     this.consultService.update(obj);
     send_email(request, obj, "email_tobuyer_cousult_reply_notify");
     return "redirect:consult.htm?currentPage=" + currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家咨询删除", value="/seller/consult_del.htm*", rtype="seller", rname="咨询管理", rcode="consult_seller", rgroup="客户服务")
   @RequestMapping({"/seller/consult_del.htm"})
   public String consult_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String consult_reply, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         this.consultService.delete(CommUtil.null2Long(id));
       }
     }
     return "redirect:consult.htm?currentPage=" + currentPage;
   }
 
   private void send_email(HttpServletRequest request, Consult obj, String mark) throws Exception
   {
     com.shopping.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
     if (template.isOpen()) {
       String email = obj.getConsult_email();
       String subject = template.getTitle();
       String path = request.getSession().getServletContext()
         .getRealPath("/") + 
         "/vm/";
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
       context.put("buyer", obj.getConsult_user());
       context.put("config", this.configService.getSysConfig());
       context.put("send_time", CommUtil.formatLongDate(new Date()));
       context.put("webPath", CommUtil.getURL(request));
       context.put("goods", obj.getGoods());
       StringWriter writer = new StringWriter();
       blank.merge(context, writer);
 
       String content = writer.toString();
       this.msgTools.sendEmail(email, subject, content);
     }
   }
 }


 
 
 