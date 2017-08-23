 package com.shopping.foundation.domain;
 
 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.TreeSet;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import javax.persistence.OneToOne;
 import javax.persistence.Table;
 import javax.persistence.Transient;
 import org.apache.commons.lang.StringUtils;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.springframework.security.GrantedAuthority;
 import org.springframework.security.GrantedAuthorityImpl;
 import org.springframework.security.userdetails.UserDetails;

import com.shopping.core.annotation.Lock;
import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_user")
 public class User extends IdEntity
   implements UserDetails
 {
   private static final long serialVersionUID = 8026813053768023527L;
   //用户名
   private String userName;
   //真实名称
   private String trueName;
   //密码
   @Lock
   private String password;
   //用户角色
   private String userRole;
   //生日
   private Date birthday;
   //电话
   private String telephone;
   private String QQ;
   private String WW;
   //年龄
   @Column(columnDefinition="int default 0")
   private int years;
   private String MSN;
   //地址
   private String address;
   //性别
   private int sex;
   //有限
   private String email;
   private String mobile;
 
   //照片
   @OneToOne
   private Accessory photo;
 
   //地区
   @OneToOne
   private Area area;
   //状态
   private int status;
 
   //角色
   @ManyToMany(targetEntity=Role.class, fetch=FetchType.LAZY)
   @JoinTable(name="shopping_user_role", joinColumns={@javax.persistence.JoinColumn(name="user_id")}, inverseJoinColumns={@javax.persistence.JoinColumn(name="role_id")})
   private Set<Role> roles = new TreeSet();
 
   //角色资源
   @Transient
   private Map<String, List<Res>> roleResources;
   //上次登录时间
   private Date lastLoginDate;
   //登录时间
   private Date loginDate;
   //上次登录IP
   private String lastLoginIp;
   //登录IP
   private String loginIp;
   //登录次数
   private int loginCount;
   //报道
   private int report;
 
   @Lock
   @Column(precision=12, scale=2)
   private BigDecimal availableBalance;
 
   @Lock
   @Column(precision=12, scale=2)
   private BigDecimal freezeBlance;
 
   @Lock
   private int integral;

   //金币
   @Lock
   private int gold;
 
   //配置
   @OneToOne(mappedBy="user")
   private UserConfig config;
 
   //文件附件
   @OneToMany(mappedBy="user")
   private List<Accessory> files = new ArrayList();
 
   //商店
   @OneToOne(cascade={javax.persistence.CascadeType.REMOVE})
   private Store store;
 
   //父用户
   @ManyToOne
   private User parent;
 
   //子用户
   @OneToMany(mappedBy="parent")
   private List<User> childs = new ArrayList();
 
   //用户信用
   @Lock
   private int user_credit;
 
   @Transient
   private GrantedAuthority[] authorities = new GrantedAuthority[0];
   private String qq_openid;
   private String sina_openid;

   //商店快捷菜单
   @Column(columnDefinition="LongText")
   private String store_quick_menu;
 
   @OneToMany(mappedBy="pd_user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Predeposit> posits = new ArrayList();
 
   @OneToMany(mappedBy="pd_log_user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<PredepositLog> user_predepositlogs = new ArrayList();
 
   @OneToMany(mappedBy="pd_log_admin", cascade={javax.persistence.CascadeType.REMOVE})
   private List<PredepositLog> admin_predepositlogs = new ArrayList();
 
   //地址
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Address> addrs = new ArrayList();
 
   //相册
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Album> albums = new ArrayList();
 
   //最喜欢
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Favorite> favs = new ArrayList();
 
   //用户商品类型
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<UserGoodsClass> ugcs = new ArrayList();
 
   //来源信息
   @OneToMany(mappedBy="fromUser", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Message> from_msgs = new ArrayList();
 
   //目标信息
   @OneToMany(mappedBy="toUser", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Message> to_msgs = new ArrayList();
 
   //金币记录
   @OneToMany(mappedBy="gold_user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<GoldRecord> gold_record = new ArrayList();
 
   //金币记录管理
   @OneToMany(mappedBy="gold_admin", cascade={javax.persistence.CascadeType.REMOVE})
   private List<GoldRecord> gold_record_admin = new ArrayList();
 
   @OneToMany(mappedBy="integral_user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<IntegralLog> integral_logs = new ArrayList();
 
   @OneToMany(mappedBy="operate_user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<IntegralLog> integral_admin_logs = new ArrayList();
 
   //系统记录
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<SysLog> syslogs = new ArrayList();
 
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Accessory> accs = new ArrayList();
 
   //订单表
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<OrderForm> ofs = new ArrayList();
 
   //用户商议
   @OneToMany(mappedBy="consult_user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Consult> user_consults = new ArrayList();
 
   //商家商议
   @OneToMany(mappedBy="reply_user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Consult> seller_consults = new ArrayList();
 
   //商家评价
   @OneToMany(mappedBy="evaluate_seller_user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Evaluate> seller_evaluate = new ArrayList();
 
   //用户评价
   @OneToMany(mappedBy="evaluate_user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<Evaluate> user_evaluate = new ArrayList();
 
   //订单记录
   @OneToMany(mappedBy="log_user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<OrderFormLog> ofls = new ArrayList();
 
   //资源返还记录
   @OneToMany(mappedBy="refund_user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<RefundLog> rls = new ArrayList();
 
   //闲置商品
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<SpareGoods> sgs = new ArrayList();
 
   //商品商标
   @OneToMany(mappedBy="user", cascade={javax.persistence.CascadeType.REMOVE})
   private List<GoodsBrand> brands = new ArrayList();
 
   public List<GoodsBrand> getBrands() {
     return this.brands;
   }
 
   public void setBrands(List<GoodsBrand> brands) {
     this.brands = brands;
   }
 
   public List<SpareGoods> getSgs() {
     return this.sgs;
   }
 
   public void setSgs(List<SpareGoods> sgs) {
     this.sgs = sgs;
   }
 
   public int getYears() {
     return this.years;
   }
 
   public void setYears(int years) {
     this.years = years;
   }
 
   public Area getArea() {
     return this.area;
   }
 
   public void setArea(Area area) {
     this.area = area;
   }
 
   public List<OrderFormLog> getOfls() {
     return this.ofls;
   }
 
   public void setOfls(List<OrderFormLog> ofls) {
     this.ofls = ofls;
   }
 
   public List<RefundLog> getRls() {
     return this.rls;
   }
 
   public void setRls(List<RefundLog> rls) {
     this.rls = rls;
   }
 
   public List<Evaluate> getSeller_evaluate() {
     return this.seller_evaluate;
   }
 
   public void setSeller_evaluate(List<Evaluate> seller_evaluate) {
     this.seller_evaluate = seller_evaluate;
   }
 
   public List<Evaluate> getUser_evaluate() {
     return this.user_evaluate;
   }
 
   public void setUser_evaluate(List<Evaluate> user_evaluate) {
     this.user_evaluate = user_evaluate;
   }
 
   public List<Consult> getUser_consults() {
     return this.user_consults;
   }
 
   public void setUser_consults(List<Consult> user_consults) {
     this.user_consults = user_consults;
   }
 
   public List<Consult> getSeller_consults() {
     return this.seller_consults;
   }
 
   public void setSeller_consults(List<Consult> seller_consults) {
     this.seller_consults = seller_consults;
   }
 
   public List<OrderForm> getOfs() {
     return this.ofs;
   }
 
   public void setOfs(List<OrderForm> ofs) {
     this.ofs = ofs;
   }
 
   public List<SysLog> getSyslogs() {
     return this.syslogs;
   }
 
   public void setSyslogs(List<SysLog> syslogs) {
     this.syslogs = syslogs;
   }
 
   public List<Accessory> getAccs() {
     return this.accs;
   }
 
   public void setAccs(List<Accessory> accs) {
     this.accs = accs;
   }
 
   public List<IntegralLog> getIntegral_logs() {
     return this.integral_logs;
   }
 
   public void setIntegral_logs(List<IntegralLog> integral_logs) {
     this.integral_logs = integral_logs;
   }
 
   public List<IntegralLog> getIntegral_admin_logs() {
     return this.integral_admin_logs;
   }
 
   public void setIntegral_admin_logs(List<IntegralLog> integral_admin_logs) {
     this.integral_admin_logs = integral_admin_logs;
   }
 
   public List<GoldRecord> getGold_record() {
     return this.gold_record;
   }
 
   public void setGold_record(List<GoldRecord> gold_record) {
     this.gold_record = gold_record;
   }
 
   public List<GoldRecord> getGold_record_admin() {
     return this.gold_record_admin;
   }
 
   public void setGold_record_admin(List<GoldRecord> gold_record_admin) {
     this.gold_record_admin = gold_record_admin;
   }
 
   public List<Message> getFrom_msgs() {
     return this.from_msgs;
   }
 
   public void setFrom_msgs(List<Message> from_msgs) {
     this.from_msgs = from_msgs;
   }
 
   public List<Message> getTo_msgs() {
     return this.to_msgs;
   }
 
   public void setTo_msgs(List<Message> to_msgs) {
     this.to_msgs = to_msgs;
   }
 
   public List<UserGoodsClass> getUgcs() {
     return this.ugcs;
   }
 
   public void setUgcs(List<UserGoodsClass> ugcs) {
     this.ugcs = ugcs;
   }
 
   public List<Favorite> getFavs() {
     return this.favs;
   }
 
   public void setFavs(List<Favorite> favs) {
     this.favs = favs;
   }
 
   public List<Album> getAlbums() {
     return this.albums;
   }
 
   public void setAlbums(List<Album> albums) {
     this.albums = albums;
   }
 
   public List<Address> getAddrs() {
     return this.addrs;
   }
 
   public void setAddrs(List<Address> addrs) {
     this.addrs = addrs;
   }
 
   public List<Predeposit> getPosits() {
     return this.posits;
   }
 
   public void setPosits(List<Predeposit> posits) {
     this.posits = posits;
   }
 
   public String getSina_openid() {
     return this.sina_openid;
   }
 
   public void setSina_openid(String sina_openid) {
     this.sina_openid = sina_openid;
   }
 
   public String getQq_openid() {
     return this.qq_openid;
   }
 
   public void setQq_openid(String qq_openid) {
     this.qq_openid = qq_openid;
   }
 
   public void setStore(Store store) {
     this.store = store;
   }
 
   public Date getLoginDate() {
     return this.loginDate;
   }
 
   public void setLoginDate(Date loginDate) {
     this.loginDate = loginDate;
   }
 
   public String getLastLoginIp() {
     return this.lastLoginIp;
   }
 
   public void setLastLoginIp(String lastLoginIp) {
     this.lastLoginIp = lastLoginIp;
   }
 
   public String getLoginIp() {
     return this.loginIp;
   }
 
   public void setLoginIp(String loginIp) {
     this.loginIp = loginIp;
   }
 
   public Date getLastLoginDate() {
     return this.lastLoginDate;
   }
 
   public void setLastLoginDate(Date lastLoginDate) {
     this.lastLoginDate = lastLoginDate;
   }
 
   public Date getBirthday() {
     return this.birthday;
   }
 
   public void setBirthday(Date birthday) {
     this.birthday = birthday;
   }
 
   public int getSex() {
     return this.sex;
   }
 
   public void setSex(int sex) {
     this.sex = sex;
   }
 
   public String getEmail() {
     return this.email;
   }
 
   public void setEmail(String email) {
     this.email = email;
   }
 
   public String getMobile() {
     return this.mobile;
   }
 
   public void setMobile(String mobile) {
     this.mobile = mobile;
   }
 
   public GrantedAuthority[] get_all_Authorities() {
     List grantedAuthorities = new ArrayList(
       this.roles.size());
     for (Role role : this.roles) {
       grantedAuthorities
         .add(new GrantedAuthorityImpl(role.getRoleCode()));
     }
     return (GrantedAuthority[])grantedAuthorities.toArray(new GrantedAuthority[this.roles.size()]);
   }
 
   public GrantedAuthority[] get_common_Authorities() {
     List grantedAuthorities = new ArrayList(
       this.roles.size());
     for (Role role : this.roles) {
       if (!role.getType().equals("ADMIN"))
         grantedAuthorities.add(
           new GrantedAuthorityImpl(role
           .getRoleCode()));
     }
     return 
       (GrantedAuthority[])grantedAuthorities
       .toArray(new GrantedAuthority[grantedAuthorities.size()]);
   }
 
   public String getAuthoritiesString() {
     List authorities = new ArrayList();
     for (GrantedAuthority authority : getAuthorities()) {
       authorities.add(authority.getAuthority());
     }
     return StringUtils.join(authorities.toArray(), ",");
   }
 
   public String getPassword() {
     return this.password;
   }
 
   public String getUsername() {
     return this.userName;
   }
 
   public String getUserName() {
     return this.userName;
   }
 
   public void setUserName(String userName) {
     this.userName = userName;
   }
 
   public boolean isAccountNonExpired() {
     return true;
   }
 
   public boolean isAccountNonLocked() {
     return true;
   }
 
   public boolean isCredentialsNonExpired() {
     return true;
   }
 
   public Map<String, List<Res>> getRoleResources()
   {
     if (this.roleResources == null)
     {
       this.roleResources = new HashMap();
 
       for (Role role : this.roles) {
         String roleCode = role.getRoleCode();
         List<Res> ress = role.getReses();
         for (Res res : ress) {
           String key = roleCode + "_" + res.getType();
           if (!this.roleResources.containsKey(key)) {
             this.roleResources.put(key, new ArrayList());
           }
           ((List)this.roleResources.get(key)).add(res);
         }
       }
     }
 
     return this.roleResources;
   }
 
   public void setPassword(String password) {
     this.password = password;
   }
 
   public Set<Role> getRoles() {
     return this.roles;
   }
 
   public void setRoles(Set<Role> roles) {
     this.roles = roles;
   }
 
   public static long getSerialVersionUID() {
     return 8026813053768023527L;
   }
 
   public void setRoleResources(Map<String, List<Res>> roleResources) {
     this.roleResources = roleResources;
   }
 
   public int getStatus() {
     return this.status;
   }
 
   public void setStatus(int status) {
     this.status = status;
   }
 
   public boolean isEnabled()
   {
     return true;
   }
 
   public String getTrueName() {
     return this.trueName;
   }
 
   public void setTrueName(String trueName) {
     this.trueName = trueName;
   }
 
   public String getUserRole() {
     return this.userRole;
   }
 
   public void setUserRole(String userRole) {
     this.userRole = userRole;
   }
 
   public String getTelephone() {
     return this.telephone;
   }
 
   public void setTelephone(String telephone) {
     this.telephone = telephone;
   }
 
   public String getQQ() {
     return this.QQ;
   }
 
   public void setQQ(String qq) {
     this.QQ = qq;
   }
 
   public String getMSN() {
     return this.MSN;
   }
 
   public void setMSN(String msn) {
     this.MSN = msn;
   }
 
   public String getAddress() {
     return this.address;
   }
 
   public void setAddress(String address) {
     this.address = address;
   }
 
   public Accessory getPhoto() {
     return this.photo;
   }
 
   public void setPhoto(Accessory photo) {
     this.photo = photo;
   }
 
   public BigDecimal getAvailableBalance() {
     return this.availableBalance;
   }
 
   public void setAvailableBalance(BigDecimal availableBalance) {
     this.availableBalance = availableBalance;
   }
 
   public BigDecimal getFreezeBlance() {
     return this.freezeBlance;
   }
 
   public void setFreezeBlance(BigDecimal freezeBlance) {
     this.freezeBlance = freezeBlance;
   }
 
   public UserConfig getConfig() {
     return this.config;
   }
 
   public void setConfig(UserConfig config) {
     this.config = config;
   }
 
   public List<Accessory> getFiles() {
     return this.files;
   }
 
   public void setFiles(List<Accessory> files) {
     this.files = files;
   }
 
   public int getIntegral() {
     return this.integral;
   }
 
   public void setIntegral(int integral) {
     this.integral = integral;
   }
 
   public int getLoginCount() {
     return this.loginCount;
   }
 
   public void setLoginCount(int loginCount) {
     this.loginCount = loginCount;
   }
 
   public String getWW() {
     return this.WW;
   }
 
   public void setWW(String ww) {
     this.WW = ww;
   }
 
   public GrantedAuthority[] getAuthorities() {
     return this.authorities;
   }
 
   public void setAuthorities(GrantedAuthority[] authorities) {
     this.authorities = authorities;
   }
 
   public int getGold() {
     return this.gold;
   }
 
   public void setGold(int gold) {
     this.gold = gold;
   }
 
   public int getReport() {
     return this.report;
   }
 
   public void setReport(int report) {
     this.report = report;
   }
 
   public int getUser_credit() {
     return this.user_credit;
   }
 
   public void setUser_credit(int user_credit) {
     this.user_credit = user_credit;
   }
 
   public List<PredepositLog> getUser_predepositlogs() {
     return this.user_predepositlogs;
   }
 
   public void setUser_predepositlogs(List<PredepositLog> user_predepositlogs) {
     this.user_predepositlogs = user_predepositlogs;
   }
 
   public List<PredepositLog> getAdmin_predepositlogs() {
     return this.admin_predepositlogs;
   }
 
   public void setAdmin_predepositlogs(List<PredepositLog> admin_predepositlogs) {
     this.admin_predepositlogs = admin_predepositlogs;
   }
 
   public void setParent(User parent) {
     this.parent = parent;
   }
 
   public List<User> getChilds() {
     return this.childs;
   }
 
   public void setChilds(List<User> childs) {
     this.childs = childs;
   }
 
   public Store getStore() {
     if (getParent() == null) {
       return this.store;
     }
     return getParent().getStore();
   }
 
   public User getParent()
   {
     return this.parent;
   }
 
   public String getStore_quick_menu() {
     return this.store_quick_menu;
   }
 
   public void setStore_quick_menu(String store_quick_menu) {
     this.store_quick_menu = store_quick_menu;
   }
 }



 
 