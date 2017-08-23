package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.OrderFormLog;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IOrderFormLogService
{
  public abstract boolean save(OrderFormLog paramOrderFormLog);

  public abstract OrderFormLog getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(OrderFormLog paramOrderFormLog);

  public abstract List<OrderFormLog> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 