 package com.shopping.foundation.domain;
 
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.OneToOne;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_partner")
 public class Partner extends IdEntity
 {
   //序列
   private int sequence;
   private String url;
   //标题
   private String title;
 
   //图片附件
   @OneToOne(fetch=FetchType.LAZY)
   private Accessory image;
 
   public int getSequence()
   {
     return this.sequence;
   }
 
   public void setSequence(int sequence) {
     this.sequence = sequence;
   }
 
   public String getUrl() {
     return this.url;
   }
 
   public void setUrl(String url) {
     this.url = url;
   }
 
   public String getTitle() {
     return this.title;
   }
 
   public void setTitle(String title) {
     this.title = title;
   }
 
   public Accessory getImage() {
     return this.image;
   }
 
   public void setImage(Accessory image) {
     this.image = image;
   }
 }



 
 