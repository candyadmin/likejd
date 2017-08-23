 package com.shopping.foundation.domain;
 
 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Lob;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import javax.persistence.OneToOne;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.annotation.Lock;
import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_orderform")
 public class OrderForm extends IdEntity
 {
   //订单id
   private String order_id;
   private String out_order_id;
   //订单类型
   private String order_type;
   
   //商品运送集合
   @OneToMany(mappedBy="of")
   List<GoodsCart> gcs = new ArrayList();
   
   //总价
   @Column(precision=12, scale=2)
   private BigDecimal totalPrice;
   
   //商品量
   @Column(precision=12, scale=2)
   private BigDecimal goods_amount;
   
   //信息
   @Lob
   @Column(columnDefinition="LongText")
   private String msg;
 
   //支付
   @ManyToOne(fetch=FetchType.LAZY)
   private Payment payment;
   //运送
   private String transport;
   private String shipCode;
   private String return_shipCode;
   private Date return_shipTime;
 
   @Lob
   @Column(columnDefinition="LongText")
   private String return_content;
 
   @ManyToOne(fetch=FetchType.LAZY)
   private ExpressCompany ec;
 
   @ManyToOne(fetch=FetchType.LAZY)
   private ExpressCompany return_ec;
 
   @Column(precision=12, scale=2)
   private BigDecimal ship_price;
 
   @Lock
   private int order_status;
 
   @ManyToOne(fetch=FetchType.LAZY)
   private User user;
 
   @ManyToOne(fetch=FetchType.LAZY)
   private Store store;
   private Date payTime;
   private Date shipTime;
   private Date finishTime;
 
   @ManyToOne(fetch=FetchType.LAZY)
   private Address addr;
   private int invoiceType;
   private String invoice;
 
   @OneToMany(mappedBy="of", cascade={javax.persistence.CascadeType.REMOVE})
   private List<OrderFormLog> ofls = new ArrayList();
 
   @OneToMany(mappedBy="of", cascade={javax.persistence.CascadeType.REMOVE})
   private List<RefundLog> rls = new ArrayList();
 
   @Lob
   @Column(columnDefinition="LongText")
   private String pay_msg;
 
   @Column(precision=12, scale=2)
   private BigDecimal refund;
   private String refund_type;
 
   @Column(columnDefinition="bit default 0")
   private boolean auto_confirm_email;
 
   @Column(columnDefinition="bit default 0")
   private boolean auto_confirm_sms;
 
   @OneToMany(mappedBy="of", cascade={javax.persistence.CascadeType.REMOVE})
   private List<GoodsReturnLog> grls = new ArrayList();
 
   @OneToMany(mappedBy="of", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Evaluate> evas = new ArrayList();
 
   @OneToMany(mappedBy="of", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Complaint> complaints = new ArrayList();
 
   @OneToOne(fetch=FetchType.LAZY)
   private CouponInfo ci;
 
   @Column(columnDefinition="LongText")
   private String order_seller_intro;
 
   public String getOrder_type()
   {
     return this.order_type;
   }
 
   public void setOrder_type(String order_type) {
     this.order_type = order_type;
   }
 
   public String getReturn_content()
   {
     return this.return_content;
   }
 
   public void setReturn_content(String return_content) {
     this.return_content = return_content;
   }
 
   public Date getReturn_shipTime() {
     return this.return_shipTime;
   }
 
   public void setReturn_shipTime(Date return_shipTime) {
     this.return_shipTime = return_shipTime;
   }
 
   public String getReturn_shipCode() {
     return this.return_shipCode;
   }
 
   public void setReturn_shipCode(String return_shipCode) {
     this.return_shipCode = return_shipCode;
   }
 
   public ExpressCompany getReturn_ec() {
     return this.return_ec;
   }
 
   public void setReturn_ec(ExpressCompany return_ec) {
     this.return_ec = return_ec;
   }
 
   public CouponInfo getCi() {
     return this.ci;
   }
 
   public void setCi(CouponInfo ci) {
     this.ci = ci;
   }
 
   public List<Complaint> getComplaints() {
     return this.complaints;
   }
 
   public void setComplaints(List<Complaint> complaints) {
     this.complaints = complaints;
   }
 
   public List<Evaluate> getEvas() {
     return this.evas;
   }
 
   public void setEvas(List<Evaluate> evas) {
     this.evas = evas;
   }
 
   public List<GoodsReturnLog> getGrls() {
     return this.grls;
   }
 
   public void setGrls(List<GoodsReturnLog> grls) {
     this.grls = grls;
   }
 
   public BigDecimal getRefund() {
     return this.refund;
   }
 
   public void setRefund(BigDecimal refund) {
     this.refund = refund;
   }
 
   public String getRefund_type() {
     return this.refund_type;
   }
 
   public void setRefund_type(String refund_type) {
     this.refund_type = refund_type;
   }
 
   public String getOrder_id() {
     return this.order_id;
   }
 
   public void setOrder_id(String order_id) {
     this.order_id = order_id;
   }
 
   public BigDecimal getTotalPrice() {
     return this.totalPrice;
   }
 
   public void setTotalPrice(BigDecimal totalPrice) {
     this.totalPrice = totalPrice;
   }
 
   public BigDecimal getShip_price() {
     return this.ship_price;
   }
 
   public void setShip_price(BigDecimal ship_price) {
     this.ship_price = ship_price;
   }
 
   public int getOrder_status() {
     return this.order_status;
   }
 
   public void setOrder_status(int order_status) {
     this.order_status = order_status;
   }
 
   public String getMsg() {
     return this.msg;
   }
 
   public void setMsg(String msg) {
     this.msg = msg;
   }
 
   public Payment getPayment() {
     return this.payment;
   }
 
   public void setPayment(Payment payment) {
     this.payment = payment;
   }
 
   public User getUser() {
     return this.user;
   }
 
   public void setUser(User user) {
     this.user = user;
   }
 
   public Date getPayTime() {
     return this.payTime;
   }
 
   public void setPayTime(Date payTime) {
     this.payTime = payTime;
   }
 
   public List<GoodsCart> getGcs() {
     return this.gcs;
   }
 
   public void setGcs(List<GoodsCart> gcs) {
     this.gcs = gcs;
   }
 
   public Address getAddr() {
     return this.addr;
   }
 
   public void setAddr(Address addr) {
     this.addr = addr;
   }
 
   public String getShipCode() {
     return this.shipCode;
   }
 
   public void setShipCode(String shipCode) {
     this.shipCode = shipCode;
   }
 
   public Date getShipTime() {
     return this.shipTime;
   }
 
   public void setShipTime(Date shipTime) {
     this.shipTime = shipTime;
   }
 
   public Date getFinishTime() {
     return this.finishTime;
   }
 
   public void setFinishTime(Date finishTime) {
     this.finishTime = finishTime;
   }
 
   public int getInvoiceType() {
     return this.invoiceType;
   }
 
   public void setInvoiceType(int invoiceType) {
     this.invoiceType = invoiceType;
   }
 
   public String getInvoice() {
     return this.invoice;
   }
 
   public void setInvoice(String invoice) {
     this.invoice = invoice;
   }
 
   public Store getStore() {
     return this.store;
   }
 
   public void setStore(Store store) {
     this.store = store;
   }
 
   public List<OrderFormLog> getOfls() {
     return this.ofls;
   }
 
   public void setOfls(List<OrderFormLog> ofls) {
     this.ofls = ofls;
   }
 
   public String getPay_msg() {
     return this.pay_msg;
   }
 
   public void setPay_msg(String pay_msg) {
     this.pay_msg = pay_msg;
   }
 
   public BigDecimal getGoods_amount() {
     return this.goods_amount;
   }
 
   public void setGoods_amount(BigDecimal goods_amount) {
     this.goods_amount = goods_amount;
   }
 
   public List<RefundLog> getRls() {
     return this.rls;
   }
 
   public void setRls(List<RefundLog> rls) {
     this.rls = rls;
   }
 
   public boolean isAuto_confirm_email() {
     return this.auto_confirm_email;
   }
 
   public void setAuto_confirm_email(boolean auto_confirm_email) {
     this.auto_confirm_email = auto_confirm_email;
   }
 
   public boolean isAuto_confirm_sms() {
     return this.auto_confirm_sms;
   }
 
   public void setAuto_confirm_sms(boolean auto_confirm_sms) {
     this.auto_confirm_sms = auto_confirm_sms;
   }
 
   public String getTransport() {
     return this.transport;
   }
 
   public void setTransport(String transport) {
     this.transport = transport;
   }
 
   public ExpressCompany getEc() {
     return this.ec;
   }
 
   public void setEc(ExpressCompany ec) {
     this.ec = ec;
   }
 
   public String getOut_order_id() {
     return this.out_order_id;
   }
 
   public void setOut_order_id(String out_order_id) {
     this.out_order_id = out_order_id;
   }
 
   public String getOrder_seller_intro() {
     return this.order_seller_intro;
   }
 
   public void setOrder_seller_intro(String order_seller_intro) {
     this.order_seller_intro = order_seller_intro;
   }
 }



 
 