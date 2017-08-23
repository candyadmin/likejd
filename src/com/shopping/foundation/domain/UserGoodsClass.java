 package com.shopping.foundation.domain;
 
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.ManyToMany;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_usergoodsclass")
 public class UserGoodsClass extends IdEntity
 {
   //类型名称
   private String className;
   //序列
   private int sequence;
   //是否展示
   private boolean display;
   //水平
   private int level;
 
   //用户父类型
   @ManyToOne(fetch=FetchType.LAZY)
   private UserGoodsClass parent;
 
   //用户自类型
   @OneToMany(mappedBy="parent", cascade={javax.persistence.CascadeType.REMOVE})
   private List<UserGoodsClass> childs = new ArrayList();
 
   //商品列表
   @ManyToMany(mappedBy="goods_ugcs")
   private List<Goods> goods_list = new ArrayList();
 
   //用户
   @ManyToOne(fetch=FetchType.LAZY)
   private User user;
 
   public List<Goods> getGoods_list() { return this.goods_list; }
 
   public void setGoods_list(List<Goods> goods_list)
   {
     this.goods_list = goods_list;
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
 
   public boolean isDisplay() {
     return this.display;
   }
 
   public void setDisplay(boolean display) {
     this.display = display;
   }
 
   public int getLevel() {
     return this.level;
   }
 
   public void setLevel(int level) {
     this.level = level;
   }
 
   public UserGoodsClass getParent() {
     return this.parent;
   }
 
   public void setParent(UserGoodsClass parent) {
     this.parent = parent;
   }
 
   public List<UserGoodsClass> getChilds() {
     return this.childs;
   }
 
   public void setChilds(List<UserGoodsClass> childs) {
     this.childs = childs;
   }
 
   public User getUser() {
     return this.user;
   }
 
   public void setUser(User user) {
     this.user = user;
   }
 }



 
 