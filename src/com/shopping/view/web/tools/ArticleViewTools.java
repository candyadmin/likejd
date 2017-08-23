 package com.shopping.view.web.tools;
 
 import com.shopping.foundation.domain.Article;
 import com.shopping.foundation.domain.ArticleClass;
 import com.shopping.foundation.service.IArticleService;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 
 @Component
 public class ArticleViewTools
 {
 
   @Autowired
   private IArticleService articleService;
 
   public Article queryArticle(Long id, int position)
   {
     String query = "select obj from Article obj where obj.articleClass.id=:class_id and obj.display=:display and ";
     Article article = this.articleService.getObjById(id);
     if (article != null) {
       Map params = new HashMap();
       params.put("addTime", article.getAddTime());
       params.put("class_id", article.getArticleClass().getId());
       params.put("display", Boolean.valueOf(true));
       if (position > 0)
         query = query + 
           "obj.addTime>:addTime order by obj.addTime desc";
       else {
         query = query + 
           "obj.addTime<:addTime order by obj.addTime desc";
       }
       List objs = this.articleService.query(query, params, 0, 1);
       if (objs.size() > 0) {
         return (Article)objs.get(0);
       }
       Article obj = new Article();
       obj.setTitle("没有了");
       return obj;
     }
 
     Article obj = new Article();
     obj.setTitle("没有了");
     return obj;
   }
 }


 
 
 