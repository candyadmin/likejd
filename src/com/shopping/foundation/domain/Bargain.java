package com.shopping.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 特价
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_bargain")
public class Bargain extends IdEntity {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -7595750284812779918L;

	//特价时间
	@Temporal(TemporalType.DATE)
	private Date bargain_time;
	
	//折扣
	@Column(precision = 3, scale = 2)
	private BigDecimal rebate;

	//最大值
	@Column(columnDefinition = "int default 0")
	private int maximum;
	
	//状态
	@Column(columnDefinition = "LongText")
	private String state;

	public Date getBargain_time() {
		return this.bargain_time;
	}

	public void setBargain_time(Date bargain_time) {
		this.bargain_time = bargain_time;
	}

	public BigDecimal getRebate() {
		return this.rebate;
	}

	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public int getMaximum() {
		return this.maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
