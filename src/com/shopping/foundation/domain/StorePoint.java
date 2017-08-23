 package com.shopping.foundation.domain;
 
 import java.math.BigDecimal;
 import java.util.Date;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.OneToOne;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_store_point")
 public class StorePoint extends IdEntity
 {
 
   //店铺
   @OneToOne(fetch=FetchType.LAZY)
   private Store store;
   private Date statTime;
 
   //店铺评价
   @Column(precision=4, scale=1)
   private BigDecimal store_evaluate1;
 
   //描述评价
   @Column(precision=4, scale=1)
   private BigDecimal description_evaluate;
 
   //服务评价
   @Column(precision=4, scale=1)
   private BigDecimal service_evaluate;
 
   //运送评价
   @Column(precision=4, scale=1)
   private BigDecimal ship_evaluate;
 
   //半年描述评价
   @Column(precision=4, scale=1)
   private BigDecimal description_evaluate_halfyear;
 
   //半年服务评价
   @Column(precision=4, scale=1)
   private BigDecimal service_evaluate_halfyear;
 
   //半年运送评价
   @Column(precision=4, scale=1)
   private BigDecimal ship_evaluate_halfyear;
   //半年描述评价5
   private int description_evaluate_halfyear_count5;
   //半年描述评价4
   private int description_evaluate_halfyear_count4;
   //半年描述评价3
   private int description_evaluate_halfyear_count3;
   //半年描述评价2
   private int description_evaluate_halfyear_count2;
   //半年描述评价1
   private int description_evaluate_halfyear_count1;
   //半年服务评价5
   private int service_evaluate_halfyear_count5;
   //半年描述评价4
   private int service_evaluate_halfyear_count4;
   //半年描述评价3
   private int service_evaluate_halfyear_count3;
   //半年描述评价2
   private int service_evaluate_halfyear_count2;
   //半年描述评价1
   private int service_evaluate_halfyear_count1;
   //半年运送评价5
   private int ship_evaluate_halfyear_count5;
   //半年运送评价4
   private int ship_evaluate_halfyear_count4;
   //半年运送评价3
   private int ship_evaluate_halfyear_count3;
   //半年运送评价2
   private int ship_evaluate_halfyear_count2;
   //半年运送评价1
   private int ship_evaluate_halfyear_count1;
 
   public BigDecimal getDescription_evaluate_halfyear()
   {
     return this.description_evaluate_halfyear;
   }
 
   public void setDescription_evaluate_halfyear(BigDecimal description_evaluate_halfyear)
   {
     this.description_evaluate_halfyear = description_evaluate_halfyear;
   }
 
   public BigDecimal getService_evaluate_halfyear() {
     return this.service_evaluate_halfyear;
   }
 
   public void setService_evaluate_halfyear(BigDecimal service_evaluate_halfyear)
   {
     this.service_evaluate_halfyear = service_evaluate_halfyear;
   }
 
   public BigDecimal getShip_evaluate_halfyear() {
     return this.ship_evaluate_halfyear;
   }
 
   public void setShip_evaluate_halfyear(BigDecimal ship_evaluate_halfyear) {
     this.ship_evaluate_halfyear = ship_evaluate_halfyear;
   }
 
   public int getDescription_evaluate_halfyear_count5() {
     return this.description_evaluate_halfyear_count5;
   }
 
   public void setDescription_evaluate_halfyear_count5(int description_evaluate_halfyear_count5)
   {
     this.description_evaluate_halfyear_count5 = description_evaluate_halfyear_count5;
   }
 
   public int getDescription_evaluate_halfyear_count4() {
     return this.description_evaluate_halfyear_count4;
   }
 
   public void setDescription_evaluate_halfyear_count4(int description_evaluate_halfyear_count4)
   {
     this.description_evaluate_halfyear_count4 = description_evaluate_halfyear_count4;
   }
 
   public int getDescription_evaluate_halfyear_count3() {
     return this.description_evaluate_halfyear_count3;
   }
 
   public void setDescription_evaluate_halfyear_count3(int description_evaluate_halfyear_count3)
   {
     this.description_evaluate_halfyear_count3 = description_evaluate_halfyear_count3;
   }
 
   public int getDescription_evaluate_halfyear_count2() {
     return this.description_evaluate_halfyear_count2;
   }
 
   public void setDescription_evaluate_halfyear_count2(int description_evaluate_halfyear_count2)
   {
     this.description_evaluate_halfyear_count2 = description_evaluate_halfyear_count2;
   }
 
   public int getDescription_evaluate_halfyear_count1() {
     return this.description_evaluate_halfyear_count1;
   }
 
   public void setDescription_evaluate_halfyear_count1(int description_evaluate_halfyear_count1)
   {
     this.description_evaluate_halfyear_count1 = description_evaluate_halfyear_count1;
   }
 
   public int getService_evaluate_halfyear_count5() {
     return this.service_evaluate_halfyear_count5;
   }
 
   public void setService_evaluate_halfyear_count5(int service_evaluate_halfyear_count5)
   {
     this.service_evaluate_halfyear_count5 = service_evaluate_halfyear_count5;
   }
 
   public int getService_evaluate_halfyear_count4() {
     return this.service_evaluate_halfyear_count4;
   }
 
   public void setService_evaluate_halfyear_count4(int service_evaluate_halfyear_count4)
   {
     this.service_evaluate_halfyear_count4 = service_evaluate_halfyear_count4;
   }
 
   public int getService_evaluate_halfyear_count3() {
     return this.service_evaluate_halfyear_count3;
   }
 
   public void setService_evaluate_halfyear_count3(int service_evaluate_halfyear_count3)
   {
     this.service_evaluate_halfyear_count3 = service_evaluate_halfyear_count3;
   }
 
   public int getService_evaluate_halfyear_count2() {
     return this.service_evaluate_halfyear_count2;
   }
 
   public void setService_evaluate_halfyear_count2(int service_evaluate_halfyear_count2)
   {
     this.service_evaluate_halfyear_count2 = service_evaluate_halfyear_count2;
   }
 
   public int getService_evaluate_halfyear_count1() {
     return this.service_evaluate_halfyear_count1;
   }
 
   public void setService_evaluate_halfyear_count1(int service_evaluate_halfyear_count1)
   {
     this.service_evaluate_halfyear_count1 = service_evaluate_halfyear_count1;
   }
 
   public int getShip_evaluate_halfyear_count5() {
     return this.ship_evaluate_halfyear_count5;
   }
 
   public void setShip_evaluate_halfyear_count5(int ship_evaluate_halfyear_count5)
   {
     this.ship_evaluate_halfyear_count5 = ship_evaluate_halfyear_count5;
   }
 
   public int getShip_evaluate_halfyear_count4() {
     return this.ship_evaluate_halfyear_count4;
   }
 
   public void setShip_evaluate_halfyear_count4(int ship_evaluate_halfyear_count4)
   {
     this.ship_evaluate_halfyear_count4 = ship_evaluate_halfyear_count4;
   }
 
   public int getShip_evaluate_halfyear_count3() {
     return this.ship_evaluate_halfyear_count3;
   }
 
   public void setShip_evaluate_halfyear_count3(int ship_evaluate_halfyear_count3)
   {
     this.ship_evaluate_halfyear_count3 = ship_evaluate_halfyear_count3;
   }
 
   public int getShip_evaluate_halfyear_count2() {
     return this.ship_evaluate_halfyear_count2;
   }
 
   public void setShip_evaluate_halfyear_count2(int ship_evaluate_halfyear_count2)
   {
     this.ship_evaluate_halfyear_count2 = ship_evaluate_halfyear_count2;
   }
 
   public int getShip_evaluate_halfyear_count1() {
     return this.ship_evaluate_halfyear_count1;
   }
 
   public void setShip_evaluate_halfyear_count1(int ship_evaluate_halfyear_count1)
   {
     this.ship_evaluate_halfyear_count1 = ship_evaluate_halfyear_count1;
   }
 
   public Store getStore() {
     return this.store;
   }
 
   public void setStore(Store store) {
     this.store = store;
   }
 
   public BigDecimal getStore_evaluate1() {
     return this.store_evaluate1;
   }
 
   public void setStore_evaluate1(BigDecimal store_evaluate1) {
     this.store_evaluate1 = store_evaluate1;
   }
 
   public BigDecimal getDescription_evaluate() {
     return this.description_evaluate;
   }
 
   public void setDescription_evaluate(BigDecimal description_evaluate) {
     this.description_evaluate = description_evaluate;
   }
 
   public BigDecimal getService_evaluate() {
     return this.service_evaluate;
   }
 
   public void setService_evaluate(BigDecimal service_evaluate) {
     this.service_evaluate = service_evaluate;
   }
 
   public BigDecimal getShip_evaluate() {
     return this.ship_evaluate;
   }
 
   public void setShip_evaluate(BigDecimal ship_evaluate) {
     this.ship_evaluate = ship_evaluate;
   }
 
   public Date getStatTime() {
     return this.statTime;
   }
 
   public void setStatTime(Date statTime) {
     this.statTime = statTime;
   }
 }



 
 