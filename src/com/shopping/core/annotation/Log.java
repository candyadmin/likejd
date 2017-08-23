package com.shopping.core.annotation;


import com.shopping.foundation.domain.LogType;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Log
{
  public abstract String title();

  public abstract String entityName();

  public abstract LogType type();

  public abstract String description();

  public abstract String ip();
}