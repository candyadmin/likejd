package com.shopping.core.filter;

import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.SysLog;
import com.shopping.foundation.domain.User;
import com.shopping.foundation.service.ISysLogService;
import com.shopping.foundation.service.IUserService;
import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.ui.logout.LogoutFilter;
import org.springframework.security.ui.logout.LogoutHandler;

public class NorLogoutFilter extends LogoutFilter
{

  @Autowired
  private ISysLogService sysLogService;

  @Autowired
  private IUserService userService;

  public void saveLog(HttpServletRequest request)
  {
    HttpSession session = request.getSession(false);
    User u = (User)session.getAttribute("user");
    if (u != null) {
      User user = this.userService.getObjById(u.getId());
      user.setLastLoginDate((Date)session.getAttribute("lastLoginDate"));
      user.setLastLoginIp((String)session.getAttribute("loginIp"));
      this.userService.update(user);
      SysLog log = new SysLog();
      log.setAddTime(new Date());
      log.setContent(user.getTrueName() + "于" + 
        CommUtil.formatTime("yyyy-MM-dd HH:mm:ss", new Date()) + "退出系统");
      log.setTitle("用户退出");
      log.setType(0);
      log.setUser(user);
      log.setIp(CommUtil.getIpAddr(request));
      this.sysLogService.save(log);
    }
  }

  public NorLogoutFilter(String logoutSuccessUrl, LogoutHandler[] handlers) {
    super(logoutSuccessUrl, handlers);
  }

  public void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    throws IOException, ServletException
  {
    if (requiresLogout(request, response)) {
      HttpSession session = request.getSession(false);
      if (session != null) {
        saveLog(request);
      }
    }
    super.doFilterHttp(request, response, chain);
  }

  protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response)
  {
    return super.requiresLogout(request, response);
  }

  protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response)
  {
    return super.determineTargetUrl(request, response);
  }

  protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
    throws IOException
  {
    super.sendRedirect(request, response, url);
  }

  public void setFilterProcessesUrl(String filterProcessesUrl)
  {
    super.setFilterProcessesUrl(filterProcessesUrl);
  }

  protected String getLogoutSuccessUrl()
  {
    return super.getLogoutSuccessUrl();
  }

  protected String getFilterProcessesUrl()
  {
    return super.getFilterProcessesUrl();
  }

  public void setUseRelativeContext(boolean useRelativeContext)
  {
    super.setUseRelativeContext(useRelativeContext);
  }

  public int getOrder()
  {
    return super.getOrder();
  }
}