 package com.shopping.manage.admin.action;
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.shopping.core.annotation.SecurityMapping;
import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.WebForm;
import com.shopping.core.tools.database.DatabaseTools;
import com.shopping.foundation.domain.Evaluate;
import com.shopping.foundation.domain.Goods;
import com.shopping.foundation.domain.GoodsCart;
import com.shopping.foundation.domain.Message;
import com.shopping.foundation.domain.OrderForm;
import com.shopping.foundation.domain.User;
import com.shopping.foundation.domain.query.GoodsQueryObject;
import com.shopping.foundation.service.IEvaluateService;
import com.shopping.foundation.service.IGoodsBrandService;
import com.shopping.foundation.service.IGoodsCartService;
import com.shopping.foundation.service.IGoodsClassService;
import com.shopping.foundation.service.IGoodsService;
import com.shopping.foundation.service.IMessageService;
import com.shopping.foundation.service.IOrderFormLogService;
import com.shopping.foundation.service.IOrderFormService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.ITemplateService;
import com.shopping.foundation.service.IUserConfigService;
import com.shopping.foundation.service.IUserService;
import com.shopping.lucene.LuceneUtil;
import com.shopping.lucene.LuceneVo;
import com.shopping.manage.admin.tools.MsgTools;
 
 @Controller
 public class GoodsManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IGoodsBrandService goodsBrandService;
 
   @Autowired
   private IGoodsClassService goodsClassService;
 
   @Autowired
   private ITemplateService templateService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IMessageService messageService;
 
   @Autowired
   private MsgTools msgTools;
 
   @Autowired
   private DatabaseTools databaseTools;
 
   @Autowired
   private IEvaluateService evaluateService;
 
   @Autowired
   private IGoodsCartService goodsCartService;
 
   @Autowired
   private IOrderFormService orderFormService;
 
   @Autowired
   private IOrderFormLogService orderFormLogService;
 
   @SecurityMapping(display = false, rsequence = 0, title="商品列表", value="/admin/goods_list.htm*", rtype="admin", rname="商品管理", rcode="admin_goods", rgroup="商品")
   @RequestMapping({"/admin/goods_list.htm"})
   public ModelAndView goods_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView("admin/blue/goods_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, 
       orderType);
     WebForm wf = new WebForm();
     wf.toQueryPo(request, qo, Goods.class, mv);
     qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(-2)), ">");
     IPageList pList = this.goodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_list.htm", "", 
       params, pList, mv);
     List gbs = this.goodsBrandService.query(
       "select obj from GoodsBrand obj order by obj.sequence asc", 
       null, -1, -1);
     List gcs = this.goodsClassService
       .query(
       "select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc", 
       null, -1, -1);
     mv.addObject("gcs", gcs);
     mv.addObject("gbs", gbs);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="违规商品列表", value="/admin/goods_outline.htm*", rtype="admin", rname="商品管理", rcode="admin_goods", rgroup="商品")
   @RequestMapping({"/admin/goods_outline.htm"})
   public ModelAndView goods_outline(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType) {
     ModelAndView mv = new JModelAndView("admin/blue/goods_outline.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, 
       orderType);
     WebForm wf = new WebForm();
     wf.toQueryPo(request, qo, Goods.class, mv);
     qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(-2)), "=");
     IPageList pList = this.goodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_list.htm", "", 
       params, pList, mv);
     List gbs = this.goodsBrandService.query(
       "select obj from GoodsBrand obj order by obj.sequence asc", 
       null, -1, -1);
     List gcs = this.goodsClassService
       .query(
       "select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc", 
       null, -1, -1);
     mv.addObject("gcs", gcs);
     mv.addObject("gbs", gbs);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品添加", value="/admin/goods_add.htm*", rtype="admin", rname="商品管理", rcode="admin_goods", rgroup="商品")
   @RequestMapping({"/admin/goods_add.htm"})
   public ModelAndView goods_add(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/goods_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品编辑", value="/admin/goods_edit.htm*", rtype="admin", rname="商品管理", rcode="admin_goods", rgroup="商品")
   @RequestMapping({"/admin/goods_edit.htm"})
   public ModelAndView goods_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/goods_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       Goods goods = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
       mv.addObject("obj", goods);
       mv.addObject("currentPage", currentPage);
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品保存", value="/admin/goods_save.htm*", rtype="admin", rname="商品管理", rcode="admin_goods", rgroup="商品")
   @RequestMapping({"/admin/goods_save.htm"})
   public ModelAndView goods_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String cmd, String list_url, String add_url)
   {
     WebForm wf = new WebForm();
     Goods goods = null;
     if (id.equals("")) {
       goods = (Goods)wf.toPo(request, Goods.class);
       goods.setAddTime(new Date());
     } else {
       Goods obj = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
       goods = (Goods)wf.toPo(request, obj);
     }
     if (id.equals(""))
       this.goodsService.save(goods);
     else
       this.goodsService.update(goods);
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("list_url", list_url);
     mv.addObject("op_title", "保存商品成功");
     if (add_url != null) {
       mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="商品删除", value="/admin/goods_del.htm*", rtype="admin", rname="商品管理", rcode="admin_goods", rgroup="商品")
   @RequestMapping({"/admin/goods_del.htm"})
   public String goods_del(HttpServletRequest request, String mulitId) throws Exception {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         Goods goods = this.goodsService.getObjById(
           CommUtil.null2Long(id));
         Map map = new HashMap();
         map.put("gid", goods.getId());
         List<GoodsCart> goodCarts = this.goodsCartService
           .query(
           "select obj from GoodsCart obj where obj.goods.id = :gid", 
           map, -1, -1);
         Long ofid = null;
         Long of_id;
         for (GoodsCart gc : goodCarts) {
           of_id = gc.getOf().getId();
           this.goodsCartService.delete(gc.getId());
           OrderForm of = this.orderFormService.getObjById(of_id);
           if (of.getGcs().size() == 0) {
             this.orderFormService.delete(of_id);
           }
         }
 
         List<Evaluate> evaluates = goods.getEvaluates();
         for (Evaluate e : evaluates) {
           this.evaluateService.delete(e.getId());
         }
         goods.getGoods_ugcs().clear();
         goods.getGoods_ugcs().clear();
         goods.getGoods_photos().clear();
         goods.getGoods_ugcs().clear();
         goods.getGoods_specs().clear();
         this.goodsService.delete(goods.getId());
 
         String goods_lucene_path = System.getProperty("user.dir") + 
           File.separator + "luence" + File.separator + "goods";
         File file = new File(goods_lucene_path);
         if (!file.exists()) {
           CommUtil.createFolder(goods_lucene_path);
         }
         LuceneUtil lucene = LuceneUtil.instance();
         LuceneUtil.setIndex_path(goods_lucene_path);
         lucene.delete_index(CommUtil.null2String(id));
 
         send_site_msg(request, 
           "msg_toseller_goods_delete_by_admin_notify", goods
           .getGoods_store().getUser(), goods, "商城存在违规");
       }
     }
     return "redirect:goods_list.htm";
   }
 
   private void send_site_msg(HttpServletRequest request, String mark, User user, Goods goods, String reason) throws Exception
   {
     com.shopping.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
     if (template.isOpen()) {
       String path = request.getSession().getServletContext().getRealPath("/") + "/vm/";
       PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
       pwrite.print(template.getContent());
       pwrite.flush();
       pwrite.close();
 
       Properties p = new Properties();
       p.setProperty("file.resource.loader.path", request.getRealPath("/") + "vm" + File.separator);
       p.setProperty("input.encoding", "UTF-8");
       p.setProperty("output.encoding", "UTF-8");
       Velocity.init(p);
       org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", 
         "UTF-8");
       VelocityContext context = new VelocityContext();
       context.put("reason", reason);
       context.put("user", user);
       context.put("config", this.configService.getSysConfig());
       context.put("send_time", CommUtil.formatLongDate(new Date()));
       StringWriter writer = new StringWriter();
       blank.merge(context, writer);
 
       String content = writer.toString();
       User fromUser = this.userService.getObjByProperty("userName", "admin");
       Message msg = new Message();
       msg.setAddTime(new Date());
       msg.setContent(content);
       msg.setFromUser(fromUser);
       msg.setTitle(template.getTitle());
       msg.setToUser(user);
       msg.setType(0);
       this.messageService.save(msg);
       CommUtil.deleteFile(path + "temp.vm");
       writer.flush();
       writer.close();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品AJAX更新", value="/admin/goods_ajax.htm*", rtype="admin", rname="商品管理", rcode="admin_goods", rgroup="商品")
   @RequestMapping({"/admin/goods_ajax.htm"})
   public void ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
     Goods obj = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
     Field[] fields = Goods.class.getDeclaredFields();
     BeanWrapper wrapper = new BeanWrapper(obj);
     Object val = null;
     for (Field field : fields) {
       if (field.getName().equals(fieldName)) {
         Class clz = Class.forName("java.lang.String");
         if (field.getType().getName().equals("int")) {
           clz = Class.forName("java.lang.Integer");
         }
         if (field.getType().getName().equals("boolean")) {
           clz = Class.forName("java.lang.Boolean");
         }
         if (!value.equals(""))
           val = BeanUtils.convertType(value, clz);
         else {
           val = Boolean.valueOf(
             !CommUtil.null2Boolean(wrapper
             .getPropertyValue(fieldName)));
         }
         wrapper.setPropertyValue(fieldName, val);
       }
     }
     if (fieldName.equals("store_recommend")) {
       if (obj.isStore_recommend())
         obj.setStore_recommend_time(new Date());
       else
         obj.setStore_recommend_time(null);
     }
     this.goodsService.update(obj);
     if (obj.getGoods_status() == 0)
     {
       String goods_lucene_path = System.getProperty("user.dir") + 
         File.separator + "luence" + File.separator + "goods";
       File file = new File(goods_lucene_path);
       if (!file.exists()) {
         CommUtil.createFolder(goods_lucene_path);
       }
       LuceneVo vo = new LuceneVo();
       vo.setVo_id(obj.getId());
       vo.setVo_title(obj.getGoods_name());
       vo.setVo_content(obj.getGoods_details());
       vo.setVo_type("goods");
       vo.setVo_store_price(CommUtil.null2Double(obj.getStore_price()));
       vo.setVo_add_time(obj.getAddTime().getTime());
       vo.setVo_goods_salenum(obj.getGoods_salenum());
       LuceneUtil lucene = LuceneUtil.instance();
       LuceneUtil.setIndex_path(goods_lucene_path);
       lucene.update(CommUtil.null2String(obj.getId()), vo);
     } else {
       String goods_lucene_path = System.getProperty("user.dir") + 
         File.separator + "luence" + File.separator + "goods";
       File file = new File(goods_lucene_path);
       if (!file.exists()) {
         CommUtil.createFolder(goods_lucene_path);
       }
       LuceneUtil lucene = LuceneUtil.instance();
       LuceneUtil.setIndex_path(goods_lucene_path);
       lucene.delete_index(CommUtil.null2String(id));
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(val.toString());
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 }


 
 
 