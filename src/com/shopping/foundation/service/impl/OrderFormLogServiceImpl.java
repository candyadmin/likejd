 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.OrderFormLog;
 import com.shopping.foundation.service.IOrderFormLogService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class OrderFormLogServiceImpl
   implements IOrderFormLogService
 {
 
   @Resource(name="orderFormLogDAO")
   private IGenericDAO<OrderFormLog> orderFormLogDao;
 
   public boolean save(OrderFormLog orderFormLog)
   {
     try
     {
       this.orderFormLogDao.save(orderFormLog);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public OrderFormLog getObjById(Long id)
   {
     OrderFormLog orderFormLog = (OrderFormLog)this.orderFormLogDao.get(id);
     if (orderFormLog != null) {
       return orderFormLog;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.orderFormLogDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> orderFormLogIds)
   {
     for (Serializable id : orderFormLogIds) {
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
     GenericPageList pList = new GenericPageList(OrderFormLog.class, query, 
       params, this.orderFormLogDao);
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
 
   public boolean update(OrderFormLog orderFormLog) {
     try {
       this.orderFormLogDao.update(orderFormLog);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<OrderFormLog> query(String query, Map params, int begin, int max) {
     return this.orderFormLogDao.query(query, params, begin, max);
   }
 }



 
 