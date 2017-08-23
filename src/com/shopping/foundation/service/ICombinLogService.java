package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.CombinLog;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface ICombinLogService
{
  public abstract boolean save(CombinLog paramCombinLog);

  public abstract CombinLog getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(CombinLog paramCombinLog);

  public abstract List<CombinLog> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 