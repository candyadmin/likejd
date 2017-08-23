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
 @Table(name="shopping_report_type")
 public class ReportType extends IdEntity
 {
   //名称
   private String name;
 
   //内容
   @Lob
   @Column(columnDefinition="LongText")
   private String content;
 
   public String getName()
   {
     return this.name;
   }
 
   public void setName(String name) {
     this.name = name;
   }
 
   public String getContent() {
     return this.content;
   }
 
   public void setContent(String content) {
     this.content = content;
   }
 }



 
 