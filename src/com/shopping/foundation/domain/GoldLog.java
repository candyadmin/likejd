package com.shopping.foundation.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 金币日志
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_gold_log")
public class GoldLog extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -609688280077748888L;
	//类型
	private int gl_type;
	//金额
	private int gl_money;
	//数量
	private int gl_count;
	//支付
	private String gl_payment;
	
	//内容
	@Column(columnDefinition = "LongText")
	private String gl_content;
	
	//用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User gl_user;
	
	//管理
	@ManyToOne(fetch = FetchType.LAZY)
	private User gl_admin;
	//时间
	private Date gl_admin_time;
	
	//内容
	@Column(columnDefinition = "LongText")
	private String gl_admin_content;
	
	//金币记录
	@OneToOne(fetch = FetchType.LAZY)
	private GoldRecord gr;

	public int getGl_type() {
		return this.gl_type;
	}

	public void setGl_type(int gl_type) {
		this.gl_type = gl_type;
	}

	public int getGl_count() {
		return this.gl_count;
	}

	public void setGl_count(int gl_count) {
		this.gl_count = gl_count;
	}

	public String getGl_content() {
		return this.gl_content;
	}

	public void setGl_content(String gl_content) {
		this.gl_content = gl_content;
	}

	public User getGl_user() {
		return this.gl_user;
	}

	public void setGl_user(User gl_user) {
		this.gl_user = gl_user;
	}

	public User getGl_admin() {
		return this.gl_admin;
	}

	public void setGl_admin(User gl_admin) {
		this.gl_admin = gl_admin;
	}

	public Date getGl_admin_time() {
		return this.gl_admin_time;
	}

	public void setGl_admin_time(Date gl_admin_time) {
		this.gl_admin_time = gl_admin_time;
	}

	public String getGl_admin_content() {
		return this.gl_admin_content;
	}

	public void setGl_admin_content(String gl_admin_content) {
		this.gl_admin_content = gl_admin_content;
	}

	public GoldRecord getGr() {
		return this.gr;
	}

	public void setGr(GoldRecord gr) {
		this.gr = gr;
	}

	public String getGl_payment() {
		return this.gl_payment;
	}

	public void setGl_payment(String gl_payment) {
		this.gl_payment = gl_payment;
	}

	public int getGl_money() {
		return this.gl_money;
	}

	public void setGl_money(int gl_money) {
		this.gl_money = gl_money;
	}
}
