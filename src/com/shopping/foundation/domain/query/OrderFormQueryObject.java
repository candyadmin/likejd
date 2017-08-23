 package com.shopping.foundation.domain.query;
 
 import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.query.QueryObject;
 
 public class OrderFormQueryObject extends QueryObject
 {
   public OrderFormQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType)
   {
     super(currentPage, mv, orderBy, orderType);
   }
   public OrderFormQueryObject(String currentPage, Map map, String orderBy, String orderType)
   {
     super(currentPage, map, orderBy, orderType);
   }
   public OrderFormQueryObject()
   {
   }
 }



 
 