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
 @Table(name="shopping_goods_returnlog")
 public class GoodsReturnLog extends IdEntity
 {
 
   @ManyToOne(fetch=FetchType.LAZY)
   private OrderForm of;
   
   //返回货物
   @ManyToOne(fetch=FetchType.LAZY)
   private GoodsReturn gr;
   //返回人
   @ManyToOne(fetch=FetchType.LAZY)
   private User return_user;
 
   public OrderForm getOf()
   {
     return this.of;
   }
 
   public void setOf(OrderForm of) {
     this.of = of;
   }
 
   public GoodsReturn getGr() {
     return this.gr;
   }
 
   public void setGr(GoodsReturn gr) {
     this.gr = gr;
   }
 
   public User getReturn_user() {
     return this.return_user;
   }
 
   public void setReturn_user(User return_user) {
     this.return_user = return_user;
   }
 }



 
 