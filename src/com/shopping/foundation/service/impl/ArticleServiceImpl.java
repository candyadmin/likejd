 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.Article;
 import com.shopping.foundation.service.IArticleService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class ArticleServiceImpl
   implements IArticleService
 {
 
   @Resource(name="articleDAO")
   private IGenericDAO<Article> articleDao;
 
   public boolean save(Article article)
   {
     try
     {
       this.articleDao.save(article);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public Article getObjById(Long id)
   {
     Article article = (Article)this.articleDao.get(id);
     if (article != null) {
       return article;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.articleDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> articleIds)
   {
     for (Serializable id : articleIds) {
       delete((Long)id);
     }
     return true;
   }
 
   public IPageList list(IQueryObject properties) {
     if (properties == null) {
       return null;
     }
     String query = properties.getQuery();
     Map params = properties.getParameters();
     GenericPageList pList = new GenericPageList(Article.class, query, 
       params, this.articleDao);
     if (properties != null) {
       PageObject pageObj = properties.getPageObj();
       if (pageObj != null)
         pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
           .getCurrentPage().intValue(), pageObj.getPageSize() == null ? 0 : 
           pageObj.getPageSize().intValue());
     } else {
       pList.doList(0, -1);
     }return pList;
   }
 
   public boolean update(Article article) {
     try {
       this.articleDao.update(article);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<Article> query(String query, Map params, int begin, int max)
   {
     return this.articleDao.query(query, params, begin, max);
   }
 
   public Article getObjByProperty(String propertyName, Object value)
   {
     Article obj;
     try {
       return (Article)this.articleDao.getBy(propertyName, value);
     } catch (Exception e) {
       obj = new Article();
       obj.setTitle("文章错误");
     }return obj;
   }
 }



 
 