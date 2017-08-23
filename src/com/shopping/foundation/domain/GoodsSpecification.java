 package com.shopping.foundation.domain;
 
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.Entity;
 import javax.persistence.ManyToMany;
 import javax.persistence.OneToMany;
 import javax.persistence.OrderBy;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_goodsspecification")
 public class GoodsSpecification extends IdEntity
 {
   //名称
   private String name;
   //序列
   private int sequence;
   //类型
   private String type;
   //类型集合
   @ManyToMany(mappedBy="gss")
   private List<GoodsType> types = new ArrayList();
   //货物规格集合
   @OneToMany(mappedBy="spec")
   @OrderBy("sequence asc")
   private List<GoodsSpecProperty> properties = new ArrayList();
 
   public String getName() {
     return this.name;
   }
 
   public void setName(String name) {
     this.name = name;
   }
 
   public int getSequence() {
     return this.sequence;
   }
 
   public void setSequence(int sequence) {
     this.sequence = sequence;
   }
 
   public String getType() {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 
   public List<GoodsType> getTypes() {
     return this.types;
   }
 
   public void setTypes(List<GoodsType> types) {
     this.types = types;
   }
 
   public List<GoodsSpecProperty> getProperties() {
     return this.properties;
   }
 
   public void setProperties(List<GoodsSpecProperty> properties) {
     this.properties = properties;
   }
 }



 
 