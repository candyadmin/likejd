package com.shopping.manage.timer;
 
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.BargainGoods;
import com.shopping.foundation.domain.Goods;
import com.shopping.foundation.domain.GoodsCart;
import com.shopping.foundation.domain.Message;
import com.shopping.foundation.domain.Store;
import com.shopping.foundation.domain.StoreCart;
import com.shopping.foundation.domain.SysConfig;
import com.shopping.foundation.domain.Template;
import com.shopping.foundation.domain.ZTCGoldLog;
import com.shopping.foundation.service.IBargainGoodsService;
import com.shopping.foundation.service.IGoodsCartService;
import com.shopping.foundation.service.IGoodsService;
import com.shopping.foundation.service.IMessageService;
import com.shopping.foundation.service.IStoreCartService;
import com.shopping.foundation.service.IStoreService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.ITemplateService;
import com.shopping.foundation.service.IUserService;
import com.shopping.foundation.service.IZTCGoldLogService;
import com.shopping.lucene.LuceneThread;
import com.shopping.lucene.LuceneVo;
 
 @Component("shop_job")
 @Transactional
 public class JobManageAction
 {
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IZTCGoldLogService ztcGoldLogService;
 
   @Autowired
   private IStoreService storeService;
 
   @Autowired
   private ITemplateService templateService;
 
   @Autowired
   private IMessageService messageService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IBargainGoodsService bargainGoodsService;
 
   @Autowired
   private IStoreCartService storeCartService;
 
   @Autowired
   private IGoodsCartService goodsCartService;
 
   public void execute()
   {
     Map params = new HashMap();
     params.put("ztc_status", Integer.valueOf(2));
     List<Goods> goods_audit_list = this.goodsService.query(
       "select obj from Goods obj where obj.ztc_status=:ztc_status", 
       params, -1, -1);
     for (Goods goods : goods_audit_list) {
       if (goods.getZtc_begin_time().before(new Date())) {
         goods.setZtc_dredge_price(goods.getZtc_price());
         goods.setZtc_status(3);
         this.goodsService.update(goods);
       }
     }
     params.clear();
     params.put("ztc_status", Integer.valueOf(3));
     goods_audit_list = this.goodsService.query(
       "select obj from Goods obj where obj.ztc_status=:ztc_status", 
       params, -1, -1);
     ZTCGoldLog log;
     for (Goods goods : goods_audit_list) {
       if (goods.getZtc_gold() > goods.getZtc_price()) {
         goods.setZtc_gold(goods.getZtc_gold() - goods.getZtc_price());
         goods.setZtc_dredge_price(goods.getZtc_price());
         this.goodsService.update(goods);
         log = new ZTCGoldLog();
         log.setAddTime(new Date());
         log.setZgl_content("直通车消耗金币");
         log.setZgl_gold(goods.getZtc_price());
         log.setZgl_goods(goods);
         log.setZgl_type(1);
         this.ztcGoldLogService.save(log);
       } else {
         goods.setZtc_status(0);
         goods.setZtc_dredge_price(0);
         goods.setZtc_pay_status(0);
         goods.setZtc_apply_time(null);
         this.goodsService.update(goods);
       }
     }
 
     List<Store> stores = this.storeService.query(
       "select obj from Store obj where obj.validity is not null", 
       null, -1, -1);
     Message msg;
     for (Store store : stores) {
       if (store.getValidity().before(new Date())) {
         store.setStore_status(4);
         this.storeService.update(store);
         Template template = this.templateService.getObjByProperty(
           "mark", "msg_toseller_store_auto_closed_notify");
         if ((template != null) && (template.isOpen())) {
           msg = new Message();
           msg.setAddTime(new Date());
           msg.setContent(template.getContent());
           msg.setFromUser(this.userService.getObjByProperty(
             "userName", "admin"));
           msg.setStatus(0);
           msg.setTitle(template.getTitle());
           msg.setToUser(store.getUser());
           msg.setType(0);
           this.messageService.save(msg);
         }
       }
     }
 
     params.clear();
     params.put("goods_status", Integer.valueOf(0));
     List<Goods> goods_list = this.goodsService
       .query(
       "select obj from Goods obj where obj.goods_status=:goods_status", 
       params, -1, -1);
     List goods_vo_list = new ArrayList();
     for (Goods goods : goods_list) {
       LuceneVo vo = new LuceneVo();
       vo.setVo_id(goods.getId());
       vo.setVo_title(goods.getGoods_name());
       vo.setVo_content(goods.getGoods_details());
       vo.setVo_type("goods");
       vo.setVo_store_price(CommUtil.null2Double(goods.getStore_price()));
       vo.setVo_add_time(goods.getAddTime().getTime());
       vo.setVo_goods_salenum(goods.getGoods_salenum());
       goods_vo_list.add(vo);
     }
     String goods_lucene_path = System.getProperty("user.dir") + 
       File.separator + "luence" + File.separator + "goods";
     File file = new File(goods_lucene_path);
     if (!file.exists()) {
       CommUtil.createFolder(goods_lucene_path);
     }
     LuceneThread goods_thread = new LuceneThread(goods_lucene_path, 
       goods_vo_list);
     goods_thread.run();
     SysConfig config = this.configService.getSysConfig();
     config.setLucene_update(new Date());
     this.configService.update(config);
 
     params.clear();
     Calendar cal = Calendar.getInstance();
     cal.add(6, -1);
     params.put("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(cal
       .getTime())));
     List<BargainGoods> bgs = this.bargainGoodsService.query(
       "select obj from BargainGoods obj where obj.bg_time=:bg_time", 
       params, -1, -1);
     for (BargainGoods bg : bgs) {
       bg.setBg_status(-2);
       this.bargainGoodsService.update(bg);
       Goods goods = bg.getBg_goods();
       goods.setBargain_status(0);
       goods.setGoods_current_price(goods.getStore_price());
       this.goodsService.update(goods);
     }
 
     params.clear();
     params.put("bg_time", CommUtil.formatDate(
       CommUtil.formatShortDate(new Date())));
     params.put("bg_status", Integer.valueOf(1));
     bgs = this.bargainGoodsService
       .query(
       "select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status", 
       params, -1, -1);
     Goods goods;
     for (BargainGoods bg : bgs) {
       goods = bg.getBg_goods();
       goods.setBargain_status(2);
       goods.setGoods_current_price(bg.getBg_price());
       this.goodsService.update(goods);
     }
 
     params.clear();
     cal = Calendar.getInstance();
     cal.add(6, -1);
     params.put("addTime", cal.getTime());
     params.put("sc_status", Integer.valueOf(0));
     List<StoreCart> cart_list = this.storeCartService
       .query(
       "select obj from StoreCart obj where obj.user.id is null and obj.addTime<=:addTime and obj.sc_status=:sc_status", 
       params, -1, -1);
     for (StoreCart cart : cart_list) {
       for (GoodsCart gc : cart.getGcs()) {
         gc.getGsps().clear();
         this.goodsCartService.delete(gc.getId());
       }
       this.storeCartService.delete(cart.getId());
     }
 
     params.clear();
     cal = Calendar.getInstance();
     cal.add(6, -7);
     params.put("addTime", cal.getTime());
     params.put("sc_status", Integer.valueOf(0));
     cart_list = this.storeCartService
       .query(
       "select obj from StoreCart obj where obj.user.id is not null and obj.addTime<=:addTime and obj.sc_status=:sc_status", 
       params, -1, -1);
     for (StoreCart cart : cart_list) {
       for (GoodsCart gc : cart.getGcs()) {
         gc.getGsps().clear();
         this.goodsCartService.delete(gc.getId());
       }
       this.storeCartService.delete(cart.getId());
     }
   }
 }


 
 
 