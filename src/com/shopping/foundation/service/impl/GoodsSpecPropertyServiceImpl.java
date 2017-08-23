 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.GoodsSpecProperty;
 import com.shopping.foundation.service.IGoodsSpecPropertyService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class GoodsSpecPropertyServiceImpl
   implements IGoodsSpecPropertyService
 {
 
   @Resource(name="goodsSpecPropertyDAO")
   private IGenericDAO<GoodsSpecProperty> goodsSpecPropertyDao;
 
   public boolean save(GoodsSpecProperty goodsSpecProperty)
   {
     try
     {
       this.goodsSpecPropertyDao.save(goodsSpecProperty);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public GoodsSpecProperty getObjById(Long id)
   {
     GoodsSpecProperty goodsSpecProperty = (GoodsSpecProperty)this.goodsSpecPropertyDao.get(id);
     if (goodsSpecProperty != null) {
       return goodsSpecProperty;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.goodsSpecPropertyDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> goodsSpecPropertyIds)
   {
     for (Serializable id : goodsSpecPropertyIds) {
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
     GenericPageList pList = new GenericPageList(GoodsSpecProperty.class, query, 
       params, this.goodsSpecPropertyDao);
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
 
   public boolean update(GoodsSpecProperty goodsSpecProperty) {
     try {
       this.goodsSpecPropertyDao.update(goodsSpecProperty);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<GoodsSpecProperty> query(String query, Map params, int begin, int max) {
     return this.goodsSpecPropertyDao.query(query, params, begin, max);
   }
 }



 
 