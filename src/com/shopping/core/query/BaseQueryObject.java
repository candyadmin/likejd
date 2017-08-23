package com.shopping.core.query;

import org.springframework.web.servlet.ModelAndView;

public class BaseQueryObject extends QueryObject
{
  public BaseQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType)
  {
    super(currentPage, mv, orderBy, orderType);
  }

  public String getQuery()
  {
    return super.getQuery();
  }
}