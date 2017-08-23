package com.shopping.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 优惠券信息
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_coupon_info")
public class CouponInfo extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 9035884340618991295L;
	
	//优惠券码
	private String coupon_sn;
	
	//用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	
	//优惠券
	@ManyToOne(fetch = FetchType.LAZY)
	private Coupon coupon;
	
	//优惠券状态
	@Column(columnDefinition = "int default 0")
	private int status;

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCoupon_sn() {
		return this.coupon_sn;
	}

	public void setCoupon_sn(String coupon_sn) {
		this.coupon_sn = coupon_sn;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Coupon getCoupon() {
		return this.coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}
}
