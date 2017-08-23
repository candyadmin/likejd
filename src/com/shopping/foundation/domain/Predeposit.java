 package com.shopping.foundation.domain;
 
 import java.math.BigDecimal;
 import java.util.Date;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToOne;
 import javax.persistence.Table;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_predeposit")
 public class Predeposit extends IdEntity
 {
   //用户
   @ManyToOne(fetch=FetchType.LAZY)
   private User pd_user;
   
   //数量
   @Column(precision=12, scale=2)
   private BigDecimal pd_amount;
   private String pd_sn;
   //支付
   private String pd_payment;
   //汇款人
   private String pd_remittance_user;
   //汇款银行
   private String pd_remittance_bank;
 
   //汇款时间
   @Temporal(TemporalType.DATE)
   private Date pd_remittance_time;
 
   //汇款信息
   @Column(columnDefinition="LongText")
   private String pd_remittance_info;
 
   //管理员
   @ManyToOne(fetch=FetchType.LAZY)
   private User pd_admin;
 
   //管理信息
   @Column(columnDefinition="LongText")
   private String pd_admin_info;
   //状态
   private int pd_status;
   //支付状态
   private int pd_pay_status;
 
   //记录
   @OneToOne(fetch=FetchType.LAZY, mappedBy="predeposit", cascade={javax.persistence.CascadeType.REMOVE})
   private PredepositLog log;
 
   public int getPd_status()
   {
     return this.pd_status;
   }
 
   public void setPd_status(int pd_status) {
     this.pd_status = pd_status;
   }
 
   public int getPd_pay_status() {
     return this.pd_pay_status;
   }
 
   public void setPd_pay_status(int pd_pay_status) {
     this.pd_pay_status = pd_pay_status;
   }
 
   public User getPd_user() {
     return this.pd_user;
   }
 
   public void setPd_user(User pd_user) {
     this.pd_user = pd_user;
   }
 
   public BigDecimal getPd_amount() {
     return this.pd_amount;
   }
 
   public void setPd_amount(BigDecimal pd_amount) {
     this.pd_amount = pd_amount;
   }
 
   public String getPd_sn() {
     return this.pd_sn;
   }
 
   public void setPd_sn(String pd_sn) {
     this.pd_sn = pd_sn;
   }
 
   public String getPd_payment() {
     return this.pd_payment;
   }
 
   public void setPd_payment(String pd_payment) {
     this.pd_payment = pd_payment;
   }
 
   public String getPd_remittance_user() {
     return this.pd_remittance_user;
   }
 
   public void setPd_remittance_user(String pd_remittance_user) {
     this.pd_remittance_user = pd_remittance_user;
   }
 
   public String getPd_remittance_bank() {
     return this.pd_remittance_bank;
   }
 
   public void setPd_remittance_bank(String pd_remittance_bank) {
     this.pd_remittance_bank = pd_remittance_bank;
   }
 
   public Date getPd_remittance_time() {
     return this.pd_remittance_time;
   }
 
   public void setPd_remittance_time(Date pd_remittance_time) {
     this.pd_remittance_time = pd_remittance_time;
   }
 
   public String getPd_remittance_info() {
     return this.pd_remittance_info;
   }
 
   public void setPd_remittance_info(String pd_remittance_info) {
     this.pd_remittance_info = pd_remittance_info;
   }
 
   public User getPd_admin() {
     return this.pd_admin;
   }
 
   public void setPd_admin(User pd_admin) {
     this.pd_admin = pd_admin;
   }
 
   public String getPd_admin_info() {
     return this.pd_admin_info;
   }
 
   public void setPd_admin_info(String pd_admin_info) {
     this.pd_admin_info = pd_admin_info;
   }
 
   public PredepositLog getLog() {
     return this.log;
   }
 
   public void setLog(PredepositLog log) {
     this.log = log;
   }
 }



 
 