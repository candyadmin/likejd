package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.StoreClass;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IStoreClassService
{
  public abstract boolean save(StoreClass paramStoreClass);

  public abstract StoreClass getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(StoreClass paramStoreClass);

  public abstract List<StoreClass> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 