 package com.shopping.foundation.domain;
 
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Lob;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_syslog")
 public class SysLog extends IdEntity
 {
	 
   private String title;
   private int type;
 
   @Lob
   @Column(columnDefinition="LongText")
   private String content;
 
   @ManyToOne(fetch=FetchType.LAZY)
   private User user;
   private String ip;
 
   public String getTitle()
   {
     return this.title;
   }
 
   public void setTitle(String title) {
     this.title = title;
   }
 
   public String getContent() {
     return this.content;
   }
 
   public void setContent(String content) {
     this.content = content;
   }
 
   public User getUser() {
     return this.user;
   }
 
   public void setUser(User user) {
     this.user = user;
   }
 
   public int getType() {
     return this.type;
   }
 
   public void setType(int type) {
     this.type = type;
   }
 
   public String getIp() {
     return this.ip;
   }
 
   public void setIp(String ip) {
     this.ip = ip;
   }
 }



 
 