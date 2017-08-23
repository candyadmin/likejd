package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.IntegralGoodsOrder;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IIntegralGoodsOrderService
{
  public abstract boolean save(IntegralGoodsOrder paramIntegralGoodsOrder);

  public abstract IntegralGoodsOrder getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(IntegralGoodsOrder paramIntegralGoodsOrder);

  public abstract List<IntegralGoodsOrder> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 