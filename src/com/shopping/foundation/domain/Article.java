package com.shopping.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_article")
public class Article extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8847246543488664326L;
	
	//文章标题
	private String title;
	//文章类
	@ManyToOne(fetch = FetchType.LAZY)
	private ArticleClass articleClass;
	//文章地址
	private String url;
	//文章序列
	private int sequence;
	//文章是否显示
	private boolean display;
	//文字标记
	private String mark;
	
	//文章内容
	@Lob
	@Column(columnDefinition = "LongText")
	private String content;

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArticleClass getArticleClass() {
		return this.articleClass;
	}

	public void setArticleClass(ArticleClass articleClass) {
		this.articleClass = articleClass;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public boolean isDisplay() {
		return this.display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMark() {
		return this.mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
}
