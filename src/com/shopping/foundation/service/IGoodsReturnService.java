package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.GoodsReturn;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IGoodsReturnService
{
  public abstract boolean save(GoodsReturn paramGoodsReturn);

  public abstract GoodsReturn getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(GoodsReturn paramGoodsReturn);

  public abstract List<GoodsReturn> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 