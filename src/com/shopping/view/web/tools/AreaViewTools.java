 package com.shopping.view.web.tools;
 
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Area;
 import com.shopping.foundation.service.IAreaService;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 
 @Component
 public class AreaViewTools
 {
 
   @Autowired
   private IAreaService areaService;
 
   public String generic_area_info(String area_id)
   {
     String area_info = "";
     Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
     if (area != null) {
       area_info = area.getAreaName() + " ";
       if (area.getParent() != null) {
         String info = generic_area_info(area.getParent().getId()
           .toString());
         area_info = info + area_info;
       }
     }
     return area_info;
   }
 }


 
 
 