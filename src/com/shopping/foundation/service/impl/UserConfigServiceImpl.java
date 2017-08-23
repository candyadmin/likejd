 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.UserConfig;
 import com.shopping.foundation.service.IUserConfigService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class UserConfigServiceImpl
   implements IUserConfigService
 {
 
   @Resource(name="userConfigDAO")
   private IGenericDAO<UserConfig> userConfigDao;
 
   @Resource(name="userDAO")
   private IGenericDAO<User> userDAO;
 
   public boolean save(UserConfig userConfig)
   {
     try
     {
       this.userConfigDao.save(userConfig);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public UserConfig getObjById(Long id)
   {
     UserConfig userConfig = (UserConfig)this.userConfigDao.get(id);
     if (userConfig != null) {
       return userConfig;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.userConfigDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> userConfigIds)
   {
     for (Serializable id : userConfigIds) {
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
     GenericPageList pList = new GenericPageList(UserConfig.class, query, 
       params, this.userConfigDao);
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
 
   public boolean update(UserConfig userConfig) {
     try {
       this.userConfigDao.update(userConfig);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<UserConfig> query(String query, Map params, int begin, int max)
   {
     return this.userConfigDao.query(query, params, begin, max);
   }
 
   public UserConfig getUserConfig()
   {
     User u = SecurityUserHolder.getCurrentUser();
     UserConfig config = null;
     if (u != null) {
       User user = (User)this.userDAO.get(u.getId());
       if (user != null)
         config = user.getConfig();
     }
     else {
       config = new UserConfig();
     }
     return config;
   }
 }



 
 