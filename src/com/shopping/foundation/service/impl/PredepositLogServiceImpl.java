 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.PredepositLog;
 import com.shopping.foundation.service.IPredepositLogService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class PredepositLogServiceImpl
   implements IPredepositLogService
 {
 
   @Resource(name="predepositLogDAO")
   private IGenericDAO<PredepositLog> predepositLogDao;
 
   public boolean save(PredepositLog predepositLog)
   {
     try
     {
       this.predepositLogDao.save(predepositLog);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public PredepositLog getObjById(Long id)
   {
     PredepositLog predepositLog = (PredepositLog)this.predepositLogDao.get(id);
     if (predepositLog != null) {
       return predepositLog;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.predepositLogDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> predepositLogIds)
   {
     for (Serializable id : predepositLogIds) {
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
     GenericPageList pList = new GenericPageList(PredepositLog.class, query, 
       params, this.predepositLogDao);
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
 
   public boolean update(PredepositLog predepositLog) {
     try {
       this.predepositLogDao.update(predepositLog);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<PredepositLog> query(String query, Map params, int begin, int max) {
     return this.predepositLogDao.query(query, params, begin, max);
   }
 }



 
 