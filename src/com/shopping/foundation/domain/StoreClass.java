 package com.shopping.foundation.domain;
 
 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_storeclass")
 public class StoreClass extends IdEntity
 {
   //类型名称
   private String className;
   //序列
   private int sequence;
 
   //商店父类型
   @ManyToOne(fetch=FetchType.LAZY)
   private StoreClass parent;
 
   //商店子类型
   @OneToMany(mappedBy="parent")
   private List<StoreClass> childs = new ArrayList();
   //水平
   private int level;
 
   //描述评价
   @Column(precision=4, scale=1)
   private BigDecimal description_evaluate;
 
   //服务评价
   @Column(precision=4, scale=1)
   private BigDecimal service_evaluate;
 
   //运输评价
   @Column(precision=4, scale=1)
   private BigDecimal ship_evaluate;
 
   public BigDecimal getDescription_evaluate()
   {
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
 
   public String getClassName() {
     return this.className;
   }
 
   public void setClassName(String className) {
     this.className = className;
   }
 
   public int getSequence() {
     return this.sequence;
   }
 
   public void setSequence(int sequence) {
     this.sequence = sequence;
   }
 
   public StoreClass getParent() {
     return this.parent;
   }
 
   public void setParent(StoreClass parent) {
     this.parent = parent;
   }
 
   public List<StoreClass> getChilds() {
     return this.childs;
   }
 
   public void setChilds(List<StoreClass> childs) {
     this.childs = childs;
   }
 
   public int getLevel() {
     return this.level;
   }
 
   public void setLevel(int level) {
     this.level = level;
   }
 }



 
 