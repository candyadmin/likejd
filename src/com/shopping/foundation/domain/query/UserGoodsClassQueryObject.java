 package com.shopping.foundation.domain.query;
 
 import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.query.QueryObject;
 
 public class UserGoodsClassQueryObject extends QueryObject
 {
   public UserGoodsClassQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType)
   {
     super(currentPage, mv, orderBy, orderType);
   }
 
   public UserGoodsClassQueryObject()
   {
   }
 }



 
 