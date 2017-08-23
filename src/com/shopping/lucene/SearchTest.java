 package com.shopping.lucene;
 
 import java.util.Date;
 
 public class SearchTest
 {
   public static void main(String[] args)
   {
     LuceneUtil lucence = LuceneUtil.instance();
     LuceneUtil.setIndex_path("F:\\JAVA_PRO\\shopping\\luence\\goods");
     Date d1 = new Date();
     LuceneResult list = lucence.search("手提包", 0, 0.0D, 500.0D, null, null);
     Date d2 = new Date();
     System.out.println("查询时间为：" + (d2.getTime() - d1.getTime()) + "毫秒");
     for (int i = 0; i < list.getVo_list().size(); i++) {
       LuceneVo vo = (LuceneVo)list.getVo_list().get(i);
       System.out.println("标题：" + vo.getVo_title());
       System.out.println("价格:" + vo.getVo_store_price());
       System.out.println("添加时间:" + vo.getVo_add_time());
     }
     System.out.println("查询结果为:" + list.getVo_list().size());
   }
 }

