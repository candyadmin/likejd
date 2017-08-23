 package com.shopping.core.tools;
 
 import java.util.Locale;
 
 public class LocalManager
 {
   private static Locale defaultLocal = Locale.getDefault();
   private static ThreadLocal<Locale> locale;
   private static ThreadLocal<Locale> customLocale;
 
   public static Locale getCurrentLocal()
   {
     return locale != null ? (Locale)locale.get() : Locale.getDefault();
   }
 
   public static void setLocale(Locale newLocale)
   {
     if (locale == null) locale = new ThreadLocal();
     locale.set(newLocale);
   }
 
   public static void setCustomLocale(Locale newLocale) {
     if (customLocale == null) customLocale = new ThreadLocal();
     customLocale.set(newLocale);
   }
   public static Locale getDefaultLocal() {
     return defaultLocal;
   }
   public static void setDefaultLocal(Locale defaultLocal) {
     defaultLocal = defaultLocal;
   }
 }

