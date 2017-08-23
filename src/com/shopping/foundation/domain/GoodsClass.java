package com.shopping.foundation.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_goodsclass")
public class GoodsClass extends IdEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//类型名称
	private String className;

	//商品子分类
	@OneToMany(mappedBy = "parent")
	@OrderBy("sequence asc")
	private List<GoodsClass> childs = new ArrayList<GoodsClass>();
	
	//商品父类
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsClass parent;
	private int sequence;
	//水平等级
	private int level;
	//是否展示
	private boolean display;
	//是否推荐
	private boolean recommend;
	
	//货物类型
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsType goodsType;

	//seo关键字
	@Column(columnDefinition = "LongText")
	private String seo_keywords;

	//seo描述
	@Column(columnDefinition = "LongText")
	private String seo_description;
	
	//货物集合
	@OneToMany(mappedBy = "gc")
	private List<Goods> goods_list = new ArrayList<Goods>();
	
	@OneToMany(mappedBy = "gc")
	private List<GoodsClassStaple> gcss = new ArrayList<GoodsClassStaple>();
	
	//图标类型
	@Column(columnDefinition = "int default 0")
	private int icon_type;
	//图标系统
	private String icon_sys;

	@OneToOne
	private Accessory icon_acc;

	public int getIcon_type() {
		return this.icon_type;
	}

	public void setIcon_type(int icon_type) {
		this.icon_type = icon_type;
	}

	public String getIcon_sys() {
		return this.icon_sys;
	}

	public void setIcon_sys(String icon_sys) {
		this.icon_sys = icon_sys;
	}

	public Accessory getIcon_acc() {
		return this.icon_acc;
	}

	public void setIcon_acc(Accessory icon_acc) {
		this.icon_acc = icon_acc;
	}

	public List<Goods> getGoods_list() {
		return this.goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public List<GoodsClassStaple> getGcss() {
		return this.gcss;
	}

	public void setGcss(List<GoodsClassStaple> gcss) {
		this.gcss = gcss;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isDisplay() {
		return this.display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public boolean isRecommend() {
		return this.recommend;
	}

	public void setRecommend(boolean recommend) {
		this.recommend = recommend;
	}

	public GoodsType getGoodsType() {
		return this.goodsType;
	}

	public void setGoodsType(GoodsType goodsType) {
		this.goodsType = goodsType;
	}

	public List<GoodsClass> getChilds() {
		return this.childs;
	}

	public void setChilds(List<GoodsClass> childs) {
		this.childs = childs;
	}

	public GoodsClass getParent() {
		return this.parent;
	}

	public void setParent(GoodsClass parent) {
		this.parent = parent;
	}

	public String getSeo_keywords() {
		return this.seo_keywords;
	}

	public void setSeo_keywords(String seo_keywords) {
		this.seo_keywords = seo_keywords;
	}

	public String getSeo_description() {
		return this.seo_description;
	}

	public void setSeo_description(String seo_description) {
		this.seo_description = seo_description;
	}
}
