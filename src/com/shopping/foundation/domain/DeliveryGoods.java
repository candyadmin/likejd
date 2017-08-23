package com.shopping.foundation.domain;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 确认收货
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_delivery_goods")
public class DeliveryGoods extends IdEntity {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -6716674058148213758L;
	
	//商品
	@OneToOne(fetch = FetchType.LAZY)
	private Goods d_goods;
	
	//商品
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods d_delivery_goods;
	//状态
	private int d_status;
	
	//用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User d_admin_user;
	//接受时间
	private Date d_audit_time;
	//拒绝时间
	private Date d_refuse_time;
	
	//开始时间
	@Temporal(TemporalType.DATE)
	private Date d_begin_time;
	
	//结束时间
	@Temporal(TemporalType.DATE)
	private Date d_end_time;

	public Goods getD_goods() {
		return this.d_goods;
	}

	public void setD_goods(Goods d_goods) {
		this.d_goods = d_goods;
	}

	public Goods getD_delivery_goods() {
		return this.d_delivery_goods;
	}

	public void setD_delivery_goods(Goods d_delivery_goods) {
		this.d_delivery_goods = d_delivery_goods;
	}

	public int getD_status() {
		return this.d_status;
	}

	public void setD_status(int d_status) {
		this.d_status = d_status;
	}

	public User getD_admin_user() {
		return this.d_admin_user;
	}

	public void setD_admin_user(User d_admin_user) {
		this.d_admin_user = d_admin_user;
	}

	public Date getD_begin_time() {
		return this.d_begin_time;
	}

	public void setD_begin_time(Date d_begin_time) {
		this.d_begin_time = d_begin_time;
	}

	public Date getD_end_time() {
		return this.d_end_time;
	}

	public void setD_end_time(Date d_end_time) {
		this.d_end_time = d_end_time;
	}

	public Date getD_audit_time() {
		return this.d_audit_time;
	}

	public void setD_audit_time(Date d_audit_time) {
		this.d_audit_time = d_audit_time;
	}

	public Date getD_refuse_time() {
		return this.d_refuse_time;
	}

	public void setD_refuse_time(Date d_refuse_time) {
		this.d_refuse_time = d_refuse_time;
	}
}
