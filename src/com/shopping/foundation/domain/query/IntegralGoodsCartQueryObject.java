 package com.shopping.foundation.domain.query;
 
 import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.query.QueryObject;
 
 public class IntegralGoodsCartQueryObject extends QueryObject
 {
   public IntegralGoodsCartQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType)
   {
     super(currentPage, mv, orderBy, orderType);
   }
 
   public IntegralGoodsCartQueryObject()
   {
   }
 }



 
 