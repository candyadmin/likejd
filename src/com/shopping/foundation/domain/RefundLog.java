 package com.shopping.foundation.domain;
 
 import java.math.BigDecimal;
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
 @Table(name="shopping_refund_log")
 public class RefundLog extends IdEntity
 {
   //退换ID
   private String refund_id;
 
   //订单表单
   @ManyToOne(fetch=FetchType.LAZY)
   private OrderForm of;
   //退还记录
   private String refund_log;
   //退还类型
   private String refund_type;
 
   //退还金额
   @Column(precision=12, scale=2)
   private BigDecimal refund;

   //退还人
   @ManyToOne(fetch=FetchType.LAZY)
   private User refund_user;
 
   @Lob
   @Column(columnDefinition="LongText")
   public OrderForm getOf()
   {
     return this.of;
   }
 
   public void setOf(OrderForm of) {
     this.of = of;
   }
 
   public String getRefund_log() {
     return this.refund_log;
   }
 
   public void setRefund_log(String refund_log) {
     this.refund_log = refund_log;
   }
 
   public BigDecimal getRefund() {
     return this.refund;
   }
 
   public void setRefund(BigDecimal refund) {
     this.refund = refund;
   }
 
   public User getRefund_user() {
     return this.refund_user;
   }
 
   public void setRefund_user(User refund_user) {
     this.refund_user = refund_user;
   }
 
   public String getRefund_type() {
     return this.refund_type;
   }
 
   public void setRefund_type(String refund_type) {
     this.refund_type = refund_type;
   }
 
   public String getRefund_id() {
     return this.refund_id;
   }
 
   public void setRefund_id(String refund_id) {
     this.refund_id = refund_id;
   }
 }



 
 