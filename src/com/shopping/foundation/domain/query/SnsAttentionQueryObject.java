 package com.shopping.foundation.domain.query;
 
 import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.query.QueryObject;
 
 public class SnsAttentionQueryObject extends QueryObject
 {
   public SnsAttentionQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType)
   {
     super(currentPage, mv, orderBy, orderType);
   }
 
   public SnsAttentionQueryObject()
   {
   }
 }



 
 