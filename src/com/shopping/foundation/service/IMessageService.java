package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Message;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IMessageService
{
  public abstract boolean save(Message paramMessage);

  public abstract Message getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(Message paramMessage);

  public abstract List<Message> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 