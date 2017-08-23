 package com.shopping.foundation.service.impl;
 
 import com.shopping.core.dao.IGenericDAO;
 import com.shopping.core.query.GenericPageList;
 import com.shopping.core.query.PageObject;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.query.support.IQueryObject;
 import com.shopping.foundation.domain.Document;
 import com.shopping.foundation.service.IDocumentService;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.annotation.Resource;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;
 
 @Service
 @Transactional
 public class DocumentServiceImpl
   implements IDocumentService
 {
 
   @Resource(name="documentDAO")
   private IGenericDAO<Document> documentDao;
 
   public boolean save(Document document)
   {
     try
     {
       this.documentDao.save(document);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public Document getObjById(Long id)
   {
     Document document = (Document)this.documentDao.get(id);
     if (document != null) {
       return document;
     }
     return null;
   }
 
   public boolean delete(Long id) {
     try {
       this.documentDao.remove(id);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public boolean batchDelete(List<Serializable> documentIds)
   {
     for (Serializable id : documentIds) {
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
     GenericPageList pList = new GenericPageList(Document.class, query, 
       params, this.documentDao);
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
 
   public boolean update(Document document) {
     try {
       this.documentDao.update(document);
       return true;
     } catch (Exception e) {
       e.printStackTrace();
     }return false;
   }
 
   public List<Document> query(String query, Map params, int begin, int max) {
     return this.documentDao.query(query, params, begin, max);
   }
 
   public Document getObjByProperty(String propertyName, Object value)
   {
     return (Document)this.documentDao.getBy(propertyName, value);
   }
 }



 
 