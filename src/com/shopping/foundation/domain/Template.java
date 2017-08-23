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
 @Table(name="shopping_template")
 public class Template extends IdEntity
 {
   //信息
   private String info;
   //类型
   private String type;
   //标题
   private String title;
 
   //内容
   @Lob
   @Column(columnDefinition="LongText")
   private String content;
   //标记
   private String mark;
   private boolean open;
 
   public String getType()
   {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 
   public String getTitle() {
     return this.title;
   }
 
   public void setTitle(String title) {
     this.title = title;
   }
 
   public String getContent() {
     return this.content;
   }
 
   public void setContent(String content) {
     this.content = content;
   }
 
   public String getMark() {
     return this.mark;
   }
 
   public void setMark(String mark) {
     this.mark = mark;
   }
 
   public boolean isOpen() {
     return this.open;
   }
 
   public void setOpen(boolean open) {
     this.open = open;
   }
 
   public String getInfo() {
     return this.info;
   }
 
   public void setInfo(String info) {
     this.info = info;
   }
 }



 
 