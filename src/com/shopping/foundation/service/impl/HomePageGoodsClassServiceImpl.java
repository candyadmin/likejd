 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.HomePageGoodsClass;
 import com.shopping.foundation.service.IHomePageGoodsClassService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class HomePageGoodsClassServiceImpl
   implements IHomePageGoodsClassService
 {
 
   @Resource(name="homePageGoodsClassDAO")
   private IGenericDAO<HomePageGoodsClass> homePageGoodsClassDao;
 
   public boolean save(HomePageGoodsClass homePageGoodsClass)
   {
     try
     {
       this.homePageGoodsClassDao.save(homePageGoodsClass);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public HomePageGoodsClass getObjById(Long id)
   {
     HomePageGoodsClass homePageGoodsClass = (HomePageGoodsClass)this.homePageGoodsClassDao.get(id);
     if (homePageGoodsClass != null) {
       return homePageGoodsClass;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.homePageGoodsClassDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> homePageGoodsClassIds)
   {
     for (Serializable id : homePageGoodsClassIds) {
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
     GenericPageList pList = new GenericPageList(HomePageGoodsClass.class, query, 
       params, this.homePageGoodsClassDao);
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
 
   public boolean update(HomePageGoodsClass homePageGoodsClass) {
     try {
       this.homePageGoodsClassDao.update(homePageGoodsClass);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<HomePageGoodsClass> query(String query, Map params, int begin, int max) {
     return this.homePageGoodsClassDao.query(query, params, begin, max);
   }
 }



 
 