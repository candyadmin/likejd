 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.Chatting;
 import com.shopping.foundation.service.IChattingService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class ChattingServiceImpl
   implements IChattingService
 {
 
   @Resource(name="chattingDAO")
   private IGenericDAO<Chatting> chattingDao;
 
   public boolean save(Chatting chatting)
   {
     try
     {
       this.chattingDao.save(chatting);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public Chatting getObjById(Long id)
   {
     Chatting chatting = (Chatting)this.chattingDao.get(id);
     if (chatting != null) {
       return chatting;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.chattingDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> chattingIds)
   {
     for (Serializable id : chattingIds) {
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
     GenericPageList pList = new GenericPageList(Chatting.class, query, 
       params, this.chattingDao);
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
 
   public boolean update(Chatting chatting) {
     try {
       this.chattingDao.update(chatting);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<Chatting> query(String query, Map params, int begin, int max) {
     return this.chattingDao.query(query, params, begin, max);
   }
 }



 
 