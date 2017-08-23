 package com.shopping.manage.admin.tools;
 
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Payment;
 import com.shopping.foundation.domain.Store;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.service.IPaymentService;
 import com.shopping.foundation.service.IUserService;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 
 @Component
 public class PaymentTools
 {
 
   @Autowired
   private IPaymentService paymentService;
 
   @Autowired
   private IUserService userService;
 
   public boolean queryPayment(String mark, String type)
   {
     Map params = new HashMap();
     params.put("mark", mark);
     params.put("type", type);
     List objs = this.paymentService
       .query(
       "select obj from Payment obj where obj.mark=:mark and obj.type=:type", 
       params, -1, -1);
     if (objs.size() > 0) {
       return ((Payment)objs.get(0)).isInstall();
     }
     return false;
   }
 
   public Map queryPayment(String mark) {
     Map params = new HashMap();
     params.put("mark", mark);
     params.put("type", "user");
     Long store_id = null;
     store_id = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId()).getStore().getId();
     params.put("store_id", store_id);
     List objs = this.paymentService
       .query(
       "select obj from Payment obj where obj.mark=:mark and obj.type=:type and obj.store.id=:store_id", 
       params, -1, -1);
     Map ret = new HashMap();
     if (objs.size() == 1) {
       ret.put("install", Boolean.valueOf(((Payment)objs.get(0)).isInstall()));
       ret.put("already", Boolean.valueOf(true));
     } else {
       ret.put("install", Boolean.valueOf(false));
       ret.put("already", Boolean.valueOf(false));
     }
     return ret;
   }
 
   public Map queryStorePayment(String mark, String store_id) {
     Map ret = new HashMap();
     Map params = new HashMap();
     params.put("mark", mark);
     params.put("store_id", CommUtil.null2Long(store_id));
     List objs = this.paymentService
       .query("select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", 
       params, -1, -1);
     if (objs.size() == 1) {
       ret.put("install", Boolean.valueOf(((Payment)objs.get(0)).isInstall()));
       ret.put("content", ((Payment)objs.get(0)).getContent());
     } else {
       ret.put("install", Boolean.valueOf(false));
       ret.put("content", "");
     }
     return ret;
   }
 
   public Map queryShopPayment(String mark) {
     Map ret = new HashMap();
     Map params = new HashMap();
     params.put("mark", mark);
     params.put("type", "admin");
     List objs = this.paymentService
       .query("select obj from Payment obj where obj.mark=:mark and obj.type=:type", 
       params, -1, -1);
     if (objs.size() == 1) {
       ret.put("install", Boolean.valueOf(((Payment)objs.get(0)).isInstall()));
       ret.put("content", ((Payment)objs.get(0)).getContent());
     } else {
       ret.put("install", Boolean.valueOf(false));
       ret.put("content", "");
     }
     return ret;
   }
 }


 
 
 