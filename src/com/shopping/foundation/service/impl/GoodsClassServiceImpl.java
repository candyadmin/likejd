 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.GoodsClass;
 import com.shopping.foundation.service.IGoodsClassService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class GoodsClassServiceImpl
   implements IGoodsClassService
 {
 
   @Resource(name="goodsClassDAO")
   private IGenericDAO<GoodsClass> goodsClassDao;
 
   public boolean save(GoodsClass goodsClass)
   {
     try
     {
       this.goodsClassDao.save(goodsClass);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public GoodsClass getObjById(Long id)
   {
     GoodsClass goodsClass = (GoodsClass)this.goodsClassDao.get(id);
     if (goodsClass != null) {
       return goodsClass;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.goodsClassDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> goodsClassIds)
   {
     for (Serializable id : goodsClassIds) {
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
     GenericPageList pList = new GenericPageList(GoodsClass.class, query, 
       params, this.goodsClassDao);
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
 
   public boolean update(GoodsClass goodsClass) {
     try {
       this.goodsClassDao.update(goodsClass);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<GoodsClass> query(String query, Map params, int begin, int max)
   {
     return this.goodsClassDao.query(query, params, begin, max);
   }
 
   public GoodsClass getObjByProperty(String propertyName, Object value)
   {
     return (GoodsClass)this.goodsClassDao.getBy(propertyName, value);
   }
 }



 
 