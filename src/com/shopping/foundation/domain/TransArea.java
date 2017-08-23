 package com.shopping.foundation.domain;
 
 import java.util.ArrayList;
 import java.util.List;
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
 @Table(name="shopping_trans_area")
 public class TransArea extends IdEntity
 {
   //地区名称
   private String areaName;
 
   //运送子地区
   @OneToMany(mappedBy="parent", cascade={javax.persistence.CascadeType.REMOVE})
   private List<TransArea> childs = new ArrayList();
 
   //运送父地区
   @ManyToOne(fetch=FetchType.LAZY)
   private TransArea parent;
   //序列
   private int sequence;
   //水平
   private int level;
 
   public String getAreaName() { return this.areaName; }
 
   public void setAreaName(String areaName)
   {
     this.areaName = areaName;
   }
 
   public List<TransArea> getChilds() {
     return this.childs;
   }
 
   public void setChilds(List<TransArea> childs) {
     this.childs = childs;
   }
 
   public TransArea getParent() {
     return this.parent;
   }
 
   public void setParent(TransArea parent) {
     this.parent = parent;
   }
 
   public int getSequence() {
     return this.sequence;
   }
 
   public void setSequence(int sequence) {
     this.sequence = sequence;
   }
 
   public int getLevel() {
     return this.level;
   }
 
   public void setLevel(int level) {
     this.level = level;
   }
 }



 
 