package com.shopping.core.ehcache;


import com.shopping.core.tools.CommUtil;
import java.util.Enumeration;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.constructs.web.AlreadyCommittedException;
import net.sf.ehcache.constructs.web.AlreadyGzippedException;
import net.sf.ehcache.constructs.web.filter.FilterNonReentrantException;
import net.sf.ehcache.constructs.web.filter.SimplePageFragmentCachingFilter;
import org.apache.commons.lang.StringUtils;

public class PageCacheFiler extends SimplePageFragmentCachingFilter
{
  private static final String FILTER_URL_PATTERNS = "patterns";
  private static String[] cacheURLs;

  private void init()
    throws CacheException
  {
    String patterns = this.filterConfig.getInitParameter("patterns");
    patterns = "/head.htm,/floor.htm,/advert_invoke.htm,public.css,index.css,goods.css,integral.css,user.css,window.css,jquery-1.6.2.js,jquery.lazyload.js,jquery-ui-1.8.21.js,jquery.shop.common.js,jquery.validate.min.js,jquery.jqzoom-core.js,jquery.metadata.js,jquery.rating.pack.js";
    cacheURLs = StringUtils.split(patterns, ",");
  }

  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    throws AlreadyGzippedException, AlreadyCommittedException, FilterNonReentrantException, LockTimeoutException, Exception
  {
    if (cacheURLs == null) {
      init();
    }
    String url = request.getRequestURI();
    String include_url = CommUtil.null2String(request
      .getAttribute("javax.servlet.include.request_uri"));
    boolean flag = false;
    if ((cacheURLs != null) && (cacheURLs.length > 0)) {
      for (String cacheURL : cacheURLs) {
        if (include_url.trim().equals("")) {
          if (url.contains(cacheURL.trim())) {
            flag = true;
            break;
          }
        }
        else if (include_url.contains(cacheURL.trim())) {
          flag = true;
          break;
        }

      }

    }

    if (flag)
      super.doFilter(request, response, chain);
    else
      chain.doFilter(request, response);
  }

  private boolean headerContains(HttpServletRequest request, String header, String value)
  {
    logRequestHeaders(request);
    Enumeration accepted = request.getHeaders(header);
    while (accepted.hasMoreElements()) {
      String headerValue = (String)accepted.nextElement();
      if (headerValue.indexOf(value) != -1) {
        return true;
      }
    }
    return false;
  }

  protected boolean acceptsGzipEncoding(HttpServletRequest request)
  {
    boolean ie6 = headerContains(request, "User-Agent", "MSIE 6.0");
    boolean ie7 = headerContains(request, "User-Agent", "MSIE 7.0");
    return (acceptsEncoding(request, "gzip")) || (ie6) || (ie7);
  }

  protected String calculateKey(HttpServletRequest httpRequest)
  {
    String url = httpRequest.getRequestURI();
    String include_url = CommUtil.null2String(httpRequest
      .getAttribute("javax.servlet.include.request_uri"));
    StringBuffer stringBuffer = new StringBuffer();
    if (include_url.equals("")) {
      stringBuffer.append(httpRequest.getRequestURI()).append(
        httpRequest.getQueryString());
      String key = stringBuffer.toString();
      return key;
    }
    stringBuffer
      .append(
      CommUtil.null2String(httpRequest
      .getAttribute("javax.servlet.include.request_uri")))
      .append(
      CommUtil.null2String(httpRequest
      .getAttribute("javax.serlvet.include.query_string")));
    String key = stringBuffer.toString();
    return key;
  }
}