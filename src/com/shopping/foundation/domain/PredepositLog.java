 package com.shopping.foundation.domain;
 
 import java.math.BigDecimal;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToOne;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_predeposit_Log")
 public class PredepositLog extends IdEntity
 {
 
   @ManyToOne(fetch=FetchType.LAZY)
   private User pd_log_user;
 
   //数量
   @Column(precision=12, scale=2)
   private BigDecimal pd_log_amount;
   //类型
   private String pd_type;
   private String pd_op_type;
 
   //管理者记录
   @ManyToOne(fetch=FetchType.LAZY)
   private User pd_log_admin;
 
   //信息记录
   @Column(columnDefinition="LongText")
   private String pd_log_info;
 
   @OneToOne(fetch=FetchType.LAZY)
   private Predeposit predeposit;
 
   public Predeposit getPredeposit()
   {
     return this.predeposit;
   }
 
   public void setPredeposit(Predeposit predeposit) {
     this.predeposit = predeposit;
   }
 
   public User getPd_log_user() {
     return this.pd_log_user;
   }
 
   public void setPd_log_user(User pd_log_user) {
     this.pd_log_user = pd_log_user;
   }
 
   public BigDecimal getPd_log_amount() {
     return this.pd_log_amount;
   }
 
   public void setPd_log_amount(BigDecimal pd_log_amount) {
     this.pd_log_amount = pd_log_amount;
   }
 
   public String getPd_type() {
     return this.pd_type;
   }
 
   public void setPd_type(String pd_type) {
     this.pd_type = pd_type;
   }
 
   public String getPd_op_type() {
     return this.pd_op_type;
   }
 
   public void setPd_op_type(String pd_op_type) {
     this.pd_op_type = pd_op_type;
   }
 
   public User getPd_log_admin() {
     return this.pd_log_admin;
   }
 
   public void setPd_log_admin(User pd_log_admin) {
     this.pd_log_admin = pd_log_admin;
   }
 
   public String getPd_log_info() {
     return this.pd_log_info;
   }
 
   public void setPd_log_info(String pd_log_info) {
     this.pd_log_info = pd_log_info;
   }
 }



 
 