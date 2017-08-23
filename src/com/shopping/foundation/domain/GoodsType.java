 package com.shopping.foundation.domain;
 
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.Entity;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
 import javax.persistence.OneToMany;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_goodstype")
 public class GoodsType extends IdEntity
 {
   //名称	 
   private String name;
   //序列
   private int sequence;
   
   //商品规格集合
   @ManyToMany
   @JoinTable(name="shopping_goodstype_spec", joinColumns={@javax.persistence.JoinColumn(name="type_id")}, inverseJoinColumns={@javax.persistence.JoinColumn(name="spec_id")})
   private List<GoodsSpecification> gss = new ArrayList();
   
   //商品品牌集合
   @ManyToMany
   @JoinTable(name="shopping_goodstype_brand", joinColumns={@javax.persistence.JoinColumn(name="type_id")}, inverseJoinColumns={@javax.persistence.JoinColumn(name="brand_id")})
   private List<GoodsBrand> gbs = new ArrayList();
 
   //商品类型属性集合
   @OneToMany(mappedBy="goodsType", cascade={javax.persistence.CascadeType.REMOVE})
   private List<GoodsTypeProperty> properties = new ArrayList();
 
   //商品类型
   @OneToMany(mappedBy="goodsType")
   private List<GoodsClass> gcs = new ArrayList();
 
   public List<GoodsClass> getGcs() {
     return this.gcs;
   }
 
   public void setGcs(List<GoodsClass> gcs) {
     this.gcs = gcs;
   }
 
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
 
   public List<GoodsSpecification> getGss() {
     return this.gss;
   }
 
   public void setGss(List<GoodsSpecification> gss) {
     this.gss = gss;
   }
 
   public List<GoodsBrand> getGbs() {
     return this.gbs;
   }
 
   public void setGbs(List<GoodsBrand> gbs) {
     this.gbs = gbs;
   }
 
   public List<GoodsTypeProperty> getProperties() {
     return this.properties;
   }
 
   public void setProperties(List<GoodsTypeProperty> properties) {
     this.properties = properties;
   }
 }



 
 