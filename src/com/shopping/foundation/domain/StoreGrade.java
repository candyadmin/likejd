 package com.shopping.foundation.domain;
 
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.Lob;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_storegrade")
 public class StoreGrade extends IdEntity
 {
   //等级名称
   private String gradeName;
   private boolean sysGrade;
   //是否审计
   private boolean audit;
 
   //商品数量
   @Column(columnDefinition="int default 50")
   private int goodsCount;
 
   //序列
   @Column(columnDefinition="int default 0")
   private int sequence;
   private float spaceSize;
 
   //内容
   @Lob
   @Column(columnDefinition="LongText")
   private String content;
   //价格
   private String price;
   //作用
   private String add_funciton;
   //模板
   private String templates;
 
   //等级水平
   @Column(columnDefinition="int default 0")
   private int gradeLevel;
 
   //数量
   @Column(columnDefinition="int default 0")
   private int acount_num;
 
   public int getAcount_num()
   {
     return this.acount_num;
   }
 
   public void setAcount_num(int acount_num) {
     this.acount_num = acount_num;
   }
 
   public String getGradeName() {
     return this.gradeName;
   }
 
   public void setGradeName(String gradeName) {
     this.gradeName = gradeName;
   }
 
   public boolean isAudit() {
     return this.audit;
   }
 
   public void setAudit(boolean audit) {
     this.audit = audit;
   }
 
   public int getGoodsCount() {
     return this.goodsCount;
   }
 
   public void setGoodsCount(int goodsCount) {
     this.goodsCount = goodsCount;
   }
 
   public String getContent() {
     return this.content;
   }
 
   public void setContent(String content) {
     this.content = content;
   }
 
   public String getPrice() {
     return this.price;
   }
 
   public void setPrice(String price) {
     this.price = price;
   }
 
   public String getAdd_funciton() {
     return this.add_funciton;
   }
 
   public void setAdd_funciton(String add_funciton) {
     this.add_funciton = add_funciton;
   }
 
   public String getTemplates() {
     return this.templates;
   }
 
   public void setTemplates(String templates) {
     this.templates = templates;
   }
 
   public boolean isSysGrade() {
     return this.sysGrade;
   }
 
   public void setSysGrade(boolean sysGrade) {
     this.sysGrade = sysGrade;
   }
 
   public int getSequence() {
     return this.sequence;
   }
 
   public void setSequence(int sequence) {
     this.sequence = sequence;
   }
 
   public float getSpaceSize() {
     return this.spaceSize;
   }
 
   public void setSpaceSize(float spaceSize) {
     this.spaceSize = spaceSize;
   }
 
   public int getGradeLevel() {
     return this.gradeLevel;
   }
 
   public void setGradeLevel(int gradeLevel) {
     this.gradeLevel = gradeLevel;
   }
 }



 
 