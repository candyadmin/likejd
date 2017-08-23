 package com.shopping.foundation.domain;
 
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_storegrade_log")
 public class StoreGradeLog extends IdEntity
 {
   //店铺
   @ManyToOne(fetch=FetchType.LAZY)
   private Store store;
   //店铺等级状态
   private int store_grade_status;
   //店铺等级信息
   private String store_grade_info;
   //编辑记录
   private boolean log_edit;
 
   public boolean isLog_edit()
   {
     return this.log_edit;
   }
 
   public void setLog_edit(boolean log_edit) {
     this.log_edit = log_edit;
   }
 
   public Store getStore() {
     return this.store;
   }
 
   public void setStore(Store store) {
     this.store = store;
   }
 
   public int getStore_grade_status() {
     return this.store_grade_status;
   }
 
   public void setStore_grade_status(int store_grade_status) {
     this.store_grade_status = store_grade_status;
   }
 
   public String getStore_grade_info() {
     return this.store_grade_info;
   }
 
   public void setStore_grade_info(String store_grade_info) {
     this.store_grade_info = store_grade_info;
   }
 }



 
 