package com.shopping.core.mv;

import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.HttpInclude;
import com.shopping.foundation.domain.SysConfig;
import com.shopping.foundation.domain.UserConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

public class JModelAndView extends ModelAndView
{
  public JModelAndView(String viewName)
  {
    super.setViewName(viewName);
  }

  public JModelAndView(String viewName, SysConfig config, UserConfig uconfig, HttpServletRequest request, HttpServletResponse response)
  {
    String contextPath = request.getContextPath().equals("/") ? "" : 
      request.getContextPath();
    String webPath = CommUtil.getURL(request);
    String port = ":" + 
      CommUtil.null2Int(Integer.valueOf(request.getServerPort()));
    if ((config.isSecond_domain_open()) && 
      (!CommUtil.generic_domain(request).equals("localhost"))) {
      webPath = "http://www." + CommUtil.generic_domain(request) + port + 
        contextPath;
    }
    super.setViewName(viewName);
    super.addObject("domainPath", CommUtil.generic_domain(request));
    if ((config.getImageWebServer() != null) && 
      (!config.getImageWebServer().equals("")))
      super.addObject("imageWebServer", config.getImageWebServer());
    else {
      super.addObject("imageWebServer", webPath);
    }
    super.addObject("webPath", webPath);
    super.addObject("config", config);
    super.addObject("uconfig", uconfig);
    super.addObject("user", SecurityUserHolder.getCurrentUser());
    super.addObject("httpInclude", new HttpInclude(request, response));
    String query_url = "";
    if ((request.getQueryString() != null) && 
      (!request.getQueryString().equals(""))) {
      query_url = "?" + request.getQueryString();
    }
    super.addObject("current_url", request.getRequestURI() + query_url);
    boolean second_domain_view = false;
    String serverName = request.getServerName().toLowerCase();
    
    if ((serverName.indexOf("www.") < 0) && (serverName.indexOf(".") >= 0) && 
      //(serverName.indexOf("localhost") < 0) && !serverName.startsWith("1") && 
      (serverName.indexOf(".") != serverName.lastIndexOf(".")) && 
      (config.isSecond_domain_open())) {
      String secondDomain = serverName.substring(0, serverName.indexOf("."));
      second_domain_view = true;
      super.addObject("secondDomain", secondDomain);
    }
    super.addObject("second_domain_view", Boolean.valueOf(second_domain_view));
  }

  public JModelAndView(String viewName, SysConfig config, UserConfig uconfig, int type, HttpServletRequest request, HttpServletResponse response)
  {
    if (config.getSysLanguage() != null) {
      if (config.getSysLanguage().equals("zh_cn")) {
        if (type == 0) {
          super.setViewName("WEB-INF/templates/zh_cn/system/" + viewName);
        }
        if (type == 1) {
          super.setViewName("WEB-INF/templates/zh_cn/shop/" + viewName);
        }
        if (type > 1)
          super.setViewName(viewName);
      }
      else {
        if (type == 0) {
          super.setViewName("WEB-INF/templates/" + 
            config.getSysLanguage() + "/system/" + viewName);
        }
        if (type == 1) {
          super.setViewName("WEB-INF/templates/" + 
            config.getSysLanguage() + "/shop/" + viewName);
        }
        if (type > 1)
          super.setViewName(viewName);
      }
    }
    else {
      super.setViewName(viewName);
    }
    super.addObject("CommUtil", new CommUtil());
    String contextPath = request.getContextPath().equals("/") ? "" : 
      request.getContextPath();
    String webPath = CommUtil.getURL(request);
    String port = ":" + 
      CommUtil.null2Int(Integer.valueOf(request.getServerPort()));
    if ((config.isSecond_domain_open()) && 
      (!CommUtil.generic_domain(request).equals("localhost"))) {
      webPath = "http://www." + CommUtil.generic_domain(request) + port + 
        contextPath;
    }
    super.addObject("domainPath", CommUtil.generic_domain(request));
    super.addObject("webPath", webPath);
    if ((config.getImageWebServer() != null) && 
      (!config.getImageWebServer().equals("")))
      super.addObject("imageWebServer", config.getImageWebServer());
    else {
      super.addObject("imageWebServer", webPath);
    }
    super.addObject("config", config);
    super.addObject("uconfig", uconfig);
    super.addObject("user", SecurityUserHolder.getCurrentUser());
    super.addObject("httpInclude", new HttpInclude(request, response));
    String query_url = "";
    if ((request.getQueryString() != null) && 
      (!request.getQueryString().equals(""))) {
      query_url = "?" + request.getQueryString();
    }
    super.addObject("current_url", request.getRequestURI() + query_url);
    boolean second_domain_view = false;
    String serverName = request.getServerName().toLowerCase();
    
    if ((serverName.indexOf("www.") < 0) && (serverName.indexOf(".") >= 0) && 
      //(serverName.indexOf("localhost") < 0) && !serverName.startsWith("1") &&
      (serverName.indexOf(".") != serverName.lastIndexOf(".")) && 
      (config.isSecond_domain_open())) {
      String secondDomain = serverName.substring(0, serverName.indexOf("."));
      second_domain_view = true;
      super.addObject("secondDomain", secondDomain);
    }
    super.addObject("second_domain_view", Boolean.valueOf(second_domain_view));
  }
}