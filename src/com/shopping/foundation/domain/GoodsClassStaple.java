package com.shopping.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_goodsclassstaple")
public class GoodsClassStaple extends IdEntity {
	/**
	 * 用户常用商品分类
	 */

	//名称
	private String name;
	
	//货物类型
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsClass gc;

	//商店
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GoodsClass getGc() {
		return this.gc;
	}

	public void setGc(GoodsClass gc) {
		this.gc = gc;
	}

	public Store getStore() {
		return this.store;
	}

	public void setStore(Store store) {
		this.store = store;
	}
}
