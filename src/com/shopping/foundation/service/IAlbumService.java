package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Album;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IAlbumService
{
  public abstract boolean save(Album paramAlbum);

  public abstract Album getObjById(Long paramLong);

  public abstract boolean delete(Long paramLong);

  public abstract boolean batchDelete(List<Serializable> paramList);

  public abstract IPageList list(IQueryObject paramIQueryObject);

  public abstract boolean update(Album paramAlbum);

  public abstract List<Album> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  public abstract Album getDefaultAlbum(Long paramLong);
}



 
 