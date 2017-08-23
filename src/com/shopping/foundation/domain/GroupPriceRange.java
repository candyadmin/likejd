 package com.shopping.foundation.domain;
 
 import javax.persistence.Entity;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_group_price_range")
 public class GroupPriceRange extends IdEntity
 {
   //价格等级名称	 
   private String gpr_name;
   //开始
   private int gpr_begin;
   //结束
   private int gpr_end;
 
   public String getGpr_name()
   {
     return this.gpr_name;
   }
 
   public void setGpr_name(String gpr_name) {
     this.gpr_name = gpr_name;
   }
 
   public int getGpr_begin() {
     return this.gpr_begin;
   }
 
   public void setGpr_begin(int gpr_begin) {
     this.gpr_begin = gpr_begin;
   }
 
   public int getGpr_end() {
     return this.gpr_end;
   }
 
   public void setGpr_end(int gpr_end) {
     this.gpr_end = gpr_end;
   }
 }



 
 