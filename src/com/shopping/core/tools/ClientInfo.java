 package com.shopping.core.tools;
 
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 public class ClientInfo
 {
   private String info = "";
   private String explorerVer = "未知";
   private String OSVer = "未知";
 
   public ClientInfo(String info)
   {
     this.info = info;
   }
 
   public String getExplorerName()
   {
     String str = "未知";
     Pattern pattern = Pattern.compile("");
 
     if (this.info.indexOf("MSIE") != -1) {
       str = "MSIE";
       pattern = Pattern.compile(str + "\\s([0-9.]+)");
     } else if (this.info.indexOf("Firefox") != -1) {
       str = "Firefox";
       pattern = Pattern.compile(str + "\\/([0-9.]+)");
     } else if (this.info.indexOf("Chrome") != -1) {
       str = "Chrome";
       pattern = Pattern.compile(str + "\\/([0-9.]+)");
     } else if (this.info.indexOf("Opera") != -1) {
       str = "Opera";
       pattern = Pattern.compile("Version\\/([0-9.]+)");
     }
     Matcher matcher = pattern.matcher(this.info);
     if (matcher.find())
       this.explorerVer = matcher.group(1);
     return str;
   }
 
   public String getExplorerVer()
   {
     return this.explorerVer;
   }
 
   public String getExplorerPlug()
   {
     String str = "无";
     if (this.info.indexOf("Maxthon") != -1)
       str = "Maxthon";
     return str;
   }
 
   public String getOSName()
   {
     String str = "未知";
     Pattern pattern = Pattern.compile("");
 
     if (this.info.indexOf("Windows") != -1) {
       str = "Windows";
       pattern = Pattern.compile(str + "\\s([a-zA-Z0-9]+\\s[0-9.]+)");
     }
     Matcher matcher = pattern.matcher(this.info);
     if (matcher.find())
       this.OSVer = matcher.group(1);
     return str;
   }
 
   public String getOSVer()
   {
     return this.OSVer;
   }
 }
