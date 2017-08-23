package com.shopping.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface SecurityMapping
{
  public abstract String title();

  public abstract String value();

  public abstract String rname();

  public abstract String rcode();

  public abstract int rsequence();

  public abstract String rgroup();

  public abstract String rtype();

  public abstract boolean display();
}