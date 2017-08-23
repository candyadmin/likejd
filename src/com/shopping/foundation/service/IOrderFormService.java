package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.OrderForm;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IOrderFormService
{
  public abstract boolean save(OrderForm paramOrderForm);

  public abstract OrderForm getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(OrderForm paramOrderForm);

  public abstract List<OrderForm> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 