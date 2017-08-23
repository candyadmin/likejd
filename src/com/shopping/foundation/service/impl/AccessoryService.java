 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.service.IAccessoryService;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class AccessoryService
   implements IAccessoryService
 {
 
   @Resource(name="accessoryDAO")
   private IGenericDAO<Accessory> accessoryDAO;
 
   public boolean delete(Long id)
   {
     try
     {
       this.accessoryDAO.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public Accessory getObjById(Long id)
   {
     return (Accessory)this.accessoryDAO.get(id);
   }
 
   public boolean save(Accessory acc)
   {
     try {
       this.accessoryDAO.save(acc);
       return true; } catch (Exception e) {
     }
     return false;
   }
 
   public boolean update(Accessory acc)
   {
     try
     {
       this.accessoryDAO.update(acc);
       return true; } catch (Exception e) {
     }
     return false;
   }
 
   public List<Accessory> query(String query, Map params, int begin, int max)
   {
     return this.accessoryDAO.query(query, params, begin, max);
   }
 
   public IPageList list(IQueryObject properties)
   {
     if (properties == null) {
       return null;
     }
     String query = properties.getQuery();
     Map params = properties.getParameters();
     GenericPageList pList = new GenericPageList(Accessory.class, query, 
       params, this.accessoryDAO);
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
 
   public Accessory getObjByProperty(String propertyName, String value)
   {
     return (Accessory)this.accessoryDAO.getBy(propertyName, value);
   }
 }



 
 