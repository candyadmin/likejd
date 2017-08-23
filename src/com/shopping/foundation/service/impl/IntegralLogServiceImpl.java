 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.IntegralLog;
 import com.shopping.foundation.service.IIntegralLogService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class IntegralLogServiceImpl
   implements IIntegralLogService
 {
 
   @Resource(name="integralLogDAO")
   private IGenericDAO<IntegralLog> integralLogDao;
 
   public boolean save(IntegralLog integralLog)
   {
     try
     {
       this.integralLogDao.save(integralLog);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public IntegralLog getObjById(Long id)
   {
     IntegralLog integralLog = (IntegralLog)this.integralLogDao.get(id);
     if (integralLog != null) {
       return integralLog;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.integralLogDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> integralLogIds)
   {
     for (Serializable id : integralLogIds) {
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
     GenericPageList pList = new GenericPageList(IntegralLog.class, query, 
       params, this.integralLogDao);
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
 
   public boolean update(IntegralLog integralLog) {
     try {
       this.integralLogDao.update(integralLog);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<IntegralLog> query(String query, Map params, int begin, int max) {
     return this.integralLogDao.query(query, params, begin, max);
   }
 }



 
 