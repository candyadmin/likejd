 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.StoreCart;
 import com.shopping.foundation.service.IStoreCartService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class StoreCartServiceImpl
   implements IStoreCartService
 {
 
   @Resource(name="storeCartDAO")
   private IGenericDAO<StoreCart> storeCartDao;
 
   public boolean save(StoreCart storeCart)
   {
     try
     {
       this.storeCartDao.save(storeCart);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public StoreCart getObjById(Long id)
   {
     StoreCart storeCart = (StoreCart)this.storeCartDao.get(id);
     if (storeCart != null) {
       return storeCart;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.storeCartDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> storeCartIds)
   {
     for (Serializable id : storeCartIds) {
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
     GenericPageList pList = new GenericPageList(StoreCart.class, query, 
       params, this.storeCartDao);
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
 
   public boolean update(StoreCart storeCart) {
     try {
       this.storeCartDao.update(storeCart);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<StoreCart> query(String query, Map params, int begin, int max) {
     return this.storeCartDao.query(query, params, begin, max);
   }
 }



 
 