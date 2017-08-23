package com.shopping.core.query;

import com.shopping.core.dao.IGenericDAO;
import com.shopping.core.query.support.IQuery;
import com.shopping.core.query.support.IQueryObject;
import java.util.Map;

public class GenericPageList extends PageList
{
  private static final long serialVersionUID = 6730593239674387757L;
  protected String scope;
  protected Class cls;

  public GenericPageList(Class cls, IQueryObject queryObject, IGenericDAO dao)
  {
    this(cls, queryObject.getQuery(), queryObject.getParameters(), dao);
  }

  public GenericPageList(Class cls, String scope, Map paras, IGenericDAO dao) {
    this.cls = cls;
    this.scope = scope;
    IQuery query = new GenericQuery(dao);
    query.setParaValues(paras);
    setQuery(query);
  }

  public void doList(int currentPage, int pageSize)
  {
    String totalSql = "select COUNT(obj) from " + this.cls.getName() + " obj where " + 
      this.scope;
    super.doList(pageSize, currentPage, totalSql, this.scope);
  }
}