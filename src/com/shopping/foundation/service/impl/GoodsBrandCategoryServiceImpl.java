 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.GoodsBrandCategory;
 import com.shopping.foundation.service.IGoodsBrandCategoryService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class GoodsBrandCategoryServiceImpl
   implements IGoodsBrandCategoryService
 {
 
   @Resource(name="goodsBrandCategoryDAO")
   private IGenericDAO<GoodsBrandCategory> goodsBrandCategoryDao;
 
   public boolean save(GoodsBrandCategory goodsBrandCategory)
   {
     try
     {
       this.goodsBrandCategoryDao.save(goodsBrandCategory);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public GoodsBrandCategory getObjById(Long id)
   {
     GoodsBrandCategory goodsBrandCategory = (GoodsBrandCategory)this.goodsBrandCategoryDao.get(id);
     if (goodsBrandCategory != null) {
       return goodsBrandCategory;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.goodsBrandCategoryDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> goodsBrandCategoryIds)
   {
     for (Serializable id : goodsBrandCategoryIds) {
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
     GenericPageList pList = new GenericPageList(GoodsBrandCategory.class, query, 
       params, this.goodsBrandCategoryDao);
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
 
   public boolean update(GoodsBrandCategory goodsBrandCategory) {
     try {
       this.goodsBrandCategoryDao.update(goodsBrandCategory);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<GoodsBrandCategory> query(String query, Map params, int begin, int max) {
     return this.goodsBrandCategoryDao.query(query, params, begin, max);
   }
 
   public GoodsBrandCategory getObjByProperty(String propertyName, Object value)
   {
     return (GoodsBrandCategory)this.goodsBrandCategoryDao.getBy(propertyName, value);
   }
 }



 
 