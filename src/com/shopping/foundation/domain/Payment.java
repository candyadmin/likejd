 package com.shopping.foundation.domain;
 
 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Lob;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_payment")
 public class Payment extends IdEntity
 {
   private boolean install;
   //名称
   private String name;
   //标记
   private String mark;
   //安全码
   private String safeKey;
   //同伴
   private String partner;
   //销售者邮箱
   private String seller_email;
   //接口类型
   private int interfaceType;
   
   //支付宝使用比率
   @Column(precision=12, scale=2)
   private BigDecimal alipay_rate;
   
   @Column(precision=12, scale=2)
   private BigDecimal alipay_divide_rate;
   private String merchantAcctId;
   private String rmbKey;
   private String pid;
   private String spname;
   private String tenpay_partner;
   private String tenpay_key;
   //交易方式
   private int trade_mode;
   //中国银行账户
   private String chinabank_account;
   //中国银行密码
   private String chinabank_key;
 
   //内容
   @Lob
   @Column(columnDefinition="LongText")
   private String content;
 
   @Column(precision=12, scale=2)
   private BigDecimal balance_divide_rate;
   //支付人ID
   private String paypal_userId;
   //货币号
   private String currency_code;
   
   //手续费
   @Column(precision=12, scale=2)
   private BigDecimal poundage;
   //类型
   private String type;
 
   //商店
   @ManyToOne(fetch=FetchType.LAZY)
   private Store store;
   
   @OneToMany(mappedBy="payment")
   private List<OrderForm> ofs = new ArrayList();
   
   private String weixin_appId;
   private String weixin_appSecret;
   private String weixin_partnerId;
   private String weixin_partnerKey;
   private String weixin_paySignKey;
 
   public String getPaypal_userId() {
     return this.paypal_userId;
   }
 
   public void setPaypal_userId(String paypal_userId) {
     this.paypal_userId = paypal_userId;
   }
 
   public String getCurrency_code() {
     return this.currency_code;
   }
 
   public void setCurrency_code(String currency_code) {
     this.currency_code = currency_code;
   }
 
   public BigDecimal getPoundage() {
     return this.poundage;
   }
 
   public void setPoundage(BigDecimal poundage) {
     this.poundage = poundage;
   }
 
   public BigDecimal getAlipay_rate() {
     return this.alipay_rate;
   }
 
   public void setAlipay_rate(BigDecimal alipay_rate) {
     this.alipay_rate = alipay_rate;
   }
 
   public BigDecimal getAlipay_divide_rate() {
     return this.alipay_divide_rate;
   }
 
   public void setAlipay_divide_rate(BigDecimal alipay_divide_rate) {
     this.alipay_divide_rate = alipay_divide_rate;
   }
 
   public BigDecimal getBalance_divide_rate() {
     return this.balance_divide_rate;
   }
 
   public void setBalance_divide_rate(BigDecimal balance_divide_rate) {
     this.balance_divide_rate = balance_divide_rate;
   }
 
   public List<OrderForm> getOfs() {
     return this.ofs;
   }
 
   public void setOfs(List<OrderForm> ofs) {
     this.ofs = ofs;
   }
 
   public boolean isInstall() {
     return this.install;
   }
 
   public String getType() {
     return this.type;
   }
 
   public void setType(String type) {
     this.type = type;
   }
 
   public Store getStore() {
     return this.store;
   }
 
   public void setStore(Store store) {
     this.store = store;
   }
 
   public void setInstall(boolean install) {
     this.install = install;
   }
 
   public String getMark() {
     return this.mark;
   }
 
   public void setMark(String mark) {
     this.mark = mark;
   }
 
   public String getSafeKey() {
     return this.safeKey;
   }
 
   public void setSafeKey(String safeKey) {
     this.safeKey = safeKey;
   }
 
   public String getPartner() {
     return this.partner;
   }
 
   public void setPartner(String partner) {
     this.partner = partner;
   }
 
   public String getSeller_email() {
     return this.seller_email;
   }
 
   public void setSeller_email(String seller_email) {
     this.seller_email = seller_email;
   }
 
   public int getInterfaceType() {
     return this.interfaceType;
   }
 
   public void setInterfaceType(int interfaceType) {
     this.interfaceType = interfaceType;
   }
 
   public String getMerchantAcctId() {
     return this.merchantAcctId;
   }
 
   public void setMerchantAcctId(String merchantAcctId) {
     this.merchantAcctId = merchantAcctId;
   }
 
   public String getRmbKey() {
     return this.rmbKey;
   }
 
   public void setRmbKey(String rmbKey) {
     this.rmbKey = rmbKey;
   }
 
   public String getPid() {
     return this.pid;
   }
 
   public void setPid(String pid) {
     this.pid = pid;
   }
 
   public String getContent() {
     return this.content;
   }
 
   public void setContent(String content) {
     this.content = content;
   }
 
   public String getChinabank_account() {
     return this.chinabank_account;
   }
 
   public void setChinabank_account(String chinabank_account) {
     this.chinabank_account = chinabank_account;
   }
 
   public String getChinabank_key() {
     return this.chinabank_key;
   }
 
   public void setChinabank_key(String chinabank_key) {
     this.chinabank_key = chinabank_key;
   }
 
   public String getName() {
     return this.name;
   }
 
   public void setName(String name) {
     this.name = name;
   }
 
   public String getSpname() {
     return this.spname;
   }
 
   public void setSpname(String spname) {
     this.spname = spname;
   }
 
   public String getTenpay_partner() {
     return this.tenpay_partner;
   }
 
   public void setTenpay_partner(String tenpay_partner) {
     this.tenpay_partner = tenpay_partner;
   }
 
   public String getTenpay_key() {
     return this.tenpay_key;
   }
 
   public void setTenpay_key(String tenpay_key) {
     this.tenpay_key = tenpay_key;
   }
 
   public int getTrade_mode() {
     return this.trade_mode;
   }
 
   public void setTrade_mode(int trade_mode) {
     this.trade_mode = trade_mode;
   }
   
   public String getWeixin_appId() {
		return weixin_appId;
	}

	public void setWeixin_appId(String weixin_appId) {
		this.weixin_appId = weixin_appId;
	}

	public String getWeixin_appSecret() {
		return weixin_appSecret;
	}

	public void setWeixin_appSecret(String weixin_appSecret) {
		this.weixin_appSecret = weixin_appSecret;
	}

	public String getWeixin_partnerId() {
		return weixin_partnerId;
	}

	public void setWeixin_partnerId(String weixin_partnerId) {
		this.weixin_partnerId = weixin_partnerId;
	}

	public String getWeixin_partnerKey() {
		return weixin_partnerKey;
	}

	public void setWeixin_partnerKey(String weixin_partnerKey) {
		this.weixin_partnerKey = weixin_partnerKey;
	}

	public String getWeixin_paySignKey() {
		return weixin_paySignKey;
	}

	public void setWeixin_paySignKey(String weixin_paySignKey) {
		this.weixin_paySignKey = weixin_paySignKey;
	}
	
 }



 
 