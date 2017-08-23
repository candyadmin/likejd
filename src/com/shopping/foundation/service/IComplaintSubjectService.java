package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.ComplaintSubject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IComplaintSubjectService
{
  public abstract boolean save(ComplaintSubject paramComplaintSubject);

  public abstract ComplaintSubject getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(ComplaintSubject paramComplaintSubject);

  public abstract List<ComplaintSubject> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 