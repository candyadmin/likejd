package com.shopping.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 聊天日志
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_chattinglog")
public class ChattingLog extends IdEntity {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 6666793243500574372L;
	
	//聊天
	@ManyToOne(fetch = FetchType.LAZY)
	private Chatting chatting;
	
	//用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	
	//聊天内容
	@Column(columnDefinition = "LongText")
	private String content;
	
	//标记
	@Column(columnDefinition = "int default 0")
	private int mark;

	public int getMark() {
		return this.mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public Chatting getChatting() {
		return this.chatting;
	}

	public void setChatting(Chatting chatting) {
		this.chatting = chatting;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
