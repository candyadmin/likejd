 package com.shopping.foundation.domain.query;
 
 import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.query.QueryObject;
 
 public class FavoriteQueryObject extends QueryObject
 {
   public FavoriteQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType)
   {
     super(currentPage, mv, orderBy, orderType);
   }
 
   public FavoriteQueryObject()
   {
   }
 }



 
 