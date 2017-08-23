package com.shopping.foundation.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_goodsbrand")
public class GoodsBrand extends IdEntity {
	/**
	 * 商品品牌
	 */
	private static final long serialVersionUID = 5608767062084760770L;
	//名称
	private String name;
	//序列
	private int sequence;
	
	//品牌logo
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory brandLogo;
	//是否推荐
	private boolean recommend;
	
	//审计
	@Column(columnDefinition = "int default 0")
	private int audit;
	
	//使用者身份
	@Column(columnDefinition = "int default 0")
	private int userStatus;

	//使用者
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	
	//评论
	@Column(columnDefinition = "LongText")
	private String remark;

	//商品种类集合
	@ManyToMany(mappedBy = "gbs")
	private List<GoodsType> types = new ArrayList<GoodsType>();
	
	//商品品牌种类
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsBrandCategory category;
	
	//商品集合
	@OneToMany(mappedBy = "goods_brand")
	private List<Goods> goods_list = new ArrayList<Goods>();
	
	//是否在微信店铺推荐
	@Column(columnDefinition = "bit default false")
	private boolean weixin_shop_recommend;
	
	//微信店铺推荐时间
	@Temporal(TemporalType.DATE)
	private Date weixin_shop_recommendTime;
	private String first_word;

	public String getFirst_word() {
		return this.first_word;
	}

	public void setFirst_word(String first_word) {
		this.first_word = first_word;
	}

	public boolean isWeixin_shop_recommend() {
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

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getUserStatus() {
		return this.userStatus;
	}

	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Goods> getGoods_list() {
		return this.goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public GoodsBrandCategory getCategory() {
		return this.category;
	}

	public void setCategory(GoodsBrandCategory category) {
		this.category = category;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Accessory getBrandLogo() {
		return this.brandLogo;
	}

	public void setBrandLogo(Accessory brandLogo) {
		this.brandLogo = brandLogo;
	}

	public boolean isRecommend() {
		return this.recommend;
	}

	public void setRecommend(boolean recommend) {
		this.recommend = recommend;
	}

	public int getAudit() {
		return this.audit;
	}

	public void setAudit(int audit) {
		this.audit = audit;
	}

	public List<GoodsType> getTypes() {
		return this.types;
	}

	public void setTypes(List<GoodsType> types) {
		this.types = types;
	}
}
