 package com.shopping.foundation.domain;
 
 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import javax.persistence.Table;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_integral_goodsorder")
 public class IntegralGoodsOrder extends IdEntity
 {
   private String igo_order_sn;
   //地址
   @ManyToOne(fetch=FetchType.LAZY)
   private Address igo_addr;
   //用户 
   @ManyToOne(fetch=FetchType.LAZY)
   private User igo_user;
   //商品运输集合
   @OneToMany(mappedBy="order", cascade={javax.persistence.CascadeType.REMOVE, javax.persistence.CascadeType.PERSIST})
   private List<IntegralGoodsCart> igo_gcs = new ArrayList();
   //运输费用
   @Column(precision=12, scale=2)
   private BigDecimal igo_trans_fee;
   //订单状态
   private int igo_status;
   private int igo_total_integral;

   //订单信息
   @Column(columnDefinition="LongText")
   private String igo_msg;
   //订单付款
   private String igo_payment;
   
   //订单支付信息
   @Column(columnDefinition="LongText")
   private String igo_pay_msg;
   //订单支付时间
   private Date igo_pay_time;
   private String igo_ship_code;
   
   //订单运送时间
   @Temporal(TemporalType.DATE)
   private Date igo_ship_time;
   //订单运送信息
   @Column(columnDefinition="LongText")
   private String igo_ship_content;
 
   public String getIgo_ship_content() { return this.igo_ship_content; }
 
   public void setIgo_ship_content(String igo_ship_content)
   {
     this.igo_ship_content = igo_ship_content;
   }
 
   public String getIgo_ship_code() {
     return this.igo_ship_code;
   }
 
   public void setIgo_ship_code(String igo_ship_code) {
     this.igo_ship_code = igo_ship_code;
   }
 
   public Date getIgo_ship_time() {
     return this.igo_ship_time;
   }
 
   public void setIgo_ship_time(Date igo_ship_time) {
     this.igo_ship_time = igo_ship_time;
   }
 
   public Date getIgo_pay_time() {
     return this.igo_pay_time;
   }
 
   public void setIgo_pay_time(Date igo_pay_time) {
     this.igo_pay_time = igo_pay_time;
   }
 
   public String getIgo_order_sn() {
     return this.igo_order_sn;
   }
 
   public void setIgo_order_sn(String igo_order_sn) {
     this.igo_order_sn = igo_order_sn;
   }
 
   public Address getIgo_addr() {
     return this.igo_addr;
   }
 
   public void setIgo_addr(Address igo_addr) {
     this.igo_addr = igo_addr;
   }
 
   public User getIgo_user() {
     return this.igo_user;
   }
 
   public void setIgo_user(User igo_user) {
     this.igo_user = igo_user;
   }
 
   public List<IntegralGoodsCart> getIgo_gcs() {
     return this.igo_gcs;
   }
 
   public void setIgo_gcs(List<IntegralGoodsCart> igo_gcs) {
     this.igo_gcs = igo_gcs;
   }
 
   public BigDecimal getIgo_trans_fee() {
     return this.igo_trans_fee;
   }
 
   public void setIgo_trans_fee(BigDecimal igo_trans_fee) {
     this.igo_trans_fee = igo_trans_fee;
   }
 
   public int getIgo_status() {
     return this.igo_status;
   }
 
   public void setIgo_status(int igo_status) {
     this.igo_status = igo_status;
   }
 
   public int getIgo_total_integral() {
     return this.igo_total_integral;
   }
 
   public void setIgo_total_integral(int igo_total_integral) {
     this.igo_total_integral = igo_total_integral;
   }
 
   public String getIgo_msg() {
     return this.igo_msg;
   }
 
   public void setIgo_msg(String igo_msg) {
     this.igo_msg = igo_msg;
   }
 
   public String getIgo_payment() {
     return this.igo_payment;
   }
 
   public void setIgo_payment(String igo_payment) {
     this.igo_payment = igo_payment;
   }
 
   public String getIgo_pay_msg() {
     return this.igo_pay_msg;
   }
 
   public void setIgo_pay_msg(String igo_pay_msg) {
     this.igo_pay_msg = igo_pay_msg;
   }
 }



 
 