 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.CouponInfo;
 import com.shopping.foundation.service.ICouponInfoService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class CouponInfoServiceImpl
   implements ICouponInfoService
 {
 
   @Resource(name="couponInfoDAO")
   private IGenericDAO<CouponInfo> couponInfoDao;
 
   public boolean save(CouponInfo couponInfo)
   {
     try
     {
       this.couponInfoDao.save(couponInfo);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public CouponInfo getObjById(Long id)
   {
     CouponInfo couponInfo = (CouponInfo)this.couponInfoDao.get(id);
     if (couponInfo != null) {
       return couponInfo;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.couponInfoDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> couponInfoIds)
   {
     for (Serializable id : couponInfoIds) {
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
     GenericPageList pList = new GenericPageList(CouponInfo.class, query, 
       params, this.couponInfoDao);
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
 
   public boolean update(CouponInfo couponInfo) {
     try {
       this.couponInfoDao.update(couponInfo);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<CouponInfo> query(String query, Map params, int begin, int max) {
     return this.couponInfoDao.query(query, params, begin, max);
   }
 }



 
 