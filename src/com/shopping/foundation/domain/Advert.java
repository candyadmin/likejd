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

import com.shopping.core.annotation.Lock;
import com.shopping.core.domain.IdEntity;
/**
 * 广告
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_advert")
public class Advert extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4650388260362359406L;
	
	//广告标题
	private String ad_title;
	
	//广告开始时间
	@Lock
	@Temporal(TemporalType.DATE)
	private Date ad_begin_time;

	//广告结束时间
	@Lock
	@Temporal(TemporalType.DATE)
	private Date ad_end_time;
	
	//广告位置
	@ManyToOne(fetch = FetchType.LAZY)
	private AdvertPosition ad_ap;
	
	//广告状态
	@Lock
	private int ad_status;
	//广告附件
	@OneToOne(cascade = { javax.persistence.CascadeType.REMOVE }, fetch = FetchType.LAZY)
	private Accessory ad_acc;
	//广告内容
	private String ad_text;
	//广告序列
	private int ad_slide_sequence;
	
	//广告所属人
	@ManyToOne(fetch = FetchType.LAZY)
	private User ad_user;
	//广告链接
	private String ad_url;
	//广告点击量
	private int ad_click_num;
	//广告金币
	private int ad_gold;

	public int getAd_gold() {
		return this.ad_gold;
	}

	public void setAd_gold(int ad_gold) {
		this.ad_gold = ad_gold;
	}

	public int getAd_status() {
		return this.ad_status;
	}

	public void setAd_status(int ad_status) {
		this.ad_status = ad_status;
	}

	public String getAd_title() {
		return this.ad_title;
	}

	public void setAd_title(String ad_title) {
		this.ad_title = ad_title;
	}

	public Date getAd_begin_time() {
		return this.ad_begin_time;
	}

	public void setAd_begin_time(Date ad_begin_time) {
		this.ad_begin_time = ad_begin_time;
	}

	public Date getAd_end_time() {
		return this.ad_end_time;
	}

	public void setAd_end_time(Date ad_end_time) {
		this.ad_end_time = ad_end_time;
	}

	public AdvertPosition getAd_ap() {
		return this.ad_ap;
	}

	public void setAd_ap(AdvertPosition ad_ap) {
		this.ad_ap = ad_ap;
	}

	public int getAd_click_num() {
		return this.ad_click_num;
	}

	public void setAd_click_num(int ad_click_num) {
		this.ad_click_num = ad_click_num;
	}

	public Accessory getAd_acc() {
		return this.ad_acc;
	}

	public void setAd_acc(Accessory ad_acc) {
		this.ad_acc = ad_acc;
	}

	public User getAd_user() {
		return this.ad_user;
	}

	public void setAd_user(User ad_user) {
		this.ad_user = ad_user;
	}

	public String getAd_text() {
		return this.ad_text;
	}

	public void setAd_text(String ad_text) {
		this.ad_text = ad_text;
	}

	public String getAd_url() {
		return this.ad_url;
	}

	public void setAd_url(String ad_url) {
		this.ad_url = ad_url;
	}

	public int getAd_slide_sequence() {
		return this.ad_slide_sequence;
	}

	public void setAd_slide_sequence(int ad_slide_sequence) {
		this.ad_slide_sequence = ad_slide_sequence;
	}
}
