 package com.shopping.core.query;
 
 public class PageObject
 {
   private Integer currentPage = Integer.valueOf(-1);
 
   private Integer pageSize = Integer.valueOf(-1);
 
   public Integer getCurrentPage() {
    if (this.currentPage == null) {
       this.currentPage = Integer.valueOf(-1);
     }
    return this.currentPage;
   }
 
   public void setCurrentPage(Integer currentPage) {
    if (currentPage == null) {
      currentPage = Integer.valueOf(-1);
     }
     this.currentPage = currentPage;
   }
 
   public Integer getPageSize() {
     return this.pageSize;
   }
 
   public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
   }
 }

