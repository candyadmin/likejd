 package com.shopping.foundation.domain;
 
 import java.math.BigDecimal;
 import java.util.Date;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_store_stat")
 public class StoreStat extends IdEntity
 {
   //一周用户
   private int week_user;
   //一周商品
   private int week_goods;
   //一周店铺
   private int week_store;
   //一周订单
   private int week_order;
   //一周投诉
   private int week_complaint;
   //一周报道
   private int week_report;
   //所有用户
   private int all_user;
   //所有店铺
   private int all_store;
   //店铺更新
   private int store_update;
   //所有商品
   private int all_goods;
 
   //订单数量
   @Column(precision=12, scale=2)
   private BigDecimal order_amount;
   private Date next_time;
 
   public int getWeek_user()
   {
     return this.week_user;
   }
 
   public void setWeek_user(int week_user) {
     this.week_user = week_user;
   }
 
   public int getWeek_goods() {
     return this.week_goods;
   }
 
   public void setWeek_goods(int week_goods) {
     this.week_goods = week_goods;
   }
 
   public int getWeek_store() {
     return this.week_store;
   }
 
   public void setWeek_store(int week_store) {
     this.week_store = week_store;
   }
 
   public int getWeek_order() {
     return this.week_order;
   }
 
   public void setWeek_order(int week_order) {
     this.week_order = week_order;
   }
 
   public int getWeek_complaint() {
     return this.week_complaint;
   }
 
   public void setWeek_complaint(int week_complaint) {
     this.week_complaint = week_complaint;
   }
 
   public int getWeek_report() {
     return this.week_report;
   }
 
   public void setWeek_report(int week_report) {
     this.week_report = week_report;
   }
 
   public int getAll_user() {
     return this.all_user;
   }
 
   public void setAll_user(int all_user) {
     this.all_user = all_user;
   }
 
   public int getAll_store() {
     return this.all_store;
   }
 
   public void setAll_store(int all_store) {
     this.all_store = all_store;
   }
 
   public int getStore_update() {
     return this.store_update;
   }
 
   public void setStore_update(int store_update) {
     this.store_update = store_update;
   }
 
   public int getAll_goods() {
     return this.all_goods;
   }
 
   public void setAll_goods(int all_goods) {
     this.all_goods = all_goods;
   }
 
   public BigDecimal getOrder_amount() {
     return this.order_amount;
   }
 
   public void setOrder_amount(BigDecimal order_amount) {
     this.order_amount = order_amount;
   }
 
   public Date getNext_time() {
     return this.next_time;
   }
 
   public void setNext_time(Date next_time) {
     this.next_time = next_time;
   }
 }



 
 