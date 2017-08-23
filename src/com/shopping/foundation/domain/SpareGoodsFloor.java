 package com.shopping.foundation.domain;
 
 import java.util.ArrayList;
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
 @Table(name="shopping_spare_goodsfloor")
 public class SpareGoodsFloor extends IdEntity
 {
   //标题
   private String title;
 
   //序列
   @Column(columnDefinition="int default 0")
   private int sequence;
 
   //余下商品类型
   @OneToOne(fetch=FetchType.LAZY)
   private SpareGoodsClass sgc;
 
   //是否展示
   @Column(columnDefinition="bit default true")
   private boolean display;
 
   //余下商品
   @OneToMany(mappedBy="sgf")
   private List<SpareGoods> sgs = new ArrayList();

   //类型
   @Column(columnDefinition="int default 0")
   private int adver_type;
 
   //位置
   @OneToOne(fetch=FetchType.LAZY)
   private AdvertPosition adp;
 
   //图片
   @OneToOne(fetch=FetchType.LAZY)
   private Accessory advert_img;
   private String advert_url;
 
   public boolean isDisplay() { return this.display; }
 
   public void setDisplay(boolean display)
   {
     this.display = display;
   }
 
   public int getAdver_type() {
     return this.adver_type;
   }
 
   public void setAdver_type(int adver_type) {
     this.adver_type = adver_type;
   }
 
   public AdvertPosition getAdp() {
     return this.adp;
   }
 
   public void setAdp(AdvertPosition adp) {
     this.adp = adp;
   }
 
   public Accessory getAdvert_img() {
     return this.advert_img;
   }
 
   public void setAdvert_img(Accessory advert_img) {
     this.advert_img = advert_img;
   }
 
   public String getAdvert_url() {
     return this.advert_url;
   }
 
   public void setAdvert_url(String advert_url) {
     this.advert_url = advert_url;
   }
 
   public String getTitle() {
     return this.title;
   }
 
   public void setTitle(String title) {
     this.title = title;
   }
 
   public int getSequence() {
     return this.sequence;
   }
 
   public void setSequence(int sequence) {
     this.sequence = sequence;
   }
 
   public SpareGoodsClass getSgc() {
     return this.sgc;
   }
 
   public void setSgc(SpareGoodsClass sgc) {
     this.sgc = sgc;
   }
 
   public List<SpareGoods> getSgs() {
     return this.sgs;
   }
 
   public void setSgs(List<SpareGoods> sgs) {
     this.sgs = sgs;
   }
 }



 
 