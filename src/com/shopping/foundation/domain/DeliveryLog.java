package com.shopping.foundation.domain;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 收货日志
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_delivery_log")
public class DeliveryLog extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7705942616905919364L;
	//开始时间
	private Date begin_time;
	//结束时间
	private Date end_time;
	//金币数量
	private int gold;
	
	//店铺
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;

	public Store getStore() {
		return this.store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Date getBegin_time() {
		return this.begin_time;
	}

	public void setBegin_time(Date begin_time) {
		this.begin_time = begin_time;
	}

	public Date getEnd_time() {
		return this.end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public int getGold() {
		return this.gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}
}
