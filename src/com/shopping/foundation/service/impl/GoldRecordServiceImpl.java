 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.GoldRecord;
 import com.shopping.foundation.service.IGoldRecordService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class GoldRecordServiceImpl
   implements IGoldRecordService
 {
 
   @Resource(name="goldRecordDAO")
   private IGenericDAO<GoldRecord> goldRecordDao;
 
   public boolean save(GoldRecord goldRecord)
   {
     try
     {
       this.goldRecordDao.save(goldRecord);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public GoldRecord getObjById(Long id)
   {
     GoldRecord goldRecord = (GoldRecord)this.goldRecordDao.get(id);
     if (goldRecord != null) {
       return goldRecord;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.goldRecordDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> goldRecordIds)
   {
     for (Serializable id : goldRecordIds) {
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
     GenericPageList pList = new GenericPageList(GoldRecord.class, query, 
       params, this.goldRecordDao);
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
 
   public boolean update(GoldRecord goldRecord) {
     try {
       this.goldRecordDao.update(goldRecord);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<GoldRecord> query(String query, Map params, int begin, int max) {
     return this.goldRecordDao.query(query, params, begin, max);
   }
 }



 
 