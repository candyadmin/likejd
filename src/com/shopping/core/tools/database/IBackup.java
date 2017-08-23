package com.shopping.core.tools.database;

import java.sql.ResultSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public abstract interface IBackup
{
  public abstract boolean createSqlScript(HttpServletRequest paramHttpServletRequest, String paramString1, String paramString2, String paramString3, String paramString4)
    throws Exception;

  public abstract boolean executSqlScript(String paramString)
    throws Exception;

  public abstract List<String> getTables()
    throws Exception;

  public abstract String queryDatabaseVersion();

  public abstract boolean execute(String paramString);

  public abstract boolean export(String paramString1, String paramString2);

  public abstract ResultSet query(String paramString);
}
