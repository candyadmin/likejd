package com.shopping.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 优惠券
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_coupon")
public class Coupon extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4988110222207115350L;
	
	//优惠券名字
	private String coupon_name;
	
	//优惠券金额
	@Column(precision = 12, scale = 2)
	private BigDecimal coupon_amount;
	
	//开始时间
	@Temporal(TemporalType.DATE)
	private Date coupon_begin_time;
	
	//结束时间
	@Temporal(TemporalType.DATE)
	private Date coupon_end_time;
	//优惠数量
	private int coupon_count;
	
	//优惠订单金额
	@Column(precision = 12, scale = 2)
	private BigDecimal coupon_order_amount;
	
	//附件
	@OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	private Accessory coupon_acc;
	
	//优惠信息
	@OneToMany(mappedBy = "coupon", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<CouponInfo> couponinfos = new ArrayList<CouponInfo>();

	public String getCoupon_name() {
		return this.coupon_name;
	}

	public void setCoupon_name(String coupon_name) {
		this.coupon_name = coupon_name;
	}

	public BigDecimal getCoupon_amount() {
		return this.coupon_amount;
	}

	public void setCoupon_amount(BigDecimal coupon_amount) {
		this.coupon_amount = coupon_amount;
	}

	public Date getCoupon_begin_time() {
		return this.coupon_begin_time;
	}

	public void setCoupon_begin_time(Date coupon_begin_time) {
		this.coupon_begin_time = coupon_begin_time;
	}

	public Date getCoupon_end_time() {
		return this.coupon_end_time;
	}

	public void setCoupon_end_time(Date coupon_end_time) {
		this.coupon_end_time = coupon_end_time;
	}

	public int getCoupon_count() {
		return this.coupon_count;
	}

	public void setCoupon_count(int coupon_count) {
		this.coupon_count = coupon_count;
	}

	public BigDecimal getCoupon_order_amount() {
		return this.coupon_order_amount;
	}

	public void setCoupon_order_amount(BigDecimal coupon_order_amount) {
		this.coupon_order_amount = coupon_order_amount;
	}

	public Accessory getCoupon_acc() {
		return this.coupon_acc;
	}

	public void setCoupon_acc(Accessory coupon_acc) {
		this.coupon_acc = coupon_acc;
	}

	public List<CouponInfo> getCouponinfos() {
		return this.couponinfos;
	}

	public void setCouponinfos(List<CouponInfo> couponinfos) {
		this.couponinfos = couponinfos;
	}
}
