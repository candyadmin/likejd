 package com.shopping.foundation.domain;
 
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import javax.persistence.OrderBy;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_group_area")
 public class GroupArea extends IdEntity
 {
   //分组地区名称
   private String ga_name;
   //分组地区序列
   private int ga_sequence;
   
   //分组父地区
   @ManyToOne(fetch=FetchType.LAZY)
   private GroupArea parent;
   
   //分组子地区
   @OneToMany(mappedBy="parent", cascade={javax.persistence.CascadeType.REMOVE})
   @OrderBy("ga_sequence asc")
   private List<GroupArea> childs = new ArrayList();
   //分组地区水平
   private int ga_level;
 
   public int getGa_level()
   {
     return this.ga_level;
   }
 
   public void setGa_level(int ga_level) {
     this.ga_level = ga_level;
   }
 
   public String getGa_name() {
     return this.ga_name;
   }
 
   public void setGa_name(String ga_name) {
     this.ga_name = ga_name;
   }
 
   public int getGa_sequence() {
     return this.ga_sequence;
   }
 
   public void setGa_sequence(int ga_sequence) {
     this.ga_sequence = ga_sequence;
   }
 
   public GroupArea getParent() {
     return this.parent;
   }
 
   public void setParent(GroupArea parent) {
     this.parent = parent;
   }
 
   public List<GroupArea> getChilds() {
     return this.childs;
   }
 
   public void setChilds(List<GroupArea> childs) {
     this.childs = childs;
   }
 }



 
 