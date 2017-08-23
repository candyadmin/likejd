package com.shopping.foundation.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 聊天
 * @author Administrator
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_chatting")
public class Chatting extends IdEntity {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -831388667691865610L;
	
	//用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User user1;
	
	//用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User user2;
	
	//类型
	@Column(columnDefinition = "int default 0")
	private int type;
	
	//日志
	@OneToMany(mappedBy = "chatting", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<ChattingLog> logs = new ArrayList<ChattingLog>();

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public User getUser1() {
		return this.user1;
	}

	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return this.user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}

	public List<ChattingLog> getLogs() {
		return this.logs;
	}

	public void setLogs(List<ChattingLog> logs) {
		this.logs = logs;
	}
}
