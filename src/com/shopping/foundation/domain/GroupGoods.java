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
 @Table(name="shopping_group_goods")
 public class GroupGoods extends IdEntity
 {
   //分组
   @ManyToOne(fetch=FetchType.LAZY)
   private Group group;
   //分组类型
   @ManyToOne(fetch=FetchType.LAZY)
   private GroupClass gg_gc;
   //分组地区
   @ManyToOne(fetch=FetchType.LAZY)
   private GroupArea gg_ga;
   //分组商品名称
   private String gg_name;
   //分组商品
   @ManyToOne(fetch=FetchType.LAZY)
   private Goods gg_goods;
   //分组商品价格
   @Column(precision=12, scale=2)
   private BigDecimal gg_price;
   //分组商品数量
   private int gg_count;
   //分组数量
   private int gg_group_count;
   //定义数量
   private int gg_def_count;
   private int gg_vir_count;
   //数量最小值
   private int gg_min_count;
   //数量最大值
   private int gg_max_count;
 
   //分组商品折扣
   @Column(precision=12, scale=2)
   private BigDecimal gg_rebate;
   //分组商品状态
   private int gg_status;
   //审计时间
   private Date gg_audit_time;
   //分组商品推荐
   private int gg_recommend;
   //分组商品推荐时间
   private Date gg_recommend_time;
   
   //分组商品内容
   @Column(columnDefinition="LongText")
   private String gg_content;
   //分组商品图片
   @OneToOne(fetch=FetchType.LAZY, cascade={javax.persistence.CascadeType.REMOVE})
   private Accessory gg_img;
   //是否在微信店铺推荐
   @Column(columnDefinition="bit default false")
   private boolean weixin_shop_recommend;
   //微信店铺推荐时间
   @Temporal(TemporalType.DATE)
   private Date weixin_shop_recommendTime;
 
   public boolean isWeixin_shop_recommend()
   {
     return this.weixin_shop_recommend;
   }
 
   public void setWeixin_shop_recommend(boolean weixin_shop_recommend) {
     this.weixin_shop_recommend = weixin_shop_recommend;
   }
 
   public Date getWeixin_shop_recommendTime() {
     return this.weixin_shop_recommendTime;
   }
 
   public void setWeixin_shop_recommendTime(Date weixin_shop_recommendTime) {
     this.weixin_shop_recommendTime = weixin_shop_recommendTime;
   }
 
   public Group getGroup() {
     return this.group;
   }
 
   public void setGroup(Group group) {
     this.group = group;
   }
 
   public String getGg_name() {
     return this.gg_name;
   }
 
   public void setGg_name(String gg_name) {
     this.gg_name = gg_name;
   }
 
   public Goods getGg_goods() {
     return this.gg_goods;
   }
 
   public void setGg_goods(Goods gg_goods) {
     this.gg_goods = gg_goods;
   }
 
   public BigDecimal getGg_price() {
     return this.gg_price;
   }
 
   public void setGg_price(BigDecimal gg_price) {
     this.gg_price = gg_price;
   }
 
   public int getGg_group_count() {
     return this.gg_group_count;
   }
 
   public void setGg_group_count(int gg_group_count) {
     this.gg_group_count = gg_group_count;
   }
 
   public int getGg_def_count() {
     return this.gg_def_count;
   }
 
   public void setGg_def_count(int gg_def_count) {
     this.gg_def_count = gg_def_count;
   }
 
   public int getGg_min_count() {
     return this.gg_min_count;
   }
 
   public void setGg_min_count(int gg_min_count) {
     this.gg_min_count = gg_min_count;
   }
 
   public int getGg_max_count() {
     return this.gg_max_count;
   }
 
   public void setGg_max_count(int gg_max_count) {
     this.gg_max_count = gg_max_count;
   }
 
   public BigDecimal getGg_rebate() {
     return this.gg_rebate;
   }
 
   public void setGg_rebate(BigDecimal gg_rebate) {
     this.gg_rebate = gg_rebate;
   }
 
   public int getGg_status() {
     return this.gg_status;
   }
 
   public void setGg_status(int gg_status) {
     this.gg_status = gg_status;
   }
 
   public int getGg_recommend() {
     return this.gg_recommend;
   }
 
   public void setGg_recommend(int gg_recommend) {
     this.gg_recommend = gg_recommend;
   }
 
   public String getGg_content() {
     return this.gg_content;
   }
 
   public void setGg_content(String gg_content) {
     this.gg_content = gg_content;
   }
 
   public GroupClass getGg_gc() {
     return this.gg_gc;
   }
 
   public void setGg_gc(GroupClass gg_gc) {
     this.gg_gc = gg_gc;
   }
 
   public GroupArea getGg_ga() {
     return this.gg_ga;
   }
 
   public void setGg_ga(GroupArea gg_ga) {
     this.gg_ga = gg_ga;
   }
 
   public int getGg_count() {
     return this.gg_count;
   }
 
   public void setGg_count(int gg_count) {
     this.gg_count = gg_count;
   }
 
   public int getGg_vir_count() {
     return this.gg_vir_count;
   }
 
   public void setGg_vir_count(int gg_vir_count) {
     this.gg_vir_count = gg_vir_count;
   }
 
   public Accessory getGg_img() {
     return this.gg_img;
   }
 
   public void setGg_img(Accessory gg_img) {
     this.gg_img = gg_img;
   }
 
   public Date getGg_audit_time() {
     return this.gg_audit_time;
   }
 
   public void setGg_audit_time(Date gg_audit_time) {
     this.gg_audit_time = gg_audit_time;
   }
 
   public Date getGg_recommend_time() {
     return this.gg_recommend_time;
   }
 
   public void setGg_recommend_time(Date gg_recommend_time) {
     this.gg_recommend_time = gg_recommend_time;
   }
 }



 
 