 package com.shopping.lucene;
 
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.Store;
 import java.util.ArrayList;
 import java.util.List;
 
 public class LuceneResult
 {
   private List<LuceneVo> vo_list = new ArrayList();
   private int pages;
   private int rows;
   private int currentPage;
   private int pageSize;
   private List<Goods> goods_list = new ArrayList();
   private List<Store> store_list = new ArrayList();
 
   public int getPages() {
     return this.pages;
   }
 
   public void setPages(int pages) {
     this.pages = pages;
   }
 
   public int getRows() {
     return this.rows;
   }
 
   public void setRows(int rows) {
     this.rows = rows;
   }
 
   public int getCurrentPage() {
     return this.currentPage;
   }
 
   public void setCurrentPage(int currentPage) {
     this.currentPage = currentPage;
   }
 
   public int getPageSize() {
     return this.pageSize;
   }
 
   public void setPageSize(int pageSize) {
     this.pageSize = pageSize;
   }
 
   public List<LuceneVo> getVo_list() {
     return this.vo_list;
   }
 
   public void setVo_list(List<LuceneVo> vo_list) {
     this.vo_list = vo_list;
   }
 
   public List<Goods> getGoods_list() {
     return this.goods_list;
   }
 
   public void setGoods_list(List<Goods> goods_list) {
     this.goods_list = goods_list;
   }
 
   public List<Store> getStore_list() {
     return this.store_list;
   }
 
   public void setStore_list(List<Store> store_list) {
     this.store_list = store_list;
   }
 }

