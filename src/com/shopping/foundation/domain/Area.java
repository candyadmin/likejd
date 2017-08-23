package com.shopping.foundation.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 地区
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_area")
public class Area extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -3166365941305570434L;
	//地区名称
	private String areaName;
	
	//地区子类
	@OneToMany(mappedBy = "parent", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<Area> childs = new ArrayList<Area>();
	//地区父类
	@ManyToOne(fetch = FetchType.LAZY)
	private Area parent;
	//地区序列
	private int sequence;
	//地区等级
	private int level;
	
	//是否常用地区
	@Column(columnDefinition = "bit default false")
	private boolean common;

	public boolean isCommon() {
		return this.common;
	}

	public void setCommon(boolean common) {
		this.common = common;
	}

	public List<Area> getChilds() {
		return this.childs;
	}

	public void setChilds(List<Area> childs) {
		this.childs = childs;
	}

	public Area getParent() {
		return this.parent;
	}

	public void setParent(Area parent) {
		this.parent = parent;
	}

	public String getAreaName() {
		return this.areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}
