package com.shopping.foundation.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 产品咨询
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_consult")
public class Consult extends IdEntity {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -9210332009604139327L;
	
	//商品
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods goods;
	
	//咨询内容
	@Column(columnDefinition = "LongText")
	private String consult_content;
	//是否回复
	private boolean reply;
	
	//咨询回复
	@Column(columnDefinition = "LongText")
	private String consult_reply;
	
	//咨询用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User consult_user;
	
	//回复用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User reply_user;
	//回复时间
	private Date reply_time;
	//咨询邮件
	private String consult_email;

	public boolean isReply() {
		return this.reply;
	}

	public void setReply(boolean reply) {
		this.reply = reply;
	}

	public String getConsult_email() {
		return this.consult_email;
	}

	public void setConsult_email(String consult_email) {
		this.consult_email = consult_email;
	}

	public Goods getGoods() {
		return this.goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public String getConsult_content() {
		return this.consult_content;
	}

	public void setConsult_content(String consult_content) {
		this.consult_content = consult_content;
	}

	public String getConsult_reply() {
		return this.consult_reply;
	}

	public void setConsult_reply(String consult_reply) {
		this.consult_reply = consult_reply;
	}

	public User getConsult_user() {
		return this.consult_user;
	}

	public void setConsult_user(User consult_user) {
		this.consult_user = consult_user;
	}

	public User getReply_user() {
		return this.reply_user;
	}

	public void setReply_user(User reply_user) {
		this.reply_user = reply_user;
	}

	public Date getReply_time() {
		return this.reply_time;
	}

	public void setReply_time(Date reply_time) {
		this.reply_time = reply_time;
	}
}
