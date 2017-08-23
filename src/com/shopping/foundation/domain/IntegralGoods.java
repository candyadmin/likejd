 package com.shopping.foundation.domain;
 
 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.OneToMany;
 import javax.persistence.OneToOne;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_integral_goods")
 public class IntegralGoods extends IdEntity
 {
   //商品名称
   private String ig_goods_name;
 
   @Column(precision=12, scale=2)
   //商品价格
   private BigDecimal ig_goods_price;
   private int ig_goods_integral;
   private String ig_goods_sn;
   //商品数量
   private int ig_goods_count;
   //商品标签
   private String ig_goods_tag;
   //商品图片附件
   @OneToOne(fetch=FetchType.LAZY)
   private Accessory ig_goods_img;
   //是否为限制类型
   private boolean ig_limit_type;
   //限制数量
   private int ig_limit_count;
   private int ig_transfee_type;
   
   @Column(precision=12, scale=2)
   private BigDecimal ig_transfee;
   private boolean ig_time_type;
   //开始时间
   private Date ig_begin_time;
   //结束时间
   private Date ig_end_time;
   //是否展示
   private boolean ig_show;
   //是否推荐
   private boolean ig_recommend;
   //序列
   private int ig_sequence;
   //SEO关键字
   private String ig_seo_keywords;
   //SEO描述
   private String ig_seo_description;
   //内容
   private String ig_content;
   //交换总数
   private int ig_exchange_count;
   //点击总数
   private int ig_click_count;
   //商品运输集合 
   @OneToMany(mappedBy="goods", cascade={javax.persistence.CascadeType.REMOVE})
   private List<IntegralGoodsCart> gcs = new ArrayList();
 
   public String getIg_goods_name() {
     return this.ig_goods_name;
   }
 
   public void setIg_goods_name(String ig_goods_name) {
     this.ig_goods_name = ig_goods_name;
   }
 
   public BigDecimal getIg_goods_price() {
     return this.ig_goods_price;
   }
 
   public void setIg_goods_price(BigDecimal ig_goods_price) {
     this.ig_goods_price = ig_goods_price;
   }
 
   public int getIg_goods_integral() {
     return this.ig_goods_integral;
   }
 
   public void setIg_goods_integral(int ig_goods_integral) {
     this.ig_goods_integral = ig_goods_integral;
   }
 
   public String getIg_goods_sn() {
     return this.ig_goods_sn;
   }
 
   public void setIg_goods_sn(String ig_goods_sn) {
     this.ig_goods_sn = ig_goods_sn;
   }
 
   public int getIg_goods_count() {
     return this.ig_goods_count;
   }
 
   public void setIg_goods_count(int ig_goods_count) {
     this.ig_goods_count = ig_goods_count;
   }
 
   public String getIg_goods_tag() {
     return this.ig_goods_tag;
   }
 
   public void setIg_goods_tag(String ig_goods_tag) {
     this.ig_goods_tag = ig_goods_tag;
   }
 
   public Accessory getIg_goods_img() {
     return this.ig_goods_img;
   }
 
   public void setIg_goods_img(Accessory ig_goods_img) {
     this.ig_goods_img = ig_goods_img;
   }
 
   public boolean isIg_limit_type() {
     return this.ig_limit_type;
   }
 
   public void setIg_limit_type(boolean ig_limit_type) {
     this.ig_limit_type = ig_limit_type;
   }
 
   public int getIg_limit_count() {
     return this.ig_limit_count;
   }
 
   public void setIg_limit_count(int ig_limit_count) {
     this.ig_limit_count = ig_limit_count;
   }
 
   public int getIg_transfee_type() {
     return this.ig_transfee_type;
   }
 
   public void setIg_transfee_type(int ig_transfee_type) {
     this.ig_transfee_type = ig_transfee_type;
   }
 
   public BigDecimal getIg_transfee() {
     return this.ig_transfee;
   }
 
   public void setIg_transfee(BigDecimal ig_transfee) {
     this.ig_transfee = ig_transfee;
   }
 
   public boolean isIg_time_type() {
     return this.ig_time_type;
   }
 
   public void setIg_time_type(boolean ig_time_type) {
     this.ig_time_type = ig_time_type;
   }
 
   public Date getIg_begin_time() {
     return this.ig_begin_time;
   }
 
   public void setIg_begin_time(Date ig_begin_time) {
     this.ig_begin_time = ig_begin_time;
   }
 
   public Date getIg_end_time() {
     return this.ig_end_time;
   }
 
   public void setIg_end_time(Date ig_end_time) {
     this.ig_end_time = ig_end_time;
   }
 
   public boolean isIg_show() {
     return this.ig_show;
   }
 
   public void setIg_show(boolean ig_show) {
     this.ig_show = ig_show;
   }
 
   public boolean isIg_recommend() {
     return this.ig_recommend;
   }
 
   public void setIg_recommend(boolean ig_recommend) {
     this.ig_recommend = ig_recommend;
   }
 
   public int getIg_sequence() {
     return this.ig_sequence;
   }
 
   public void setIg_sequence(int ig_sequence) {
     this.ig_sequence = ig_sequence;
   }
 
   public String getIg_seo_keywords() {
     return this.ig_seo_keywords;
   }
 
   public void setIg_seo_keywords(String ig_seo_keywords) {
     this.ig_seo_keywords = ig_seo_keywords;
   }
 
   public String getIg_seo_description() {
     return this.ig_seo_description;
   }
 
   public void setIg_seo_description(String ig_seo_description) {
     this.ig_seo_description = ig_seo_description;
   }
 
   public String getIg_content() {
     return this.ig_content;
   }
 
   public void setIg_content(String ig_content) {
     this.ig_content = ig_content;
   }
 
   public int getIg_exchange_count() {
     return this.ig_exchange_count;
   }
 
   public void setIg_exchange_count(int ig_exchange_count) {
     this.ig_exchange_count = ig_exchange_count;
   }
 
   public int getIg_click_count() {
     return this.ig_click_count;
   }
 
   public void setIg_click_count(int ig_click_count) {
     this.ig_click_count = ig_click_count;
   }
 }



 
 