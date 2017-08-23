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
 * 组合聊天
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_combin_log")
public class CombinLog extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6386087245825092486L;
	//开始时间
	private Date begin_time;
	//结束时间
	private Date end_time;
	//金币
	private int gold;

	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;

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

	public Store getStore() {
		return this.store;
	}

	public void setStore(Store store) {
		this.store = store;
	}
}
