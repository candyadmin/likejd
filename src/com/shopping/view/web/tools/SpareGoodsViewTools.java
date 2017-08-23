 package com.shopping.view.web.tools;
 
 import com.shopping.foundation.domain.SpareGoodsClass;
 import com.shopping.foundation.domain.SpareGoodsFloor;
 import com.shopping.foundation.service.ISpareGoodsClassService;
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 
 @Component
 public class SpareGoodsViewTools
 {
 
   @Autowired
   private ISpareGoodsClassService sgcService;
 
   public List<SpareGoodsClass> query_childclass(SpareGoodsClass sgc)
   {
     List list = new ArrayList();
     for (SpareGoodsClass child : sgc.getChilds()) {
       list.add(child);
       for (SpareGoodsClass c : child.getChilds()) {
         list.add(c);
       }
     }
     return list;
   }
 
   public List<SpareGoodsClass> query_floorClass(SpareGoodsFloor sgf)
   {
     List list = new ArrayList();
     for (SpareGoodsClass child : sgf.getSgc().getChilds()) {
       if (child.isViewInFloor()) {
         list.add(child);
       }
       for (SpareGoodsClass c : child.getChilds()) {
         if (c.isViewInFloor()) {
           list.add(c);
         }
       }
     }
 
     return list;
   }
 
   public String ClearContent(String inputString)
   {
     String htmlStr = inputString;
     String textStr = "";
     try
     {
       String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>";
       String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>";
       String regEx_html = "<[^>]+>";
       String regEx_html1 = "<[^>]+";
       Pattern p_script = Pattern.compile(regEx_script, 2);
       Matcher m_script = p_script.matcher(htmlStr);
       htmlStr = m_script.replaceAll("");
 
       Pattern p_style = Pattern.compile(regEx_style, 2);
       Matcher m_style = p_style.matcher(htmlStr);
       htmlStr = m_style.replaceAll("");
 
       Pattern p_html = Pattern.compile(regEx_html, 2);
       Matcher m_html = p_html.matcher(htmlStr);
       htmlStr = m_html.replaceAll("");
 
       Pattern p_html1 = Pattern.compile(regEx_html1, 2);
       Matcher m_html1 = p_html1.matcher(htmlStr);
       htmlStr = m_html1.replaceAll("");
 
       textStr = htmlStr;
     } catch (Exception e) {
       System.err.println("Html2Text: " + e.getMessage());
     }
     return textStr;
   }
 }


 
 
 