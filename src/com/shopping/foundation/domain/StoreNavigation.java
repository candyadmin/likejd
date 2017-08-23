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
 @Table(name="shopping_store_nav")
 public class StoreNavigation extends IdEntity
 {
   //标题
   private String title;
   private String url;
   //序列
   private String sequence;
   private String win_type;
   //是否展示
   private boolean display;
 
   //店铺
   @ManyToOne(fetch=FetchType.LAZY)
   private Store store;
 
   //内容
   @Lob
   @Column(columnDefinition="LongText")
   private String content;
 
   public String getTitle()
   {
     return this.title;
   }
 
   public void setTitle(String title) {
     this.title = title;
   }
 
   public String getUrl() {
     return this.url;
   }
 
   public void setUrl(String url) {
     this.url = url;
   }
 
   public String getSequence() {
     return this.sequence;
   }
 
   public void setSequence(String sequence) {
     this.sequence = sequence;
   }
 
   public String getWin_type() {
     return this.win_type;
   }
 
   public void setWin_type(String win_type) {
     this.win_type = win_type;
   }
 
   public boolean isDisplay() {
     return this.display;
   }
 
   public void setDisplay(boolean display) {
     this.display = display;
   }
 
   public String getContent() {
     return this.content;
   }
 
   public void setContent(String content) {
     this.content = content;
   }
 
   public Store getStore() {
     return this.store;
   }
 
   public void setStore(Store store) {
     this.store = store;
   }
 }



 
 