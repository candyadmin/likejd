 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.Coupon;
 import com.shopping.foundation.service.ICouponService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class CouponServiceImpl
   implements ICouponService
 {
 
   @Resource(name="couponDAO")
   private IGenericDAO<Coupon> couponDao;
 
   public boolean save(Coupon coupon)
   {
     try
     {
       this.couponDao.save(coupon);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public Coupon getObjById(Long id)
   {
     Coupon coupon = (Coupon)this.couponDao.get(id);
     if (coupon != null) {
       return coupon;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.couponDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> couponIds)
   {
     for (Serializable id : couponIds) {
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
     GenericPageList pList = new GenericPageList(Coupon.class, query, 
       params, this.couponDao);
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
 
   public boolean update(Coupon coupon) {
     try {
       this.couponDao.update(coupon);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<Coupon> query(String query, Map params, int begin, int max) {
     return this.couponDao.query(query, params, begin, max);
   }
 }



 
 