 package com.shopping.core.security.support;
 
 import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.User;
 
 public class SecurityUserHolder
 {
   public static User getCurrentUser()
   {
     if (SecurityContextHolder.getContext().getAuthentication() != null)
     {
       if ((SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User))
       {
         return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       }
     }
     User user = null;
     if (RequestContextHolder.getRequestAttributes() != null) {
       HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
       HttpSession  session = request.getSession(false);
       if(session!=null)
    	   user = session.getAttribute("user") != null ?(User)session.getAttribute("user") : null;
 
       Cookie[] cookies = request.getCookies();
       String id = "";
       if (cookies != null) {
         for (Cookie cookie : cookies)
         {
           if (cookie.getName().equals("shopping_user_session")) {
             id = CommUtil.null2String(cookie.getValue());
           }
         }
       }
       if (id.equals("")) {
         user = null;
       }
     }
 
     return user;
   }
 }

