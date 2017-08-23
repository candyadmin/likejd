 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.RefundLog;
 import com.shopping.foundation.service.IRefundLogService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class RefundLogServiceImpl
   implements IRefundLogService
 {
 
   @Resource(name="refundLogDAO")
   private IGenericDAO<RefundLog> refundLogDao;
 
   public boolean save(RefundLog refundLog)
   {
     try
     {
       this.refundLogDao.save(refundLog);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public RefundLog getObjById(Long id)
   {
     RefundLog refundLog = (RefundLog)this.refundLogDao.get(id);
     if (refundLog != null) {
       return refundLog;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.refundLogDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> refundLogIds)
   {
     for (Serializable id : refundLogIds) {
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
     GenericPageList pList = new GenericPageList(RefundLog.class, query, 
       params, this.refundLogDao);
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
 
   public boolean update(RefundLog refundLog) {
     try {
       this.refundLogDao.update(refundLog);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<RefundLog> query(String query, Map params, int begin, int max) {
     return this.refundLogDao.query(query, params, begin, max);
   }
 }



 
 