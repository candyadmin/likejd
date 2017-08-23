 package com.shopping.manage.admin.tools;
 
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.OrderForm;
 import com.shopping.foundation.service.IComplaintService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.IOrderFormService;
 import com.shopping.foundation.service.IReportService;
 import com.shopping.foundation.service.IStoreService;
 import com.shopping.foundation.service.IUserService;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 
 @Component
 public class StatTools
 {
 
   @Autowired
   private IStoreService storeService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IOrderFormService orderFormService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IReportService reportService;
 
   @Autowired
   private IComplaintService complaintService;
 
   public int query_store(int count)
   {
     List stores = new ArrayList();
     Map params = new HashMap();
     Calendar cal = Calendar.getInstance();
     cal.add(6, count);
     params.put("time", cal.getTime());
     stores = this.storeService.query(
       "select obj from Store obj where obj.addTime>=:time", params, 
       -1, -1);
     return stores.size();
   }
 
   public int query_user(int count) {
     List users = new ArrayList();
     Map params = new HashMap();
     Calendar cal = Calendar.getInstance();
     cal.add(6, count);
     params.put("time", cal.getTime());
     users = this.userService.query(
       "select obj from User obj where obj.addTime>=:time", params, 
       -1, -1);
     return users.size();
   }
 
   public int query_goods(int count) {
     List goods = new ArrayList();
     Map params = new HashMap();
     Calendar cal = Calendar.getInstance();
     cal.add(6, count);
     params.put("time", cal.getTime());
     goods = this.goodsService.query(
       "select obj from Goods obj where obj.addTime>=:time", params, 
       -1, -1);
     return goods.size();
   }
 
   public int query_order(int count) {
     List orders = new ArrayList();
     Map params = new HashMap();
     Calendar cal = Calendar.getInstance();
     cal.add(6, count);
     params.put("time", cal.getTime());
     orders = this.orderFormService.query(
       "select obj from OrderForm obj where obj.addTime>=:time", 
       params, -1, -1);
     return orders.size();
   }
 
   public int query_all_user() {
     Map params = new HashMap();
     params.put("userRole", "ADMIN");
     List users = this.userService.query(
       "select obj from User obj where obj.userRole!=:userRole", 
       params, -1, -1);
     return users.size();
   }
 
   public int query_all_goods()
   {
     List goods = this.goodsService.query(
       "select obj from Goods obj", null, -1, -1);
     return goods.size();
   }
 
   public int query_all_store() {
     List stores = this.storeService.query(
       "select obj from Store obj", null, -1, -1);
     return stores.size();
   }
 
   public int query_update_store() {
     List stores = this.storeService
       .query(
       "select obj from Store obj where obj.update_grade.id is not null", 
       null, -1, -1);
     return stores.size();
   }
 
   public double query_all_amount() {
     double price = 0.0D;
     Map params = new HashMap();
     params.put("order_status", Integer.valueOf(60));
     List<OrderForm> ofs = this.orderFormService
       .query(
       "select obj from OrderForm obj where obj.order_status=:order_status", 
       params, -1, -1);
     for (OrderForm of : ofs) {
       price = CommUtil.null2Double(of.getTotalPrice()) + price;
     }
     return price;
   }
 
   public int query_complaint(int count) {
     List objs = new ArrayList();
     Map params = new HashMap();
     Calendar cal = Calendar.getInstance();
     cal.add(6, count);
     params.put("time", cal.getTime());
     params.put("status", Integer.valueOf(0));
     objs = this.complaintService
       .query(
       "select obj from Complaint obj where obj.addTime>=:time and obj.status=:status", 
       params, -1, -1);
     return objs.size();
   }
 
   public int query_report(int count) {
     List objs = new ArrayList();
     Map params = new HashMap();
     Calendar cal = Calendar.getInstance();
     cal.add(6, count);
     params.put("time", cal.getTime());
     params.put("status", Integer.valueOf(0));
     objs = this.reportService
       .query(
       "select obj from Report obj where obj.addTime>=:time and obj.status=:status", 
       params, -1, -1);
     return objs.size();
   }
 }


 
 
 