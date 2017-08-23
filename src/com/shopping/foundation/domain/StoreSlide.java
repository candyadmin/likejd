 package com.shopping.foundation.domain;
 
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToOne;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_store_slide")
 public class StoreSlide extends IdEntity
 {
   private String url;
 
   //附件
   @OneToOne(fetch=FetchType.LAZY)
   private Accessory acc;
 
   //店铺
   @ManyToOne(fetch=FetchType.LAZY)
   private Store store;
 
   public Store getStore()
   {
     return this.store;
   }
 
   public void setStore(Store store) {
     this.store = store;
   }
 
   public String getUrl() {
     return this.url;
   }
 
   public void setUrl(String url) {
     this.url = url;
   }
 
   public Accessory getAcc() {
     return this.acc;
   }
 
   public void setAcc(Accessory acc) {
     this.acc = acc;
   }
 }



 
 