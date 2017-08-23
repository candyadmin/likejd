package com.shopping.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.annotation.Lock;
import com.shopping.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "shopping_store")
public class Store extends IdEntity {
	//商店名称
	private String store_name;
	//店主
	private String store_ower;
	//店主名片
	private String store_ower_card;
	//商店电话
	private String store_telephone;
	//商店qq
	private String store_qq;
	//商店msn
	private String store_msn;
	//商店网站
	private String store_ww;
	//商店地址
	private String store_address;
	private String store_zip;
	//商店状态
	private int store_status;

	//用户
	@OneToOne(mappedBy = "store", fetch = FetchType.LAZY)
	private User user;

	//商店级别
	@ManyToOne(fetch = FetchType.LAZY)
	private StoreGrade grade;

	//商店类型
	@ManyToOne(fetch = FetchType.LAZY)
	private StoreClass sc;

	//商店地址
	@ManyToOne(fetch = FetchType.LAZY)
	private Area area;
	//是否推荐商店
	private boolean store_recommend;
	//推荐时间
	private Date store_recommend_time;
	//有效时间
	private Date validity;
	private boolean card_approve;
	
	//商店logo
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory store_logo;

	//商店标语
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory store_banner;

	//卡片附件
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory card;
	private boolean realstore_approve;

	//执照附件
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory store_license;

	//商品集合
	@OneToMany(mappedBy = "goods_store")
	private List<Goods> goods_list = new ArrayList();
	//商店信用
	private int store_credit;
	//模板
	private String template;

	//违规原因
	@Lob
	@Column(columnDefinition = "LongText")
	private String violation_reseaon;

	//seo关键字
	@Lob
	@Column(columnDefinition = "LongText")
	private String store_seo_keywords;

	//seo描述
	@Lob
	@Column(columnDefinition = "LongText")
	private String store_seo_description;

	//商店信息
	@Lob
	@Column(columnDefinition = "LongText")
	private String store_info;

	//更新等级
	@OneToOne(fetch = FetchType.LAZY)
	private StoreGrade update_grade;

	@OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<StoreSlide> slides = new ArrayList();

	@Lock
	private String store_second_domain;

	@Column(columnDefinition = "int default 0")
	private int domain_modify_count;

	@Column(columnDefinition = "int default 0")
	private int favorite_count;

	@OneToOne(mappedBy = "store", fetch = FetchType.LAZY)
	private StorePoint point;

	//地图类型
	@Column(columnDefinition = "varchar(255) default 'baidu'")
	private String map_type;

	@Column(precision = 18, scale = 15)
	private BigDecimal store_lat;

	@Column(precision = 18, scale = 15)
	private BigDecimal store_lng;
	//买送开始时间
	private Date delivery_begin_time;
	//买送结束时间
	private Date delivery_end_time;
	//组合开始时间
	private Date combin_begin_time;
	//组合结束时间
	private Date combin_end_time;

	//商店等级记录
	@OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<StoreGradeLog> logs = new ArrayList();

	//支付
	@OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<Payment> payments = new ArrayList();

	@OneToOne(mappedBy = "store", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	private StorePoint sp;

	//商店导航
	@OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<StoreNavigation> navs = new ArrayList();

	@OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<Favorite> favs = new ArrayList();

	//商品类型主题
	@OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<GoodsClassStaple> gcss = new ArrayList();

	//订单表
	@OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<OrderForm> ofs = new ArrayList();

	//投递记录
	@OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<DeliveryLog> delivery_logs = new ArrayList();

	//结合记录
	@OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<CombinLog> combin_logs = new ArrayList();

	//运输集合
	@OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<Transport> transport_list = new ArrayList();

	@OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
	private List<Dynamic> dynamics = new ArrayList();

	//微信状态
	@Column(columnDefinition = "int default 0")
	private int weixin_status;

	//微信开始时间
	@Temporal(TemporalType.DATE)
	private Date weixin_begin_time;

	//微信结束时间
	@Temporal(TemporalType.DATE)
	private Date weixin_end_time;

	//微信图片
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory weixin_qr_img;
	//微信数量
	private String weixin_account;
	private String weixin_token;
	//微信应用ID
	private String weixin_appId;
	private String weixin_appSecret;

	//微信欢迎内容
	@Column(columnDefinition = "LongText")
	private String weixin_welecome_content;

	//微信店铺logo
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory store_weixin_logo;

	public Accessory getStore_weixin_logo() {
		return this.store_weixin_logo;
	}

	public void setStore_weixin_logo(Accessory store_weixin_logo) {
		this.store_weixin_logo = store_weixin_logo;
	}

	public String getWeixin_account() {
		return this.weixin_account;
	}

	public void setWeixin_account(String weixin_account) {
		this.weixin_account = weixin_account;
	}

	public Accessory getWeixin_qr_img() {
		return this.weixin_qr_img;
	}

	public void setWeixin_qr_img(Accessory weixin_qr_img) {
		this.weixin_qr_img = weixin_qr_img;
	}

	public String getWeixin_welecome_content() {
		return this.weixin_welecome_content;
	}

	public void setWeixin_welecome_content(String weixin_welecome_content) {
		this.weixin_welecome_content = weixin_welecome_content;
	}

	public String getWeixin_token() {
		return this.weixin_token;
	}

	public void setWeixin_token(String weixin_token) {
		this.weixin_token = weixin_token;
	}

	public String getWeixin_appId() {
		return this.weixin_appId;
	}

	public void setWeixin_appId(String weixin_appId) {
		this.weixin_appId = weixin_appId;
	}

	public String getWeixin_appSecret() {
		return this.weixin_appSecret;
	}

	public void setWeixin_appSecret(String weixin_appSecret) {
		this.weixin_appSecret = weixin_appSecret;
	}

	public int getWeixin_status() {
		return this.weixin_status;
	}

	public void setWeixin_status(int weixin_status) {
		this.weixin_status = weixin_status;
	}

	public Date getWeixin_begin_time() {
		return this.weixin_begin_time;
	}

	public void setWeixin_begin_time(Date weixin_begin_time) {
		this.weixin_begin_time = weixin_begin_time;
	}

	public Date getWeixin_end_time() {
		return this.weixin_end_time;
	}

	public void setWeixin_end_time(Date weixin_end_time) {
		this.weixin_end_time = weixin_end_time;
	}

	public List<Dynamic> getDynamics() {
		return this.dynamics;
	}

	public void setDynamics(List<Dynamic> dynamics) {
		this.dynamics = dynamics;
	}

	public List<Transport> getTransport_list() {
		return this.transport_list;
	}

	public void setTransport_list(List<Transport> transport_list) {
		this.transport_list = transport_list;
	}

	public List<CombinLog> getCombin_logs() {
		return this.combin_logs;
	}

	public void setCombin_logs(List<CombinLog> combin_logs) {
		this.combin_logs = combin_logs;
	}

	public List<OrderForm> getOfs() {
		return this.ofs;
	}

	public void setOfs(List<OrderForm> ofs) {
		this.ofs = ofs;
	}

	public List<GoodsClassStaple> getGcss() {
		return this.gcss;
	}

	public void setGcss(List<GoodsClassStaple> gcss) {
		this.gcss = gcss;
	}

	public List<Favorite> getFavs() {
		return this.favs;
	}

	public void setFavs(List<Favorite> favs) {
		this.favs = favs;
	}

	public List<StoreNavigation> getNavs() {
		return this.navs;
	}

	public void setNavs(List<StoreNavigation> navs) {
		this.navs = navs;
	}

	public StorePoint getSp() {
		return this.sp;
	}

	public void setSp(StorePoint sp) {
		this.sp = sp;
	}

	public List<Payment> getPayments() {
		return this.payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public BigDecimal getStore_lat() {
		return this.store_lat;
	}

	public void setStore_lat(BigDecimal store_lat) {
		this.store_lat = store_lat;
	}

	public BigDecimal getStore_lng() {
		return this.store_lng;
	}

	public void setStore_lng(BigDecimal store_lng) {
		this.store_lng = store_lng;
	}

	public int getFavorite_count() {
		return this.favorite_count;
	}

	public void setFavorite_count(int favorite_count) {
		this.favorite_count = favorite_count;
	}

	public String getStore_second_domain() {
		return this.store_second_domain;
	}

	public void setStore_second_domain(String store_second_domain) {
		this.store_second_domain = store_second_domain;
	}

	public int getDomain_modify_count() {
		return this.domain_modify_count;
	}

	public void setDomain_modify_count(int domain_modify_count) {
		this.domain_modify_count = domain_modify_count;
	}

	public List<StoreSlide> getSlides() {
		return this.slides;
	}

	public void setSlides(List<StoreSlide> slides) {
		this.slides = slides;
	}

	public String getViolation_reseaon() {
		return this.violation_reseaon;
	}

	public void setViolation_reseaon(String violation_reseaon) {
		this.violation_reseaon = violation_reseaon;
	}

	public String getTemplate() {
		return this.template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public int getStore_credit() {
		return this.store_credit;
	}

	public void setStore_credit(int store_credit) {
		this.store_credit = store_credit;
	}

	public List<Goods> getGoods_list() {
		return this.goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public StoreClass getSc() {
		return this.sc;
	}

	public void setSc(StoreClass sc) {
		this.sc = sc;
	}

	public Area getArea() {
		return this.area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getStore_address() {
		return this.store_address;
	}

	public void setStore_address(String store_address) {
		this.store_address = store_address;
	}

	public String getStore_name() {
		return this.store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public String getStore_ower() {
		return this.store_ower;
	}

	public void setStore_ower(String store_ower) {
		this.store_ower = store_ower;
	}

	public String getStore_ower_card() {
		return this.store_ower_card;
	}

	public void setStore_ower_card(String store_ower_card) {
		this.store_ower_card = store_ower_card;
	}

	public StoreGrade getGrade() {
		return this.grade;
	}

	public void setGrade(StoreGrade grade) {
		this.grade = grade;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isStore_recommend() {
		return this.store_recommend;
	}

	public void setStore_recommend(boolean store_recommend) {
		this.store_recommend = store_recommend;
	}

	public Date getValidity() {
		return this.validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

	public boolean isCard_approve() {
		return this.card_approve;
	}

	public void setCard_approve(boolean card_approve) {
		this.card_approve = card_approve;
	}

	public Accessory getCard() {
		return this.card;
	}

	public void setCard(Accessory card) {
		this.card = card;
	}

	public boolean isRealstore_approve() {
		return this.realstore_approve;
	}

	public void setRealstore_approve(boolean realstore_approve) {
		this.realstore_approve = realstore_approve;
	}

	public Accessory getStore_license() {
		return this.store_license;
	}

	public void setStore_license(Accessory store_license) {
		this.store_license = store_license;
	}

	public int getStore_status() {
		return this.store_status;
	}

	public void setStore_status(int store_status) {
		this.store_status = store_status;
	}

	public String getStore_telephone() {
		return this.store_telephone;
	}

	public void setStore_telephone(String store_telephone) {
		this.store_telephone = store_telephone;
	}

	public String getStore_zip() {
		return this.store_zip;
	}

	public void setStore_zip(String store_zip) {
		this.store_zip = store_zip;
	}

	public Accessory getStore_logo() {
		return this.store_logo;
	}

	public void setStore_logo(Accessory store_logo) {
		this.store_logo = store_logo;
	}

	public Accessory getStore_banner() {
		return this.store_banner;
	}

	public void setStore_banner(Accessory store_banner) {
		this.store_banner = store_banner;
	}

	public String getStore_seo_keywords() {
		return this.store_seo_keywords;
	}

	public void setStore_seo_keywords(String store_seo_keywords) {
		this.store_seo_keywords = store_seo_keywords;
	}

	public String getStore_seo_description() {
		return this.store_seo_description;
	}

	public void setStore_seo_description(String store_seo_description) {
		this.store_seo_description = store_seo_description;
	}

	public String getStore_info() {
		return this.store_info;
	}

	public void setStore_info(String store_info) {
		this.store_info = store_info;
	}

	public StoreGrade getUpdate_grade() {
		return this.update_grade;
	}

	public void setUpdate_grade(StoreGrade update_grade) {
		this.update_grade = update_grade;
	}

	public Date getStore_recommend_time() {
		return this.store_recommend_time;
	}

	public void setStore_recommend_time(Date store_recommend_time) {
		this.store_recommend_time = store_recommend_time;
	}

	public String getStore_qq() {
		return this.store_qq;
	}

	public void setStore_qq(String store_qq) {
		this.store_qq = store_qq;
	}

	public String getStore_msn() {
		return this.store_msn;
	}

	public void setStore_msn(String store_msn) {
		this.store_msn = store_msn;
	}

	public StorePoint getPoint() {
		return this.point;
	}

	public void setPoint(StorePoint point) {
		this.point = point;
	}

	public List<StoreGradeLog> getLogs() {
		return this.logs;
	}

	public void setLogs(List<StoreGradeLog> logs) {
		this.logs = logs;
	}

	public String getStore_ww() {
		return this.store_ww;
	}

	public void setStore_ww(String store_ww) {
		this.store_ww = store_ww;
	}

	public String getMap_type() {
		return this.map_type;
	}

	public void setMap_type(String map_type) {
		this.map_type = map_type;
	}

	public Date getDelivery_begin_time() {
		return this.delivery_begin_time;
	}

	public void setDelivery_begin_time(Date delivery_begin_time) {
		this.delivery_begin_time = delivery_begin_time;
	}

	public Date getDelivery_end_time() {
		return this.delivery_end_time;
	}

	public void setDelivery_end_time(Date delivery_end_time) {
		this.delivery_end_time = delivery_end_time;
	}

	public List<DeliveryLog> getDelivery_logs() {
		return this.delivery_logs;
	}

	public void setDelivery_logs(List<DeliveryLog> delivery_logs) {
		this.delivery_logs = delivery_logs;
	}

	public Date getCombin_begin_time() {
		return this.combin_begin_time;
	}

	public void setCombin_begin_time(Date combin_begin_time) {
		this.combin_begin_time = combin_begin_time;
	}

	public Date getCombin_end_time() {
		return this.combin_end_time;
	}

	public void setCombin_end_time(Date combin_end_time) {
		this.combin_end_time = combin_end_time;
	}
}
