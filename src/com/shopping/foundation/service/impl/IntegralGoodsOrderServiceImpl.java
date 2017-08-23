 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.IntegralGoodsOrder;
 import com.shopping.foundation.service.IIntegralGoodsOrderService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class IntegralGoodsOrderServiceImpl
   implements IIntegralGoodsOrderService
 {
 
   @Resource(name="integralGoodsOrderDAO")
   private IGenericDAO<IntegralGoodsOrder> integralGoodsOrderDao;
 
   public boolean save(IntegralGoodsOrder integralGoodsOrder)
   {
     try
     {
       this.integralGoodsOrderDao.save(integralGoodsOrder);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public IntegralGoodsOrder getObjById(Long id)
   {
     IntegralGoodsOrder integralGoodsOrder = (IntegralGoodsOrder)this.integralGoodsOrderDao.get(id);
     if (integralGoodsOrder != null) {
       return integralGoodsOrder;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.integralGoodsOrderDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> integralGoodsOrderIds)
   {
     for (Serializable id : integralGoodsOrderIds) {
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
     GenericPageList pList = new GenericPageList(IntegralGoodsOrder.class, query, 
       params, this.integralGoodsOrderDao);
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
 
   public boolean update(IntegralGoodsOrder integralGoodsOrder) {
     try {
       this.integralGoodsOrderDao.update(integralGoodsOrder);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<IntegralGoodsOrder> query(String query, Map params, int begin, int max) {
     return this.integralGoodsOrderDao.query(query, params, begin, max);
   }
 }



 
 