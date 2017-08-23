package com.shopping.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 地址
 * @author shopping
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_address")
public class Address extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -754369306890462179L;

	private String trueName;
	
	//地区
	@ManyToOne(fetch = FetchType.LAZY)
	private Area area;
	//地址信息
	private String area_info;
	//邮编
	private String zip;
	//电话
	private String telephone;
	//手机
	private String mobile;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTrueName() {
		return this.trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public Area getArea() {
		return this.area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getArea_info() {
		return this.area_info;
	}

	public void setArea_info(String area_info) {
		this.area_info = area_info;
	}

	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
