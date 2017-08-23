package com.shopping.core.domain.virtual;


public class SysMap
{
  private Object key;
  private Object value;

  public SysMap()
  {
  }

  public SysMap(Object key, Object value)
  {
    this.key = key;
    this.value = value;
  }

  public Object getKey() {
    return this.key;
  }

  public void setKey(Object key) {
    this.key = key;
  }

  public Object getValue() {
    return this.value;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}