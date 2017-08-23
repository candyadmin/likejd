 package com.shopping.manage.admin.tools;
 
 import com.shopping.foundation.domain.Bargain;
 import com.shopping.foundation.domain.BargainGoods;
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
 public class BargainManageTools
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
     params.put("bt", bargain_time);
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
     params.put("bt", bargain_time);
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
     params.put("bt", bargain_time);
     List<BargainGoods> bargainGoods = this.bargainGoodsService.query(
       "select obj from BargainGoods obj where obj.bg_time =:bt", 
       params, -1, -1);
     int bd = 0;
     for (BargainGoods bg : bargainGoods) {
       if (bg.getBg_status() == 1) {
         bd++;
       }
     }
     return bd;
   }
 
   public int query_bargain_apply(Object bargain_time)
   {
     Map params = new HashMap();
     params.put("bt", bargain_time);
     List bargainGoods = this.bargainGoodsService.query(
       "select obj from BargainGoods obj where obj.bg_time =:bt", 
       params, -1, -1);
     return bargainGoods.size();
   }
 }


 
 
 