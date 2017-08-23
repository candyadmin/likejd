 package com.shopping.manage.seller.Tools;
 
 import com.shopping.core.domain.virtual.CglibBean;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Area;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.GoodsCart;
 import com.shopping.foundation.domain.StoreCart;
 import com.shopping.foundation.domain.Transport;
 import com.shopping.foundation.service.IAreaService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.ITransportService;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.nutz.json.Json;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 
 @Component
 public class TransportTools
 {
 
   @Autowired
   private ITransportService transportService;
 
   @Autowired
   private IAreaService areaService;
 
   @Autowired
   private IGoodsService goodsService;
 
   public String query_transprot(String json, String mark)
   {
     String ret = "";
     List<Map> list = (List)Json.fromJson(ArrayList.class, json);
     if ((list != null) && (list.size() > 0)) {
       for (Map map : list) {
         if (CommUtil.null2String(map.get("city_id")).equals("-1")) {
           ret = CommUtil.null2String(map.get(mark));
         }
       }
     }
     return ret;
   }
 
   public List<CglibBean> query_all_transprot(String json, int type)
     throws ClassNotFoundException
   {
     List cbs = new ArrayList();
     List<Map> list = (List)Json.fromJson(ArrayList.class, json);
     if ((list != null) && (list.size() > 0)) {
       if (type == 0) {
         for (Map map : list) {
           HashMap propertyMap = new HashMap();
           propertyMap.put("city_id", 
             Class.forName("java.lang.String"));
           propertyMap.put("city_name", 
             Class.forName("java.lang.String"));
           propertyMap.put("trans_weight", 
             Class.forName("java.lang.String"));
           propertyMap.put("trans_fee", 
             Class.forName("java.lang.String"));
           propertyMap.put("trans_add_weight", 
             Class.forName("java.lang.String"));
           propertyMap.put("trans_add_fee", 
             Class.forName("java.lang.String"));
           CglibBean cb = new CglibBean(propertyMap);
           cb.setValue("city_id", 
             CommUtil.null2String(map.get("city_id")));
           cb.setValue("city_name", 
             CommUtil.null2String(map.get("city_name")));
           cb.setValue("trans_weight", 
             CommUtil.null2String(map.get("trans_weight")));
           cb.setValue("trans_fee", 
             CommUtil.null2String(map.get("trans_fee")));
           cb.setValue("trans_add_weight", 
             CommUtil.null2String(map.get("trans_add_weight")));
           cb.setValue("trans_add_fee", 
             CommUtil.null2String(map.get("trans_add_fee")));
           cbs.add(cb);
         }
       }
       if (type == 1) {
         for (Map map : list) {
           if (!CommUtil.null2String(map.get("city_id")).equals("-1")) {
             HashMap propertyMap = new HashMap();
             propertyMap.put("city_id", 
               Class.forName("java.lang.String"));
             propertyMap.put("city_name", 
               Class.forName("java.lang.String"));
             propertyMap.put("trans_weight", 
               Class.forName("java.lang.String"));
             propertyMap.put("trans_fee", 
               Class.forName("java.lang.String"));
             propertyMap.put("trans_add_weight", 
               Class.forName("java.lang.String"));
             propertyMap.put("trans_add_fee", 
               Class.forName("java.lang.String"));
             CglibBean cb = new CglibBean(propertyMap);
             cb.setValue("city_id", 
               CommUtil.null2String(map.get("city_id")));
             cb.setValue("city_name", 
               CommUtil.null2String(map.get("city_name")));
             cb.setValue("trans_weight", 
               CommUtil.null2String(map.get("trans_weight")));
             cb.setValue("trans_fee", 
               CommUtil.null2String(map.get("trans_fee")));
             cb.setValue("trans_add_weight", 
               CommUtil.null2String(map.get("trans_add_weight")));
             cb.setValue("trans_add_fee", 
               CommUtil.null2String(map.get("trans_add_fee")));
             cbs.add(cb);
           }
         }
       }
     }
     return cbs;
   }
 
   public float cal_goods_trans_fee(String trans_id, String type, String weight, String volume, String city_name)
   {
     Transport trans = this.transportService.getObjById(
       CommUtil.null2Long(trans_id));
     String json = "";
     if (type.equals("mail")) {
       json = trans.getTrans_mail_info();
     }
     if (type.equals("express")) {
       json = trans.getTrans_express_info();
     }
     if (type.equals("ems")) {
       json = trans.getTrans_ems_info();
     }
     float fee = 0.0F;
     boolean cal_flag = false;
     List<Map> list = (List)Json.fromJson(ArrayList.class, json);
     if ((list != null) && (list.size() > 0)) {
       for (Map map : list) {
         String[] city_list = CommUtil.null2String(map.get("city_name")).split("、");
         for (String city : city_list) {
           if (city.equals(city_name)) {
             cal_flag = true;
             float trans_weight = CommUtil.null2Float(map
               .get("trans_weight"));
             float trans_fee = CommUtil.null2Float(map
               .get("trans_fee"));
             float trans_add_weight = CommUtil.null2Float(map
               .get("trans_add_weight"));
             float trans_add_fee = CommUtil.null2Float(map
               .get("trans_add_fee"));
             if (trans.getTrans_type() == 0) {
               fee = trans_fee;
             }
             if (trans.getTrans_type() == 1) {
               float goods_weight = CommUtil.null2Float(weight);
               if (goods_weight > 0.0F) {
                 fee = trans_fee;
                 float other_price = 0.0F;
                 if (trans_add_weight > 0.0F) {
                   other_price = trans_add_fee * 
                     (float)Math.round(Math.ceil(
                     CommUtil.subtract(Float.valueOf(goods_weight), 
                     Float.valueOf(trans_weight)))) / 
                     trans_add_fee;
                 }
                 fee += other_price;
               }
             }
             if (trans.getTrans_type() != 2) break;
             float goods_volume = CommUtil.null2Float(volume);
             if (goods_volume <= 0.0F) break;
             fee = trans_fee;
             float other_price = 0.0F;
             if (trans_add_weight > 0.0F) {
               other_price = trans_add_fee * 
                 (float)Math.round(Math.ceil(
                 CommUtil.subtract(Float.valueOf(goods_volume), 
                 Float.valueOf(trans_weight)))) / 
                 trans_add_fee;
             }
             fee += other_price;
 
             break;
           }
         }
       }
       if (!cal_flag) {
         for (Map map : list) {
           String[] city_list = CommUtil.null2String(
             map.get("city_name")).split("、");
           for (String city : city_list) {
             if (city.equals("全国")) {
               float trans_weight = CommUtil.null2Float(map
                 .get("trans_weight"));
               float trans_fee = CommUtil.null2Float(map
                 .get("trans_fee"));
               float trans_add_weight = CommUtil.null2Float(map
                 .get("trans_add_weight"));
               float trans_add_fee = CommUtil.null2Float(map
                 .get("trans_add_fee"));
               if (trans.getTrans_type() == 0) {
                 fee = trans_fee;
               }
               if (trans.getTrans_type() == 1) {
                 float goods_weight = 
                   CommUtil.null2Float(weight);
                 if (goods_weight > 0.0F) {
                   fee = trans_fee;
                   float other_price = 0.0F;
                   if (trans_add_weight > 0.0F) {
                     other_price = trans_add_fee * 
                       (float)Math.round(Math.ceil(
                       CommUtil.subtract(Float.valueOf(goods_weight), 
                       Float.valueOf(trans_weight)))) / 
                       trans_add_fee;
                   }
                   fee += other_price;
                 }
               }
               if (trans.getTrans_type() != 2) break;
               float goods_volume = 
                 CommUtil.null2Float(volume);
               if (goods_volume <= 0.0F) break;
               fee = trans_fee;
               float other_price = 0.0F;
               if (trans_add_weight > 0.0F) {
                 other_price = trans_add_fee * 
                   (float)Math.round(Math.ceil(
                   CommUtil.subtract(Float.valueOf(goods_volume), 
                   Float.valueOf(trans_weight)))) / 
                   trans_add_fee;
               }
               fee += other_price;
 
               break;
             }
           }
         }
       }
     }
     return fee;
   }
 
   public List<SysMap> query_cart_trans(StoreCart sc, String area_id) {
     List sms = new ArrayList();
     if ((area_id != null) && (!area_id.equals(""))) {
       Area area = this.areaService
         .getObjById(CommUtil.null2Long(area_id)).getParent();
       String city_name = area.getAreaName();
 
       float mail_fee = 0.0F;
       float express_fee = 0.0F;
       float ems_fee = 0.0F;
       for (GoodsCart gc : sc.getGcs()) {
         Goods goods = this.goodsService.getObjById(gc.getGoods()
           .getId());
         if (goods.getGoods_transfee() == 0) {
           if (goods.getTransport() != null)
           {
             mail_fee = mail_fee + 
               cal_order_trans(goods.getTransport()
               .getTrans_mail_info(), goods
               .getTransport().getTrans_type(), goods
               .getGoods_weight(), goods
               .getGoods_volume(), city_name);
 
             express_fee = express_fee + 
               cal_order_trans(goods.getTransport()
               .getTrans_express_info(), goods
               .getTransport().getTrans_type(), goods
               .getGoods_weight(), goods
               .getGoods_volume(), city_name);
 
             ems_fee = ems_fee + 
               cal_order_trans(goods.getTransport()
               .getTrans_ems_info(), goods
               .getTransport().getTrans_type(), goods
               .getGoods_weight(), goods
               .getGoods_volume(), city_name);
           }
           else
           {
             mail_fee = mail_fee + 
               CommUtil.null2Float(goods.getMail_trans_fee());
 
             express_fee = express_fee + 
               CommUtil.null2Float(goods
               .getExpress_trans_fee());
 
             ems_fee = ems_fee + 
               CommUtil.null2Float(goods.getEms_trans_fee());
           }
         }
       }
       if ((mail_fee == 0.0F) && (express_fee == 0.0F) && (ems_fee == 0.0F)) {
         sms.add(new SysMap("卖家承担", Integer.valueOf(0)));
       } else {
         sms.add(new SysMap("平邮[" + mail_fee + "元]", Float.valueOf(mail_fee)));
         sms.add(new SysMap("快递[" + express_fee + "元]", Float.valueOf(express_fee)));
         sms.add(new SysMap("EMS[" + ems_fee + "元]", Float.valueOf(ems_fee)));
       }
     }
     return sms;
   }
 
   private float cal_order_trans(String trans_json, int trans_type, Object goods_weight, Object goods_volume, String city_name)
   {
     float fee = 0.0F;
     boolean cal_flag = false;
     List<Map> list = (List)Json.fromJson(ArrayList.class, trans_json);
     if ((list != null) && (list.size() > 0)) {
       for (Map map : list) {
         String[] city_list = CommUtil.null2String(map.get("city_name"))
           .split("、");
         for (String city : city_list)
         {
           if ((city.equals(city_name)) || (city_name.indexOf(city) == 0)) {
             cal_flag = true;
             float trans_weight = CommUtil.null2Float(map
               .get("trans_weight"));
             float trans_fee = CommUtil.null2Float(map
               .get("trans_fee"));
             float trans_add_weight = CommUtil.null2Float(map
               .get("trans_add_weight"));
             float trans_add_fee = CommUtil.null2Float(map
               .get("trans_add_fee"));
             if (trans_type == 0) {
               fee = trans_fee;
             }
             if ((trans_type == 1) && 
               (CommUtil.null2Float(goods_weight) > 0.0F)) {
               fee = trans_fee;
               float other_price = 0.0F;
               if (trans_add_weight > 0.0F) {
                 other_price = trans_add_fee * 
                   (float)Math.round(Math.ceil(
                   CommUtil.subtract(goods_weight, 
                   Float.valueOf(trans_weight)))) / 
                   trans_add_fee;
               }
               fee += other_price;
             }
 
             if ((trans_type != 2) || 
               (CommUtil.null2Float(goods_volume) <= 0.0F)) break;
             fee = trans_fee;
             float other_price = 0.0F;
             if (trans_add_weight > 0.0F) {
               other_price = trans_add_fee * 
                 (float)Math.round(Math.ceil(
                 CommUtil.subtract(goods_volume, 
                 Float.valueOf(trans_weight)))) / 
                 trans_add_fee;
             }
             fee += other_price;
 
             break;
           }
         }
       }
       if (!cal_flag) {
         for (Map map : list) {
           String[] city_list = CommUtil.null2String(
             map.get("city_name")).split("、");
           for (String city : city_list) {
             if (city.equals("全国")) {
               float trans_weight = CommUtil.null2Float(map
                 .get("trans_weight"));
               float trans_fee = CommUtil.null2Float(map
                 .get("trans_fee"));
               float trans_add_weight = CommUtil.null2Float(map
                 .get("trans_add_weight"));
               float trans_add_fee = CommUtil.null2Float(map
                 .get("trans_add_fee"));
               if (trans_type == 0) {
                 fee = trans_fee;
               }
               if ((trans_type == 1) && 
                 (CommUtil.null2Float(goods_weight) > 0.0F)) {
                 fee = trans_fee;
                 float other_price = 0.0F;
                 if (trans_add_weight > 0.0F) {
                   other_price = trans_add_fee * 
                     (float)Math.round(Math.ceil(
                     CommUtil.subtract(goods_weight, 
                     Float.valueOf(trans_weight)))) / 
                     trans_add_fee;
                 }
                 fee += other_price;
               }
 
               if ((trans_type != 2) || 
                 (CommUtil.null2Float(goods_volume) <= 0.0F)) break;
               fee = trans_fee;
               float other_price = 0.0F;
               if (trans_add_weight > 0.0F) {
                 other_price = trans_add_fee * 
                   (float)Math.round(Math.ceil(
                   CommUtil.subtract(goods_volume, 
                   Float.valueOf(trans_weight)))) / 
                   trans_add_fee;
               }
               fee += other_price;
 
               break;
             }
           }
         }
       }
     }
     return fee;
   }
 
   public static void main(String[] args) throws UnsupportedEncodingException
   {
     String s = "[{\"trans_weight\":1,\"trans_fee\":13.5,\"trans_add_weight\":1,\"trans_add_fee\":2},{\"city_id\":1,\"city_name\":\"沈阳\",\"trans_weight\":1,\"trans_fee\":13.5,\"trans_add_weight\":1,\"trans_add_fee\":2}]";
     List<Map> list = (List)Json.fromJson(ArrayList.class, s);
     for (Map map : list)
       System.out.println(map.get("city_id"));
   }
 }


 
 
 