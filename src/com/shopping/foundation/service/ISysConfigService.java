package com.shopping.foundation.service;

import com.shopping.foundation.domain.SysConfig;

public abstract interface ISysConfigService
{
  public abstract boolean save(SysConfig paramSysConfig);

  public abstract boolean delete(SysConfig paramSysConfig);

  public abstract boolean update(SysConfig paramSysConfig);

  public abstract SysConfig getSysConfig();
}



 
 