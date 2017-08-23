package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Visit;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IVisitService
{
  public abstract boolean save(Visit paramVisit);

  public abstract Visit getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(Visit paramVisit);

  public abstract List<Visit> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 