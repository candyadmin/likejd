 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.GoldLog;
 import com.shopping.foundation.service.IGoldLogService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class GoldLogServiceImpl
   implements IGoldLogService
 {
 
   @Resource(name="goldLogDAO")
   private IGenericDAO<GoldLog> goldLogDao;
 
   public boolean save(GoldLog goldLog)
   {
     try
     {
       this.goldLogDao.save(goldLog);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public GoldLog getObjById(Long id)
   {
     GoldLog goldLog = (GoldLog)this.goldLogDao.get(id);
     if (goldLog != null) {
       return goldLog;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.goldLogDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> goldLogIds)
   {
     for (Serializable id : goldLogIds) {
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
     GenericPageList pList = new GenericPageList(GoldLog.class, query, 
       params, this.goldLogDao);
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
 
   public boolean update(GoldLog goldLog) {
     try {
       this.goldLogDao.update(goldLog);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<GoldLog> query(String query, Map params, int begin, int max) {
     return this.goldLogDao.query(query, params, begin, max);
   }
 }



 
 