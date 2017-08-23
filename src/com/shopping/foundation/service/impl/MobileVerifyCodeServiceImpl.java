 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.MobileVerifyCode;
 import com.shopping.foundation.service.IMobileVerifyCodeService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class MobileVerifyCodeServiceImpl
   implements IMobileVerifyCodeService
 {
 
   @Resource(name="mobileVerifyCodeDAO")
   private IGenericDAO<MobileVerifyCode> mobileVerifyCodeDao;
 
   public boolean save(MobileVerifyCode mobileVerifyCode)
   {
     try
     {
       this.mobileVerifyCodeDao.save(mobileVerifyCode);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public MobileVerifyCode getObjById(Long id)
   {
     MobileVerifyCode mobileVerifyCode = (MobileVerifyCode)this.mobileVerifyCodeDao.get(id);
     if (mobileVerifyCode != null) {
       return mobileVerifyCode;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.mobileVerifyCodeDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> mobileVerifyCodeIds)
   {
     for (Serializable id : mobileVerifyCodeIds) {
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
     GenericPageList pList = new GenericPageList(MobileVerifyCode.class, query, 
       params, this.mobileVerifyCodeDao);
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
 
   public boolean update(MobileVerifyCode mobileVerifyCode) {
     try {
       this.mobileVerifyCodeDao.update(mobileVerifyCode);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<MobileVerifyCode> query(String query, Map params, int begin, int max) {
     return this.mobileVerifyCodeDao.query(query, params, begin, max);
   }
 
   public MobileVerifyCode getObjByProperty(String propertyName, Object value)
   {
     return (MobileVerifyCode)this.mobileVerifyCodeDao.getBy(propertyName, value);
   }
 }



 
 