package com.shopping.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_goodscart")
public class GoodsCart extends IdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//商品
	@OneToOne
	private Goods goods;
	//数量
	private int count;
	
	//价格
	@Column(precision = 12, scale = 2)
	private BigDecimal price;
	
	//商品规格属性
	@ManyToMany
	@JoinTable(name = "shopping_cart_gsp", joinColumns = {
			@javax.persistence.JoinColumn(name = "cart_id") }, inverseJoinColumns = {
					@javax.persistence.JoinColumn(name = "gsp_id") })
	private List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();
	
    //商品规格
	@Lob
	@Column(columnDefinition = "LongText")
	private String spec_info;

	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm of;
	private String cart_type;
	
	//商店运输
	@ManyToOne(fetch=FetchType.LAZY)
	private StoreCart sc;

	public StoreCart getSc() {
		return this.sc;
	}

	public void setSc(StoreCart sc) {
		this.sc = sc;
	}

	public Goods getGoods() {
		return this.goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<GoodsSpecProperty> getGsps() {
		return this.gsps;
	}

	public void setGsps(List<GoodsSpecProperty> gsps) {
		this.gsps = gsps;
	}

	public String getSpec_info() {
		return this.spec_info;
	}

	public void setSpec_info(String spec_info) {
		this.spec_info = spec_info;
	}

	public OrderForm getOf() {
		return this.of;
	}

	public void setOf(OrderForm of) {
		this.of = of;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCart_type() {
		return this.cart_type;
	}

	public void setCart_type(String cart_type) {
		this.cart_type = cart_type;
	}
}
