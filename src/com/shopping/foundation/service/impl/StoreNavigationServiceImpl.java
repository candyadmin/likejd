 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.StoreNavigation;
 import com.shopping.foundation.service.IStoreNavigationService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class StoreNavigationServiceImpl
   implements IStoreNavigationService
 {
 
   @Resource(name="storeNavigationDAO")
   private IGenericDAO<StoreNavigation> storeNavigationDao;
 
   public boolean save(StoreNavigation storeNavigation)
   {
     try
     {
       this.storeNavigationDao.save(storeNavigation);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public StoreNavigation getObjById(Long id)
   {
     StoreNavigation storeNavigation = (StoreNavigation)this.storeNavigationDao.get(id);
     if (storeNavigation != null) {
       return storeNavigation;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.storeNavigationDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> storeNavigationIds)
   {
     for (Serializable id : storeNavigationIds) {
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
     GenericPageList pList = new GenericPageList(StoreNavigation.class, query, 
       params, this.storeNavigationDao);
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
 
   public boolean update(StoreNavigation storeNavigation) {
     try {
       this.storeNavigationDao.update(storeNavigation);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<StoreNavigation> query(String query, Map params, int begin, int max) {
     return this.storeNavigationDao.query(query, params, begin, max);
   }
 }



 
 