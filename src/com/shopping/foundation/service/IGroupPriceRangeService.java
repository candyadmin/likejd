package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.GroupPriceRange;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IGroupPriceRangeService
{
  public abstract boolean save(GroupPriceRange paramGroupPriceRange);

  public abstract GroupPriceRange getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(GroupPriceRange paramGroupPriceRange);

  public abstract List<GroupPriceRange> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 