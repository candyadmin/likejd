package com.shopping.foundation.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
/**
 * 相册
 * @author 
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_album")
public class Album extends IdEntity {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7030183976940888977L;
	//相册名称
	private String album_name;
	//相册序号
	private int album_sequence;
	//头像
	@OneToMany(mappedBy = "album", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<Accessory> photos = new ArrayList<Accessory>();
	//相册
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory album_cover;
	//默认相册
	private boolean album_default;
	
	//相册信息
	@Lob
	@Column(columnDefinition = "LongText")
	private String alblum_info;
	
	//相册所属人
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	public String getAlbum_name() {
		return this.album_name;
	}

	public void setAlbum_name(String album_name) {
		this.album_name = album_name;
	}

	public List<Accessory> getPhotos() {
		return this.photos;
	}

	public void setPhotos(List<Accessory> photos) {
		this.photos = photos;
	}

	public String getAlblum_info() {
		return this.alblum_info;
	}

	public void setAlblum_info(String alblum_info) {
		this.alblum_info = alblum_info;
	}

	public int getAlbum_sequence() {
		return this.album_sequence;
	}

	public void setAlbum_sequence(int album_sequence) {
		this.album_sequence = album_sequence;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isAlbum_default() {
		return this.album_default;
	}

	public void setAlbum_default(boolean album_default) {
		this.album_default = album_default;
	}

	public Accessory getAlbum_cover() {
		return this.album_cover;
	}

	public void setAlbum_cover(Accessory album_cover) {
		this.album_cover = album_cover;
	}
}
