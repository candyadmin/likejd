package com.shopping.core.service;

import java.util.List;
import java.util.Map;

public abstract interface IQueryService
{
  public abstract List query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}
