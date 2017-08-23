 package com.shopping.foundation.domain.query;
 
 import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.query.QueryObject;
 
 public class IntegralGoodsOrderQueryObject extends QueryObject
 {
   public IntegralGoodsOrderQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType)
   {
     super(currentPage, mv, orderBy, orderType);
   }
 
   public IntegralGoodsOrderQueryObject()
   {
   }
 }



 
 