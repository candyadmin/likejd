 package com.shopping.foundation.domain;
 
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_user_attention")
 public class SnsAttention extends IdEntity
 {
 
   //来源用户
   @ManyToOne(fetch=FetchType.LAZY)
   private User fromUser;
 
   //目标用户
   @ManyToOne(fetch=FetchType.LAZY)
   private User toUser;
 
   public User getFromUser()
   {
     return this.fromUser;
   }
 
   public void setFromUser(User fromUser) {
     this.fromUser = fromUser;
   }
 
   public User getToUser() {
     return this.toUser;
   }
 
   public void setToUser(User toUser) {
     this.toUser = toUser;
   }
 }



 
 