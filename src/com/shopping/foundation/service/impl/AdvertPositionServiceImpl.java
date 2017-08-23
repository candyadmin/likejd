 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.AdvertPosition;
 import com.shopping.foundation.service.IAdvertPositionService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class AdvertPositionServiceImpl
   implements IAdvertPositionService
 {
 
   @Resource(name="advertPositionDAO")
   private IGenericDAO<AdvertPosition> advertPositionDao;
 
   public boolean save(AdvertPosition advertPosition)
   {
     try
     {
       this.advertPositionDao.save(advertPosition);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public AdvertPosition getObjById(Long id)
   {
     AdvertPosition advertPosition = (AdvertPosition)this.advertPositionDao.get(id);
     if (advertPosition != null) {
       return advertPosition;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.advertPositionDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> advertPositionIds)
   {
     for (Serializable id : advertPositionIds) {
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
     GenericPageList pList = new GenericPageList(AdvertPosition.class, query, 
       params, this.advertPositionDao);
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
 
   public boolean update(AdvertPosition advertPosition) {
     try {
       this.advertPositionDao.update(advertPosition);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<AdvertPosition> query(String query, Map params, int begin, int max) {
     return this.advertPositionDao.query(query, params, begin, max);
   }
 }



 
 