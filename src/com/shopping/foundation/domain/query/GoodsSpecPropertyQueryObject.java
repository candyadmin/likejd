 package com.shopping.foundation.domain.query;
 
 import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.query.QueryObject;
 
 public class GoodsSpecPropertyQueryObject extends QueryObject
 {
   public GoodsSpecPropertyQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType)
   {
     super(currentPage, mv, orderBy, orderType);
   }
 
   public GoodsSpecPropertyQueryObject()
   {
   }
 }



 
 