 package com.shopping.view.web.action;
 
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Article;
 import com.shopping.foundation.domain.ArticleClass;
 import com.shopping.foundation.domain.query.ArticleQueryObject;
 import com.shopping.foundation.service.IArticleClassService;
 import com.shopping.foundation.service.IArticleService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.view.web.tools.ArticleViewTools;
 import java.util.List;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class ArticleViewAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IArticleService articleService;
 
   @Autowired
   private IArticleClassService articleClassService;
 
   @Autowired
   private ArticleViewTools articleTools;
 
   @RequestMapping({"/articlelist.htm"})
   public ModelAndView articlelist(HttpServletRequest request, HttpServletResponse response, String param, String currentPage)
   {
     ModelAndView mv = new JModelAndView("articlelist.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     ArticleClass ac = null;
     ArticleQueryObject aqo = new ArticleQueryObject();
     aqo.setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
     Long id = CommUtil.null2Long(param);
     String mark = "";
     if (id.longValue() == -1L) {
       mark = param;
     }
     if (!mark.equals("")) {
       aqo
         .addQuery("obj.articleClass.mark", 
         new SysMap("mark", mark), "=");
       ac = this.articleClassService.getObjByPropertyName("mark", mark);
     }
     if (id.longValue() != -1L) {
       aqo.addQuery("obj.articleClass.id", new SysMap("id", id), "=");
       ac = this.articleClassService.getObjById(id);
     }
     aqo.addQuery("obj.display", new SysMap("display", Boolean.valueOf(true)), "=");
     aqo.setOrderBy("addTime");
     aqo.setOrderType("desc");
     IPageList pList = this.articleService.list(aqo);
     String url = CommUtil.getURL(request) + "/articlelist_" + ac.getId();
     CommUtil.saveIPageList2ModelAndView("", url, "", pList, mv);
     List acs = this.articleClassService
       .query(
       "select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc", 
       null, -1, -1);
     List articles = this.articleService.query(
       "select obj from Article obj order by obj.addTime desc", null, 
       0, 6);
     mv.addObject("ac", ac);
     mv.addObject("articles", articles);
     mv.addObject("acs", acs);
     return mv;
   }
 
   @RequestMapping({"/article.htm"})
   public ModelAndView article(HttpServletRequest request, HttpServletResponse response, String param) {
     ModelAndView mv = new JModelAndView("article.html", this.configService.getSysConfig(), 
    		 this.userConfigService.getUserConfig(), 1, request, response);
     Article obj = null;
     Long id = CommUtil.null2Long(param);
     String mark = "";
     if (id.longValue() == -1L) {
       mark = param;
     }
     if (id.longValue() != -1L) {
       obj = this.articleService.getObjById(id);
     }
     if (!mark.equals("")) {
       obj = this.articleService.getObjByProperty("mark", mark);
     }
     List acs = this.articleClassService.query(
       "select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc", null, -1, -1);
     List articles = this.articleService.query(
       "select obj from Article obj order by obj.addTime desc", null, 0, 6);
     mv.addObject("articles", articles);
     mv.addObject("acs", acs);
     mv.addObject("obj", obj);
     mv.addObject("articleTools", this.articleTools);
     return mv;
   }
 }


 
 
 