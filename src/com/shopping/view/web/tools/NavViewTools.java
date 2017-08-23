 package com.shopping.view.web.tools;
 
 import com.shopping.foundation.domain.Navigation;
 import com.shopping.foundation.service.IActivityService;
 import com.shopping.foundation.service.IArticleService;
 import com.shopping.foundation.service.IGoodsClassService;
 import com.shopping.foundation.service.INavigationService;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 
 @Component
 public class NavViewTools
 {
 
   @Autowired
   private INavigationService navService;
 
   @Autowired
   private IArticleService articleService;
 
   @Autowired
   private IActivityService activityService;
 
   @Autowired
   private IGoodsClassService goodsClassService;
 
   public List<Navigation> queryNav(int location, int count)
   {
     List navs = new ArrayList();
     Map params = new HashMap();
     params.put("display", Boolean.valueOf(true));
     params.put("location", Integer.valueOf(location));
     params.put("type", "sparegoods");
     navs = this.navService
       .query("select obj from Navigation obj where obj.display=:display and obj.location=:location and obj.type!=:type order by obj.sequence asc", params, 0, count);
     return navs;
   }
 }


 
 
 