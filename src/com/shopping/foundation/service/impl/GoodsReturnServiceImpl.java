 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.GoodsReturn;
 import com.shopping.foundation.service.IGoodsReturnService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class GoodsReturnServiceImpl
   implements IGoodsReturnService
 {
 
   @Resource(name="goodsReturnDAO")
   private IGenericDAO<GoodsReturn> goodsReturnDao;
 
   public boolean save(GoodsReturn goodsReturn)
   {
     try
     {
       this.goodsReturnDao.save(goodsReturn);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public GoodsReturn getObjById(Long id)
   {
     GoodsReturn goodsReturn = (GoodsReturn)this.goodsReturnDao.get(id);
     if (goodsReturn != null) {
       return goodsReturn;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.goodsReturnDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> goodsReturnIds)
   {
     for (Serializable id : goodsReturnIds) {
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
     GenericPageList pList = new GenericPageList(GoodsReturn.class, query, 
       params, this.goodsReturnDao);
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
 
   public boolean update(GoodsReturn goodsReturn) {
     try {
       this.goodsReturnDao.update(goodsReturn);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<GoodsReturn> query(String query, Map params, int begin, int max) {
     return this.goodsReturnDao.query(query, params, begin, max);
   }
 }



 
 