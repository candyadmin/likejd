 package com.shopping.view.web.action;
 
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.foundation.domain.Document;
 import com.shopping.foundation.service.IDocumentService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class DocumentViewAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IDocumentService documentService;
 
   @RequestMapping({"/doc.htm"})
   public ModelAndView doc(HttpServletRequest request, HttpServletResponse response, String mark)
   {
     ModelAndView mv = new JModelAndView("doc.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     Document obj = this.documentService.getObjByProperty("mark", mark);
     mv.addObject("obj", obj);
     return mv;
   }
 }


 
 
 