 package com.shopping.manage.seller.Tools;
 
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Bargain;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.service.IBargainGoodsService;
 import com.shopping.foundation.service.IBargainService;
 import com.shopping.foundation.service.ISysConfigService;
 import java.math.BigDecimal;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 
 @Component
 public class BargainSellerTools
 {
 
   @Autowired
   private IBargainGoodsService bargainGoodsService;
 
   @Autowired
   private IBargainService bargainServicve;
 
   @Autowired
   private ISysConfigService configService;
 
   public BigDecimal query_bargain_rebate(Object bargain_time)
   {
     Map params = new HashMap();
     params.put("bt", CommUtil.formatDate(
       CommUtil.null2String(bargain_time), "yyyy-MM-dd"));
     List bargain = this.bargainServicve.query(
       "select obj from Bargain obj where obj.bargain_time =:bt", 
       params, 0, 1);
     BigDecimal bd = null;
     if (bargain.size() > 0)
       bd = ((Bargain)bargain.get(0)).getRebate();
     else {
       bd = this.configService.getSysConfig().getBargain_rebate();
     }
     return bd;
   }
 
   public int query_bargain_maximum(Object bargain_time)
   {
     Map params = new HashMap();
     params.put("bt", CommUtil.formatDate(
       CommUtil.null2String(bargain_time), "yyyy-MM-dd"));
     List bargain = this.bargainServicve.query(
       "select obj from Bargain obj where obj.bargain_time =:bt", 
       params, 0, 1);
     int bd = 0;
     if (bargain.size() > 0)
       bd = ((Bargain)bargain.get(0)).getMaximum();
     else {
       bd = this.configService.getSysConfig().getBargain_maximum();
     }
     return bd;
   }
 
   public int query_bargain_audit(Object bargain_time)
   {
     Map params = new HashMap();
     params.put("bg_time", CommUtil.formatDate(
       CommUtil.null2String(bargain_time), "yyyy-MM-dd"));
     params.put("bg_status", Integer.valueOf(1));
     List bargainGoods = this.bargainGoodsService
       .query(
       "select obj from BargainGoods obj where obj.bg_time =:bg_time and obj.bg_status=:bg_status", 
       params, -1, -1);
     return bargainGoods.size();
   }
 }


 
 
 