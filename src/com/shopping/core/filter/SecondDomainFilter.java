package com.shopping.core.filter;


import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.SysConfig;
import com.shopping.foundation.domain.User;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.IUserService;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecondDomainFilter
  implements Filter
{

  @Autowired
  private IUserService userService;

  @Autowired
  private ISysConfigService configService;

  public void destroy()
  {
  }

  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException
  {
    HttpServletRequest request = (HttpServletRequest)req;
    HttpServletResponse response = (HttpServletResponse)res;
    if (this.configService.getSysConfig().isSecond_domain_open())
    {
      Cookie[] cookies = request.getCookies();
      String id = "";
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (cookie.getName().equals("shopping_user_session")) {
            id = CommUtil.null2String(cookie.getValue());
          }
        }
        User user = this.userService.getObjById(CommUtil.null2Long(id));
        if (user != null)
          request.getSession(false).setAttribute("user", user);
      }
    }
    chain.doFilter(req, res);
  }

  public void init(FilterConfig config)
    throws ServletException
  {
  }
}