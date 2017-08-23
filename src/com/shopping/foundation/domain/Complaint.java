package com.shopping.foundation.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 投诉
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_complaint")
public class Complaint extends IdEntity {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 1022459514300854119L;
	
	//举报人
	@ManyToOne(fetch = FetchType.LAZY)
	private User from_user;
	
	//被举报人
	@ManyToOne(fetch = FetchType.LAZY)
	private User to_user;
	//举报类型
	private String type;
	//举报状态
	private int status;
	
	//举报商品
	@OneToMany(mappedBy = "complaint", cascade = { javax.persistence.CascadeType.ALL })
	private List<ComplaintGoods> cgs = new ArrayList<ComplaintGoods>();
	
	//举报主题
	@ManyToOne(fetch = FetchType.LAZY)
	private ComplaintSubject cs;
	
	//举报内容
	@Column(columnDefinition = "LongText")
	private String from_user_content;
	
	//被举报内容
	@Column(columnDefinition = "LongText")
	private String to_user_content;
	//举报日期
	private Date appeal_time;
	
	//处理内容
	@Column(columnDefinition = "LongText")
	private String handle_content;
	//处理时间
	private Date handle_time;
	
	//处理人
	@ManyToOne(fetch = FetchType.LAZY)
	private User handle_user;
	
	//证据1
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory from_acc1;
	
	//证据2
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory from_acc2;
	
	//证据3
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory from_acc3;
	
	//举证1
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory to_acc1;

	//举证2
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory to_acc2;

	//举证3
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory to_acc3;
	
	//被举报订单
	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm of;
	
	//聊天内容
	@Column(columnDefinition = "LongText")
	private String talk_content;

	public OrderForm getOf() {
		return this.of;
	}

	public void setOf(OrderForm of) {
		this.of = of;
	}

	public User getFrom_user() {
		return this.from_user;
	}

	public void setFrom_user(User from_user) {
		this.from_user = from_user;
	}

	public User getTo_user() {
		return this.to_user;
	}

	public void setTo_user(User to_user) {
		this.to_user = to_user;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ComplaintSubject getCs() {
		return this.cs;
	}

	public void setCs(ComplaintSubject cs) {
		this.cs = cs;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ComplaintGoods> getCgs() {
		return this.cgs;
	}

	public void setCgs(List<ComplaintGoods> cgs) {
		this.cgs = cgs;
	}

	public String getFrom_user_content() {
		return this.from_user_content;
	}

	public void setFrom_user_content(String from_user_content) {
		this.from_user_content = from_user_content;
	}

	public String getTo_user_content() {
		return this.to_user_content;
	}

	public void setTo_user_content(String to_user_content) {
		this.to_user_content = to_user_content;
	}

	public Accessory getFrom_acc1() {
		return this.from_acc1;
	}

	public void setFrom_acc1(Accessory from_acc1) {
		this.from_acc1 = from_acc1;
	}

	public Accessory getFrom_acc2() {
		return this.from_acc2;
	}

	public void setFrom_acc2(Accessory from_acc2) {
		this.from_acc2 = from_acc2;
	}

	public Accessory getFrom_acc3() {
		return this.from_acc3;
	}

	public void setFrom_acc3(Accessory from_acc3) {
		this.from_acc3 = from_acc3;
	}

	public Accessory getTo_acc1() {
		return this.to_acc1;
	}

	public void setTo_acc1(Accessory to_acc1) {
		this.to_acc1 = to_acc1;
	}

	public Accessory getTo_acc2() {
		return this.to_acc2;
	}

	public void setTo_acc2(Accessory to_acc2) {
		this.to_acc2 = to_acc2;
	}

	public Accessory getTo_acc3() {
		return this.to_acc3;
	}

	public void setTo_acc3(Accessory to_acc3) {
		this.to_acc3 = to_acc3;
	}

	public String getHandle_content() {
		return this.handle_content;
	}

	public void setHandle_content(String handle_content) {
		this.handle_content = handle_content;
	}

	public Date getHandle_time() {
		return this.handle_time;
	}

	public void setHandle_time(Date handle_time) {
		this.handle_time = handle_time;
	}

	public Date getAppeal_time() {
		return this.appeal_time;
	}

	public void setAppeal_time(Date appeal_time) {
		this.appeal_time = appeal_time;
	}

	public String getTalk_content() {
		return this.talk_content;
	}

	public void setTalk_content(String talk_content) {
		this.talk_content = talk_content;
	}

	public User getHandle_user() {
		return this.handle_user;
	}

	public void setHandle_user(User handle_user) {
		this.handle_user = handle_user;
	}
}
