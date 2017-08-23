package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.SnsAttention;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface ISnsAttentionService
{
  public abstract boolean save(SnsAttention paramSnsAttention);

  public abstract SnsAttention getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(SnsAttention paramSnsAttention);

  public abstract List<SnsAttention> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 