package com.shopping.core.domain.virtual;


import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;

public class CglibBean
{
  public Object object = null;

  public BeanMap beanMap = null;
  @SuppressWarnings({ "rawtypes", "unused" })
private Map propertyMap;

  public CglibBean()
  {
  }

  @SuppressWarnings("rawtypes")
public CglibBean(Map propertyMap)
  {
    this.object = generateBean(propertyMap);
    this.beanMap = BeanMap.create(this.object);
    this.propertyMap = propertyMap;
  }

  public void setValue(String property, Object value)
  {
    this.beanMap.put(property, value);
  }

  public Object value(Object property)
  {
    if ((property != null) && (!property.equals(""))) {
      return this.beanMap.get(property);
    }
    return "";
  }

  public Object getObject()
  {
    return this.object;
  }

  @SuppressWarnings("rawtypes")
private Object generateBean(Map propertyMap)
  {
    BeanGenerator generator = new BeanGenerator();
    Set keySet = propertyMap.keySet();
    for (Iterator i = keySet.iterator(); i.hasNext(); ) {
      String key = (String)i.next();
      generator.addProperty(key, (Class)propertyMap.get(key));
    }
    return generator.create();
  }
}