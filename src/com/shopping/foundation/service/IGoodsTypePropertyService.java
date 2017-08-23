package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.GoodsTypeProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IGoodsTypePropertyService
{
  public abstract boolean save(GoodsTypeProperty paramGoodsTypeProperty);

  public abstract GoodsTypeProperty getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(GoodsTypeProperty paramGoodsTypeProperty);

  public abstract List<GoodsTypeProperty> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 