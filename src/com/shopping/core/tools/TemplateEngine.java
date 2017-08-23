 package com.shopping.core.tools;
 
 import java.io.StringWriter;
 import java.util.Map;
 import org.apache.velocity.app.VelocityEngine;
 import org.apache.velocity.exception.VelocityException;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 import org.springframework.ui.velocity.VelocityEngineUtils;
 
 @Component
 public class TemplateEngine
 {
 
   @Autowired
   private VelocityEngine velocityEngine;
 
   public String generateWithTemplate(String templateName, Map map)
   {
     try
     {
       return VelocityEngineUtils.mergeTemplateIntoString(
         this.velocityEngine, templateName, "UTF-8", map);
     } catch (VelocityException e) {
       e.printStackTrace();
     }
     return "";
   }
 
   public String generateWithString(String content, Map map) {
     try {
       StringWriter writer = new StringWriter();
       VelocityEngineUtils.mergeTemplate(this.velocityEngine, content, 
         map, writer);
       return writer.toString();
     } catch (VelocityException e) {
       e.printStackTrace();
     }
     return "";
   }
 }
