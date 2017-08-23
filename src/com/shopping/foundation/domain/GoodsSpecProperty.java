 package com.shopping.foundation.domain;
 
 import javax.persistence.Column;
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
 @Table(name="shopping_goodsspecproperty")
 public class GoodsSpecProperty extends IdEntity
 {
   //序列
   private int sequence;
   //值
   @Column(columnDefinition="LongText")
   private String value;
   //规格图
   @OneToOne(fetch=FetchType.LAZY, cascade={javax.persistence.CascadeType.REMOVE})
   private Accessory specImage;
   //货物规格
   @ManyToOne(fetch=FetchType.LAZY)
   private GoodsSpecification spec;
 
   public int getSequence()
   {
     return this.sequence;
   }
 
   public void setSequence(int sequence) {
     this.sequence = sequence;
   }
 
   public String getValue() {
     return this.value;
   }
 
   public void setValue(String value) {
     this.value = value;
   }
 
   public Accessory getSpecImage() {
     return this.specImage;
   }
 
   public void setSpecImage(Accessory specImage) {
     this.specImage = specImage;
   }
 
   public GoodsSpecification getSpec() {
     return this.spec;
   }
 
   public void setSpec(GoodsSpecification spec) {
     this.spec = spec;
   }
 }



 
 