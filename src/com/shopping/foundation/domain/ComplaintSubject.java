package com.shopping.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 举报主题
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_complaint_subject")
public class ComplaintSubject extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4022101705095993582L;
	//类型
	private String type;
	
	//标题
	private String title;
	
	//内容
	@Column(columnDefinition = "LongText")
	private String content;

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
