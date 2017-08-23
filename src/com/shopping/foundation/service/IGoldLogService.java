package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.GoldLog;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IGoldLogService
{
  public abstract boolean save(GoldLog paramGoldLog);

  public abstract GoldLog getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(GoldLog paramGoldLog);

  public abstract List<GoldLog> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 