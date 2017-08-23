 package com.shopping.foundation.domain;
 
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 import javax.persistence.Entity;
 import javax.persistence.OneToMany;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_group")
 public class Group extends IdEntity
 {
   //分组名称
   private String group_name;
   //开始时间
   private Date beginTime;
   //结束时间
   private Date endTime;
   //加入结束时间
   private Date joinEndTime;
   //状态
   private int status;
 
   //货物集合
   @OneToMany(mappedBy="group")
   private List<Goods> goods_list = new ArrayList();
 
   //分组货物集合
   @OneToMany(mappedBy="group")
   private List<GroupGoods> gg_list = new ArrayList();
 
   public List<Goods> getGoods_list() {
     return this.goods_list;
   }
 
   public void setGoods_list(List<Goods> goods_list) {
     this.goods_list = goods_list;
   }
 
   public List<GroupGoods> getGg_list() {
     return this.gg_list;
   }
 
   public void setGg_list(List<GroupGoods> gg_list) {
     this.gg_list = gg_list;
   }
 
   public int getStatus() {
     return this.status;
   }
 
   public void setStatus(int status) {
     this.status = status;
   }
 
   public String getGroup_name() {
     return this.group_name;
   }
 
   public void setGroup_name(String group_name) {
     this.group_name = group_name;
   }
 
   public Date getBeginTime() {
     return this.beginTime;
   }
 
   public void setBeginTime(Date beginTime) {
     this.beginTime = beginTime;
   }
 
   public Date getEndTime() {
     return this.endTime;
   }
 
   public void setEndTime(Date endTime) {
     this.endTime = endTime;
   }
 
   public Date getJoinEndTime() {
     return this.joinEndTime;
   }
 
   public void setJoinEndTime(Date joinEndTime) {
     this.joinEndTime = joinEndTime;
   }
 }



 
 