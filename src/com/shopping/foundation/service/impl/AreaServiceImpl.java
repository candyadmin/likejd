 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.Area;
 import com.shopping.foundation.service.IAreaService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class AreaServiceImpl
   implements IAreaService
 {
 
   @Resource(name="areaDAO")
   private IGenericDAO<Area> areaDao;
 
   public boolean save(Area area)
   {
     try
     {
       this.areaDao.save(area);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public Area getObjById(Long id)
   {
     Area area = (Area)this.areaDao.get(id);
     if (area != null) {
       return area;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.areaDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> areaIds)
   {
     for (Serializable id : areaIds) {
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
     GenericPageList pList = new GenericPageList(Area.class, query, 
       params, this.areaDao);
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
 
   public boolean update(Area area) {
     try {
       this.areaDao.update(area);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<Area> query(String query, Map params, int begin, int max) {
     return this.areaDao.query(query, params, begin, max);
   }
 }



 
 