 package com.shopping.foundation.domain;
 
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Lob;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_integrallog")
 public class IntegralLog extends IdEntity
 {
   //用户
   @ManyToOne(fetch=FetchType.LAZY)
   private User integral_user;
   
   //操作者
   @ManyToOne(fetch=FetchType.LAZY)
   private User operate_user;
   private int integral;
   //类型
   private String type;
 
   //内容
   @Lob
   @Column(columnDefinition="LongText")
   private String content;
 
   public String getContent()
   {
     return this.content;
   }
 
   public void setContent(String content) {
     this.content = content;
   }
 
   public User getIntegral_user() {
     return this.integral_user;
   }
 
   public void setIntegral_user(User integral_user) {
     this.integral_user = integral_user;
   }
 
   public User getOperate_user() {
     return this.operate_user;
   }
 
   public void setOperate_user(User operate_user) {
     this.operate_user = operate_user;
   }
 
   public int getIntegral() {
     return this.integral;
   }
 
   public void setIntegral(int integral) {
     this.integral = integral;
   }
 
   public String getType() {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 }



 
 