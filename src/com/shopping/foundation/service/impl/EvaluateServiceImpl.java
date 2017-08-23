 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.Evaluate;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.service.IEvaluateService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class EvaluateServiceImpl
   implements IEvaluateService
 {
 
   @Resource(name="evaluateDAO")
   private IGenericDAO<Evaluate> evaluateDao;
 
   public boolean save(Evaluate evaluate)
   {
     try
     {
       this.evaluateDao.save(evaluate);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public Evaluate getObjById(Long id)
   {
     Evaluate evaluate = (Evaluate)this.evaluateDao.get(id);
     if (evaluate != null) {
       return evaluate;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.evaluateDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> evaluateIds)
   {
     for (Serializable id : evaluateIds) {
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
     GenericPageList pList = new GenericPageList(Evaluate.class, query, 
       params, this.evaluateDao);
     if (properties != null) {
       PageObject pageObj = properties.getPageObj();
       if (pageObj != null)
         pList.doList(
           pageObj.getCurrentPage() == null ? 0 : pageObj
           .getCurrentPage().intValue(), 
           pageObj.getPageSize() == null ? 0 : pageObj
           .getPageSize().intValue());
     } else {
       pList.doList(0, -1);
     }return pList;
   }
 
   public boolean update(Evaluate evaluate) {
     try {
       this.evaluateDao.update(evaluate);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<Evaluate> query(String query, Map params, int begin, int max)
   {
     return this.evaluateDao.query(query, params, begin, max);
   }
 
   public List<Goods> query_goods(String query, Map params, int begin, int max)
   {
     return this.evaluateDao.query(query, params, begin, max);
   }
 }



 
 