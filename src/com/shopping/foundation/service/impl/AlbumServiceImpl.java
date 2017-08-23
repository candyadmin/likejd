 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.Album;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.service.IAlbumService;
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class AlbumServiceImpl
   implements IAlbumService
 {
 
   @Resource(name="albumDAO")
   private IGenericDAO<Album> albumDao;
 
   @Resource(name="userDAO")
   private IGenericDAO<User> userDAO;
 
   public boolean save(Album album)
   {
     try
     {
       this.albumDao.save(album);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public Album getObjById(Long id)
   {
     Album album = (Album)this.albumDao.get(id);
     if (album != null) {
       return album;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.albumDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> albumIds)
   {
     for (Serializable id : albumIds) {
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
     GenericPageList pList = new GenericPageList(Album.class, query, params, 
       this.albumDao);
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
 
   public boolean update(Album album) {
     try {
       this.albumDao.update(album);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<Album> query(String query, Map params, int begin, int max)
   {
     return this.albumDao.query(query, params, begin, max);
   }
 
   public Album getDefaultAlbum(Long id)
   {
     User user = (User)this.userDAO.get(id);
     if (user.getParent() == null) {
       Map params = new HashMap();
       params.put("user_id", id);
       params.put("album_default", Boolean.valueOf(true));
       List list = this.albumDao
         .query(
         "select obj from Album obj where obj.user.id=:user_id and obj.album_default=:album_default", 
         params, -1, -1);
       if (list.size() > 0) {
         return (Album)list.get(0);
       }
       return null;
     }
     Map params = new HashMap();
     params.put("user_id", user.getParent().getId());
     params.put("album_default", Boolean.valueOf(true));
     List list = this.albumDao
       .query(
       "select obj from Album obj where obj.user.id=:user_id and obj.album_default=:album_default", 
       params, -1, -1);
     if (list.size() > 0) {
       return (Album)list.get(0);
     }
     return null;
   }
 }



 
 