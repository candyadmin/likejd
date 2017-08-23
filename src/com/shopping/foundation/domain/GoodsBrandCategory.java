package com.shopping.foundation.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_brandcategory")
public class GoodsBrandCategory extends IdEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//名称
	private String name;
	//序列
	private int sequence;
	
	//商品品牌
	@OneToMany(mappedBy = "category")
	private List<GoodsBrand> brands = new ArrayList<GoodsBrand>();

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

	public List<GoodsBrand> getBrands() {
		return this.brands;
	}

	public void setBrands(List<GoodsBrand> brands) {
		this.brands = brands;
	}
}
