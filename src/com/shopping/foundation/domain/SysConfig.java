 package com.shopping.foundation.domain;
 
 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Lob;
 import javax.persistence.OneToMany;
 import javax.persistence.OneToOne;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.annotation.Lock;
import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_sysconfig")
 public class SysConfig extends IdEntity
 {
   //标题
   private String title;
   //关键字
   private String keywords;
   //描述
   private String description;
   //地址
   private String address;
   //版权
   private String copyRight;
   //下载路径
   private String uploadFilePath;
   //系统语言
   private String sysLanguage;
   private int integralRate;
   private boolean smsEnbale;
   private String smsURL;
   //用户名
   private String smsUserName;
   //密码
   private String smsPassword;
   //测试
   private String smsTest;
   
   private boolean emailEnable;
   //email主人
   private String emailHost;
   //email接口
   private int emailPort;
   //email使用者
   private String emailUser;
   //email使用者名称
   private String emailUserName;
   //email密码
   private String emailPws;
   //email测试
   private String emailTest;
   //网站名称
   private String websiteName;
   //热搜
   private String hotSearch;
 
   @Column(columnDefinition="varchar(255) default 'blue' ")
   private String websiteCss;
 
   //网站logo
   @OneToOne(fetch=FetchType.LAZY)
   private Accessory websiteLogo;
 
   @Lob
   @Column(columnDefinition="LongText")
   private String codeStat;
   //网站状态
   private boolean websiteState;
   //游客是否可以咨询
   private boolean visitorConsult;
 
   //关闭原因
   @Lob
   @Column(columnDefinition="LongText")
   private String closeReason;
   //安全码类型
   private String securityCodeType;
   //是否注册安全码
   private boolean securityCodeRegister;
   //是否可以使用安全码登录
   private boolean securityCodeLogin;
   private boolean securityCodeConsult;
   //图片后缀名
   private String imageSuffix;
   //图片web服务
   private String imageWebServer;
   //图片文件大小
   private int imageFilesize;
   //小宽度
   private int smallWidth;
   //小高度
   private int smallHeight;
   //中等宽度   
   private int middleWidth;
   //中等高度
   private int middleHeight;
   //大宽度
   private int bigWidth;
   //大高度
   private int bigHeight;
   private boolean integral;
   private boolean integralStore;
   //是否有证件
   private boolean voucher;
   //是否有保证金
   private boolean deposit;
   //是否团购
   private boolean groupBuy;
   //是否支持金币
   private boolean gold;
   //金币值
   private int goldMarketValue;
   //成员注册
   private int memberRegister;
   //成员登录
   private int memberDayLogin;
   private int indentComment;
   //合理消费
   private int consumptionRatio;
   private int everyIndentLimit;
   //图片保存类型
   private String imageSaveType;
   //投诉时间
   private int complaint_time;
 
   //店铺图片
   @OneToOne(cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.LAZY)
   private Accessory storeImage;
 
   //商品图片
   @OneToOne(cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.LAZY)
   private Accessory goodsImage;
 
   //成员图标
   @OneToOne(cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.LAZY)
   private Accessory memberIcon;
   private boolean store_allow;
 
   //信用规则
   @Lob
   @Column(columnDefinition="LongText")
   private String creditrule;
 
   //用户信用规则
   @Lob
   @Column(columnDefinition="LongText")
   private String user_creditrule;
 
   //模板
   @Lob
   @Column(columnDefinition="LongText")
   private String templates;
 
   //店铺支付
   @Lob
   @Column(columnDefinition="LongText")
   private String store_payment;
 
   //分享码
   @Lob
   @Column(columnDefinition="LongText")
   private String share_code;
   //ztc状态
   private boolean ztc_status;
 
   //ztc商品浏览
   @Column(columnDefinition="int default 0")
   private int ztc_goods_view;
   //ztc价格
   private int ztc_price;
 
   @Column(columnDefinition="bit default 0")
   private boolean second_domain_open;

   //允许数量
   @Column(columnDefinition="int default 0")
   @Lock
   private int domain_allow_count;
 
   @Column(columnDefinition="LongText")
   @Lock
   private String sys_domain;
 
   //qq登录
   @Column(columnDefinition="bit default 0")
   private boolean qq_login;
   //qqID
   private String qq_login_id;
   private String qq_login_key;
 
   @Column(columnDefinition="LongText")
   private String qq_domain_code;
 
   //新浪登录
   @Column(columnDefinition="bit default 0")
   private boolean sina_login;
   //新浪ID
   private String sina_login_id;
   private String sina_login_key;
 
   @Column(columnDefinition="LongText")
   private String sina_domain_code;
   private Date lucene_update;
 
   @Column(columnDefinition="int default 0")
   @Lock
   private int alipay_fenrun;
 
   @Column(columnDefinition="int default 0")
   @Lock
   private int balance_fenrun;
   //交易标题
   private String bargain_title;
 
   //交易状态
   @Column(columnDefinition="int default 0")
   private int bargain_status;
 
   //交易合法
   @Column(columnDefinition="int default 3")
   private int bargain_validity;
 
   //交易折扣
   @Column(precision=3, scale=2)
   private BigDecimal bargain_rebate;
 
   //交易最大值
   @Column(columnDefinition="int default 0")
   private int bargain_maximum;
 
   //交易状态
   @Column(columnDefinition="LongText")
   private String bargain_state;
   //运送标题
   private String delivery_title;
 
   //运送状态
   @Column(columnDefinition="int default 0")
   private int delivery_status;
 
   //运送数量
   @Column(columnDefinition="int default 50")
   private int delivery_amount;
 
   //结合量
   @Column(columnDefinition="int default 50")
   private int combin_amount;
 
   //结合数量
   @Column(columnDefinition="int default 3")
   private int combin_count;
 
   //登录图片
   @OneToMany(mappedBy="config")
   private List<Accessory> login_imgs = new ArrayList();
 
   //服务电话列表
   @Column(columnDefinition="LongText")
   private String service_telphone_list;
 
   //服务QQ列表
   @Column(columnDefinition="LongText")
   private String service_qq_list;
 
   @Column(columnDefinition="bit default 0")
   private boolean uc_bbs;
   //uc数据
   private String uc_database = "";
   //uc表前缀
   private String uc_table_preffix = "";
   //uc数据地址
   private String uc_database_url = "";
   //uc数据端口
   private String uc_database_port = "";
   //uc数据用户名
   private String uc_database_username = "";
   //uc数据密码
   private String uc_database_pws = "";
   //uc文档
   private String uc_api;
   //ucIP地址
   private String uc_ip;
   private String uc_key;
   //uc应用ID
   private String uc_appid;
 
   @Column(columnDefinition="int default 3")
   @Lock
   private int auto_order_notice;
 
   //自动确认订单
   @Column(columnDefinition="int default 7")
   @Lock
   private int auto_order_confirm;
 
   //自动返回订单
   @Column(columnDefinition="int default 7")
   @Lock
   private int auto_order_return;
 
   //自动评价订单
   @Column(columnDefinition="int default 7")
   @Lock
   private int auto_order_evaluate;
 
   @Column(columnDefinition="LongText")
   private String kuaidi_id;
 
   //货币码
   @Column(columnDefinition="varchar(255) default '¥'")
   private String currency_code;
 
   //微信店铺
   @Lock
   @Column(columnDefinition="bit default 0")
   private boolean weixin_store;
 
   //微信量
   @Lock
   @Column(columnDefinition="int default 50")
   private int weixin_amount;
 
   //支付类型配置
   @Lock
   @Column(columnDefinition="int default 0")
   private int config_payment_type;
 
   //微信图片附件
   @OneToOne
   private Accessory weixin_qr_img;
 
   //微信账目
   @Lock
   private String weixin_account;
 
   @Lock
   private String weixin_token;
 
   //微信应用地址
   @Lock
   private String weixin_appId;
 
   @Lock
   private String weixin_appSecret;

   //微信欢迎内容
   @Lock
   @Column(columnDefinition="LongText")
   private String weixin_welecome_content;
 
   //微信店铺logo
   @Lock
   @OneToOne(fetch=FetchType.LAZY)
   private Accessory store_weixin_logo;
   
   private String site_url;
 
   public int getConfig_payment_type() { return this.config_payment_type; }
 
   public void setConfig_payment_type(int config_payment_type)
   {
     this.config_payment_type = config_payment_type;
   }
 
   public Accessory getWeixin_qr_img() {
     return this.weixin_qr_img;
   }
 
   public void setWeixin_qr_img(Accessory weixin_qr_img) {
     this.weixin_qr_img = weixin_qr_img;
   }
 
   public String getWeixin_account() {
     return this.weixin_account;
   }
 
   public void setWeixin_account(String weixin_account) {
     this.weixin_account = weixin_account;
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
 
   public String getWeixin_welecome_content() {
     return this.weixin_welecome_content;
   }
 
   public void setWeixin_welecome_content(String weixin_welecome_content) {
     this.weixin_welecome_content = weixin_welecome_content;
   }
 
   public Accessory getStore_weixin_logo() {
     return this.store_weixin_logo;
   }
 
   public void setStore_weixin_logo(Accessory store_weixin_logo) {
     this.store_weixin_logo = store_weixin_logo;
   }
 
   public int getWeixin_amount() {
     return this.weixin_amount;
   }
 
   public void setWeixin_amount(int weixin_amount) {
     this.weixin_amount = weixin_amount;
   }
 
   public boolean isWeixin_store() {
     return this.weixin_store;
   }
 
   public void setWeixin_store(boolean weixin_store) {
     this.weixin_store = weixin_store;
   }
 
   public int getAuto_order_return() {
     return this.auto_order_return;
   }
 
   public void setAuto_order_return(int auto_order_return) {
     this.auto_order_return = auto_order_return;
   }
 
   public int getAuto_order_evaluate() {
     return this.auto_order_evaluate;
   }
 
   public void setAuto_order_evaluate(int auto_order_evaluate) {
     this.auto_order_evaluate = auto_order_evaluate;
   }
 
   public int getZtc_goods_view() {
     return this.ztc_goods_view;
   }
 
   public void setZtc_goods_view(int ztc_goods_view) {
     this.ztc_goods_view = ztc_goods_view;
   }
 
   public String getWebsiteCss() {
     return this.websiteCss;
   }
 
   public void setWebsiteCss(String websiteCss) {
     this.websiteCss = websiteCss;
   }
 
   public String getCurrency_code() {
     return this.currency_code;
   }
 
   public void setCurrency_code(String currency_code) {
     this.currency_code = currency_code;
   }
 
   public boolean isUc_bbs() {
     return this.uc_bbs;
   }
 
   public void setUc_bbs(boolean uc_bbs) {
     this.uc_bbs = uc_bbs;
   }
 
   public List<Accessory> getLogin_imgs() {
     return this.login_imgs;
   }
 
   public void setLogin_imgs(List<Accessory> login_imgs) {
     this.login_imgs = login_imgs;
   }
 
   public int getBargain_status() {
     return this.bargain_status;
   }
 
   public void setBargain_status(int bargain_status) {
     this.bargain_status = bargain_status;
   }
 
   public int getBargain_validity() {
     return this.bargain_validity;
   }
 
   public void setBargain_validity(int bargain_validity) {
     this.bargain_validity = bargain_validity;
   }
 
   public BigDecimal getBargain_rebate() {
     return this.bargain_rebate;
   }
 
   public void setBargain_rebate(BigDecimal bargain_rebate) {
     this.bargain_rebate = bargain_rebate;
   }
 
   public int getBargain_maximum() {
     return this.bargain_maximum;
   }
 
   public void setBargain_maximum(int bargain_maximum) {
     this.bargain_maximum = bargain_maximum;
   }
 
   public Date getLucene_update() {
     return this.lucene_update;
   }
 
   public void setLucene_update(Date lucene_update) {
     this.lucene_update = lucene_update;
   }
 
   public boolean isSina_login() {
     return this.sina_login;
   }
 
   public void setSina_login(boolean sina_login) {
     this.sina_login = sina_login;
   }
 
   public String getSina_login_id() {
     return this.sina_login_id;
   }
 
   public void setSina_login_id(String sina_login_id) {
     this.sina_login_id = sina_login_id;
   }
 
   public String getSina_login_key() {
     return this.sina_login_key;
   }
 
   public void setSina_login_key(String sina_login_key) {
     this.sina_login_key = sina_login_key;
   }
 
   public String getSina_domain_code() {
     return this.sina_domain_code;
   }
 
   public void setSina_domain_code(String sina_domain_code) {
     this.sina_domain_code = sina_domain_code;
   }
 
   public boolean isQq_login() {
     return this.qq_login;
   }
 
   public void setQq_login(boolean qq_login) {
     this.qq_login = qq_login;
   }
 
   public String getQq_login_id() {
     return this.qq_login_id;
   }
 
   public void setQq_login_id(String qq_login_id) {
     this.qq_login_id = qq_login_id;
   }
 
   public String getQq_login_key() {
     return this.qq_login_key;
   }
 
   public void setQq_login_key(String qq_login_key) {
     this.qq_login_key = qq_login_key;
   }
 
   public int getDomain_allow_count() {
     return this.domain_allow_count;
   }
 
   public void setDomain_allow_count(int domain_allow_count) {
     this.domain_allow_count = domain_allow_count;
   }
 
   public String getSys_domain() {
     return this.sys_domain;
   }
 
   public void setSys_domain(String sys_domain) {
     this.sys_domain = sys_domain;
   }
 
   public boolean isZtc_status() {
     return this.ztc_status;
   }
 
   public void setZtc_status(boolean ztc_status) {
     this.ztc_status = ztc_status;
   }
 
   public int getZtc_price() {
     return this.ztc_price;
   }
 
   public void setZtc_price(int ztc_price) {
     this.ztc_price = ztc_price;
   }
 
   public String getTemplates() {
     return this.templates;
   }
 
   public void setTemplates(String templates) {
     this.templates = templates;
   }
 
   public boolean isStore_allow() {
     return this.store_allow;
   }
 
   public void setStore_allow(boolean store_allow) {
     this.store_allow = store_allow;
   }
 
   public Accessory getStoreImage() {
     return this.storeImage;
   }
 
   public void setStoreImage(Accessory storeImage) {
     this.storeImage = storeImage;
   }
 
   public Accessory getGoodsImage() {
     return this.goodsImage;
   }
 
   public void setGoodsImage(Accessory goodsImage) {
     this.goodsImage = goodsImage;
   }
 
   public Accessory getMemberIcon() {
     return this.memberIcon;
   }
 
   public void setMemberIcon(Accessory memberIcon) {
     this.memberIcon = memberIcon;
   }
 
   public String getEmailHost() {
     return this.emailHost;
   }
 
   public void setEmailHost(String emailHost) {
     this.emailHost = emailHost;
   }
 
   public int getEmailPort() {
     return this.emailPort;
   }
 
   public void setEmailPort(int emailPort) {
     this.emailPort = emailPort;
   }
 
   public String getEmailUser() {
     return this.emailUser;
   }
 
   public void setEmailUser(String emailUser) {
     this.emailUser = emailUser;
   }
 
   public String getEmailUserName() {
     return this.emailUserName;
   }
 
   public void setEmailUserName(String emailUserName) {
     this.emailUserName = emailUserName;
   }
 
   public String getEmailPws() {
     return this.emailPws;
   }
 
   public void setEmailPws(String emailPws) {
     this.emailPws = emailPws;
   }
 
   public String getSysLanguage() {
     return this.sysLanguage;
   }
 
   public void setSysLanguage(String sysLanguage) {
     this.sysLanguage = sysLanguage;
   }
 
   public String getTitle() {
     return this.title;
   }
 
   public void setTitle(String title) {
     this.title = title;
   }
 
   public String getKeywords() {
     return this.keywords;
   }
 
   public void setKeywords(String keywords) {
     this.keywords = keywords;
   }
 
   public String getSmsURL() {
     return this.smsURL;
   }
 
   public void setSmsURL(String smsURL) {
     this.smsURL = smsURL;
   }
 
   public String getSmsUserName() {
     return this.smsUserName;
   }
 
   public void setSmsUserName(String smsUserName) {
     this.smsUserName = smsUserName;
   }
 
   public String getSmsPassword() {
     return this.smsPassword;
   }
 
   public void setSmsPassword(String smsPassword) {
     this.smsPassword = smsPassword;
   }
 
   public String getDescription() {
     return this.description;
   }
 
   public void setDescription(String description) {
     this.description = description;
   }
 
   public int getIntegralRate() {
     return this.integralRate;
   }
 
   public void setIntegralRate(int integralRate) {
     this.integralRate = integralRate;
   }
 
   public String getCopyRight() {
     return this.copyRight;
   }
 
   public void setCopyRight(String copyRight) {
     this.copyRight = copyRight;
   }
 
   public String getWebsiteName() {
     return this.websiteName;
   }
 
   public void setWebsiteName(String websiteName) {
     this.websiteName = websiteName;
   }
 
   public String getHotSearch() {
     return this.hotSearch;
   }
 
   public void setHotSearch(String hotSearch) {
     this.hotSearch = hotSearch;
   }
 
   public Accessory getWebsiteLogo() {
     return this.websiteLogo;
   }
 
   public void setWebsiteLogo(Accessory websiteLogo) {
     this.websiteLogo = websiteLogo;
   }
 
   public String getCodeStat() {
     return this.codeStat;
   }
 
   public void setCodeStat(String codeStat) {
     this.codeStat = codeStat;
   }
 
   public boolean isWebsiteState() {
     return this.websiteState;
   }
 
   public void setWebsiteState(boolean websiteState) {
     this.websiteState = websiteState;
   }
 
   public String getCloseReason() {
     return this.closeReason;
   }
 
   public void setCloseReason(String closeReason) {
     this.closeReason = closeReason;
   }
 
   public boolean isEmailEnable() {
     return this.emailEnable;
   }
 
   public void setEmailEnable(boolean emailEnable) {
     this.emailEnable = emailEnable;
   }
 
   public String getEmailTest() {
     return this.emailTest;
   }
 
   public void setEmailTest(String emailTest) {
     this.emailTest = emailTest;
   }
 
   public boolean isSecurityCodeRegister() {
     return this.securityCodeRegister;
   }
 
   public void setSecurityCodeRegister(boolean securityCodeRegister) {
     this.securityCodeRegister = securityCodeRegister;
   }
 
   public boolean isSecurityCodeLogin() {
     return this.securityCodeLogin;
   }
 
   public void setSecurityCodeLogin(boolean securityCodeLogin) {
     this.securityCodeLogin = securityCodeLogin;
   }
 
   public boolean isSecurityCodeConsult() {
     return this.securityCodeConsult;
   }
 
   public void setSecurityCodeConsult(boolean securityCodeConsult) {
     this.securityCodeConsult = securityCodeConsult;
   }
 
   public boolean isVisitorConsult() {
     return this.visitorConsult;
   }
 
   public void setVisitorConsult(boolean visitorConsult) {
     this.visitorConsult = visitorConsult;
   }
 
   public String getImageSuffix() {
     return this.imageSuffix;
   }
 
   public void setImageSuffix(String imageSuffix) {
     this.imageSuffix = imageSuffix;
   }
 
   public int getImageFilesize() {
     return this.imageFilesize;
   }
 
   public void setImageFilesize(int imageFilesize) {
     this.imageFilesize = imageFilesize;
   }
 
   public int getSmallWidth() {
     return this.smallWidth;
   }
 
   public void setSmallWidth(int smallWidth) {
     this.smallWidth = smallWidth;
   }
 
   public int getSmallHeight() {
     return this.smallHeight;
   }
 
   public void setSmallHeight(int smallHeight) {
     this.smallHeight = smallHeight;
   }
 
   public int getMiddleWidth() {
     return this.middleWidth;
   }
 
   public void setMiddleWidth(int middleWidth) {
     this.middleWidth = middleWidth;
   }
 
   public int getMiddleHeight() {
     return this.middleHeight;
   }
 
   public void setMiddleHeight(int middleHeight) {
     this.middleHeight = middleHeight;
   }
 
   public int getBigWidth() {
     return this.bigWidth;
   }
 
   public void setBigWidth(int bigWidth) {
     this.bigWidth = bigWidth;
   }
 
   public int getBigHeight() {
     return this.bigHeight;
   }
 
   public void setBigHeight(int bigHeight) {
     this.bigHeight = bigHeight;
   }
 
   public String getImageSaveType() {
     return this.imageSaveType;
   }
 
   public void setImageSaveType(String imageSaveType) {
     this.imageSaveType = imageSaveType;
   }
 
   public String getSecurityCodeType() {
     return this.securityCodeType;
   }
 
   public void setSecurityCodeType(String securityCodeType) {
     this.securityCodeType = securityCodeType;
   }
 
   public boolean isIntegral() {
     return this.integral;
   }
 
   public void setIntegral(boolean integral) {
     this.integral = integral;
   }
 
   public boolean isIntegralStore() {
     return this.integralStore;
   }
 
   public void setIntegralStore(boolean integralStore) {
     this.integralStore = integralStore;
   }
 
   public boolean isVoucher() {
     return this.voucher;
   }
 
   public void setVoucher(boolean voucher) {
     this.voucher = voucher;
   }
 
   public boolean isDeposit() {
     return this.deposit;
   }
 
   public void setDeposit(boolean deposit) {
     this.deposit = deposit;
   }
 
   public boolean isGroupBuy() {
     return this.groupBuy;
   }
 
   public void setGroupBuy(boolean groupBuy) {
     this.groupBuy = groupBuy;
   }
 
   public boolean isGold() {
     return this.gold;
   }
 
   public void setGold(boolean gold) {
     this.gold = gold;
   }
 
   public int getGoldMarketValue() {
     return this.goldMarketValue;
   }
 
   public void setGoldMarketValue(int goldMarketValue) {
     this.goldMarketValue = goldMarketValue;
   }
 
   public int getMemberRegister() {
     return this.memberRegister;
   }
 
   public void setMemberRegister(int memberRegister) {
     this.memberRegister = memberRegister;
   }
 
   public int getMemberDayLogin() {
     return this.memberDayLogin;
   }
 
   public void setMemberDayLogin(int memberDayLogin) {
     this.memberDayLogin = memberDayLogin;
   }
 
   public int getIndentComment() {
     return this.indentComment;
   }
 
   public void setIndentComment(int indentComment) {
     this.indentComment = indentComment;
   }
 
   public int getConsumptionRatio() {
     return this.consumptionRatio;
   }
 
   public void setConsumptionRatio(int consumptionRatio) {
     this.consumptionRatio = consumptionRatio;
   }
 
   public int getEveryIndentLimit() {
     return this.everyIndentLimit;
   }
 
   public void setEveryIndentLimit(int everyIndentLimit) {
     this.everyIndentLimit = everyIndentLimit;
   }
 
   public boolean isSmsEnbale() {
     return this.smsEnbale;
   }
 
   public void setSmsEnbale(boolean smsEnbale) {
     this.smsEnbale = smsEnbale;
   }
 
   public String getSmsTest() {
     return this.smsTest;
   }
 
   public void setSmsTest(String smsTest) {
     this.smsTest = smsTest;
   }
 
   public String getCreditrule() {
     return this.creditrule;
   }
 
   public void setCreditrule(String creditrule) {
     this.creditrule = creditrule;
   }
 
   public String getUploadFilePath() {
     return this.uploadFilePath;
   }
 
   public void setUploadFilePath(String uploadFilePath) {
     this.uploadFilePath = uploadFilePath;
   }
 
   public String getAddress() {
     return this.address;
   }
 
   public void setAddress(String address) {
     this.address = address;
   }
 
   public String getStore_payment() {
     return this.store_payment;
   }
 
   public void setStore_payment(String store_payment) {
     this.store_payment = store_payment;
   }
 
   public String getShare_code() {
     return this.share_code;
   }
 
   public void setShare_code(String share_code) {
     this.share_code = share_code;
   }
 
   public String getUser_creditrule() {
     return this.user_creditrule;
   }
 
   public void setUser_creditrule(String user_creditrule) {
     this.user_creditrule = user_creditrule;
   }
 
   public int getComplaint_time() {
     return this.complaint_time;
   }
 
   public void setComplaint_time(int complaint_time) {
     this.complaint_time = complaint_time;
   }
 
   public boolean isSecond_domain_open() {
     return this.second_domain_open;
   }
 
   public void setSecond_domain_open(boolean second_domain_open) {
     this.second_domain_open = second_domain_open;
   }
 
   public String getQq_domain_code() {
     return this.qq_domain_code;
   }
 
   public void setQq_domain_code(String qq_domain_code) {
     this.qq_domain_code = qq_domain_code;
   }
 
   public String getImageWebServer() {
     return this.imageWebServer;
   }
 
   public void setImageWebServer(String imageWebServer) {
     this.imageWebServer = imageWebServer;
   }
 
   public int getAlipay_fenrun() {
     return this.alipay_fenrun;
   }
 
   public void setAlipay_fenrun(int alipay_fenrun) {
     this.alipay_fenrun = alipay_fenrun;
   }
 
   public int getBalance_fenrun() {
     return this.balance_fenrun;
   }
 
   public void setBalance_fenrun(int balance_fenrun) {
     this.balance_fenrun = balance_fenrun;
   }
 
   public String getBargain_title() {
     return this.bargain_title;
   }
 
   public void setBargain_title(String bargain_title) {
     this.bargain_title = bargain_title;
   }
 
   public String getBargain_state() {
     return this.bargain_state;
   }
 
   public void setBargain_state(String bargain_state) {
     this.bargain_state = bargain_state;
   }
 
   public String getDelivery_title() {
     return this.delivery_title;
   }
 
   public void setDelivery_title(String delivery_title) {
     this.delivery_title = delivery_title;
   }
 
   public int getDelivery_status() {
     return this.delivery_status;
   }
 
   public void setDelivery_status(int delivery_status) {
     this.delivery_status = delivery_status;
   }
 
   public String getService_telphone_list() {
     return this.service_telphone_list;
   }
 
   public void setService_telphone_list(String service_telphone_list) {
     this.service_telphone_list = service_telphone_list;
   }
 
   public String getService_qq_list() {
     return this.service_qq_list;
   }
 
   public void setService_qq_list(String service_qq_list) {
     this.service_qq_list = service_qq_list;
   }
 
   public int getAuto_order_confirm() {
     return this.auto_order_confirm;
   }
 
   public void setAuto_order_confirm(int auto_order_confirm) {
     this.auto_order_confirm = auto_order_confirm;
   }
 
   public int getAuto_order_notice() {
     return this.auto_order_notice;
   }
 
   public void setAuto_order_notice(int auto_order_notice) {
     this.auto_order_notice = auto_order_notice;
   }
 
   public String getKuaidi_id() {
     return this.kuaidi_id;
   }
 
   public void setKuaidi_id(String kuaidi_id) {
     this.kuaidi_id = kuaidi_id;
   }
 
   public String getUc_database() {
     return this.uc_database;
   }
 
   public void setUc_database(String uc_database) {
     this.uc_database = uc_database;
   }
 
   public String getUc_table_preffix() {
     return this.uc_table_preffix;
   }
 
   public void setUc_table_preffix(String uc_table_preffix) {
     this.uc_table_preffix = uc_table_preffix;
   }
 
   public String getUc_database_url() {
     return this.uc_database_url;
   }
 
   public void setUc_database_url(String uc_database_url) {
     this.uc_database_url = uc_database_url;
   }
 
   public String getUc_database_port() {
     return this.uc_database_port;
   }
 
   public void setUc_database_port(String uc_database_port) {
     this.uc_database_port = uc_database_port;
   }
 
   public String getUc_database_username() {
     return this.uc_database_username;
   }
 
   public void setUc_database_username(String uc_database_username) {
     this.uc_database_username = uc_database_username;
   }
 
   public String getUc_database_pws() {
     return this.uc_database_pws;
   }
 
   public void setUc_database_pws(String uc_database_pws) {
     this.uc_database_pws = uc_database_pws;
   }
 
   public String getUc_api() {
     return this.uc_api;
   }
 
   public void setUc_api(String uc_api) {
     this.uc_api = uc_api;
   }
 
   public String getUc_ip() {
     return this.uc_ip;
   }
 
   public void setUc_ip(String uc_ip) {
     this.uc_ip = uc_ip;
   }
 
   public String getUc_key() {
     return this.uc_key;
   }
 
   public void setUc_key(String uc_key) {
     this.uc_key = uc_key;
   }
 
   public String getUc_appid() {
     return this.uc_appid;
   }
 
   public void setUc_appid(String uc_appid) {
     this.uc_appid = uc_appid;
   }
 
   public int getDelivery_amount() {
     return this.delivery_amount;
   }
 
   public void setDelivery_amount(int delivery_amount) {
     this.delivery_amount = delivery_amount;
   }
 
   public int getCombin_amount() {
     return this.combin_amount;
   }
 
   public void setCombin_amount(int combin_amount) {
     this.combin_amount = combin_amount;
   }
 
   public int getCombin_count() {
     return this.combin_count;
   }
 
   public void setCombin_count(int combin_count) {
     this.combin_count = combin_count;
   }
	public String getSite_url() {
		return site_url;
	}
	
	public void setSite_url(String site_url) {
		this.site_url = site_url;
	}
 }



 
 