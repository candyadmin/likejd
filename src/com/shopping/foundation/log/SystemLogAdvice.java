 package com.shopping.foundation.log;
 
 import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shopping.core.annotation.Log;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.foundation.domain.LogFieldType;
import com.shopping.foundation.domain.LogType;
import com.shopping.foundation.domain.SysLog;
import com.shopping.foundation.service.IAccessoryService;
import com.shopping.foundation.service.IRoleService;
import com.shopping.foundation.service.ISysLogService;
import com.shopping.foundation.service.IUserService;
 
 @Aspect
 @Component
 public class SystemLogAdvice
 {
 
   @Autowired
   private ISysLogService sysLogService;
 
   @Autowired
   private IRoleService roleService;
 
   @Autowired
   private IUserService userSerivce;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @AfterReturning("execution(* com.wonders.manage..*.*(..))&& @annotation(annotation)&&args(request,..) ")
   public void userLog(JoinPoint joinPoint, Log annotation, HttpServletRequest request)
     throws Exception
   {
   }
 
   private void saveLog(JoinPoint joinPoint, Log annotation, HttpServletRequest request)
     throws Exception
   {
     String title = annotation.title();
 
     String methodName = joinPoint.getSignature().getName();
 
     String description = SecurityUserHolder.getCurrentUser().getTrueName() + 
       "于" + CommUtil.formatTime("yyyy-MM-dd HH:mm:ss", new Date());
     if (annotation.type().equals(LogType.LOGIN)) {
       description = description + "登录系统";
     }
     if (annotation.type().equals(LogType.LIST)) {
       description = description + "查阅" + annotation.entityName();
     }
     if (annotation.type().equals(LogType.REMOVE)) {
       String mulitId = request.getParameter("mulitId");
       String[] ids = mulitId.split(",");
       if (ids.length > 1)
         description = description + "批量删除" + annotation.entityName() + 
           "数据，Id为：" + mulitId;
       else
         description = description + "删除Id为：" + mulitId + "的" + 
           annotation.entityName();
     }
     if (annotation.type().equals(LogType.SAVE)) {
       String id = request.getParameter("id");
       if (id.equals(""))
         description = description + "新建并保存" + annotation.entityName();
       else {
         description = description + "修改并更新Id为：" + id + "的" + 
           annotation.entityName();
       }
     }
     if (annotation.type().equals(LogType.VIEW)) {
       String id = request.getParameter("id");
       description = description + "查阅Id为:" + id + "的" + 
         annotation.entityName();
     }
     if (annotation.type().equals(LogType.RESTORE)) {
       String id = request.getParameter("id");
       description = description + "还原系统数据为Id是" + id + "的备份数据";
     }
     if (annotation.type().equals(LogType.IMPORT)) {
       description = description + "从本地导入数据覆盖系统数据库";
     }
     if (annotation.type().equals(LogType.UPDATEPWS)) {
       String id = request.getParameter("id");
       String pws = request.getParameter("pws");
       description = description + "修改密码为" + pws.substring(0, 1) + "*****";
     }
     if (annotation.type().equals(LogType.SEND)) {
       String toUser = request.getParameter("toUser");
       description = description + "向" + toUser + "发送站内短信息";
     }
 
     SysLog log = new SysLog();
     log.setTitle(title);
     log.setType(0);
     log.setAddTime(new Date());
     log.setUser(SecurityUserHolder.getCurrentUser());
     log.setContent(description);
     log.setIp(CommUtil.getIpAddr(request));
     this.sysLogService.save(log);
   }
 
   @AfterThrowing(value="execution(* com.wonders.manage..*.*(..))&&args(request,..) ", throwing="exception")
   public void exceptionLog(HttpServletRequest request, Throwable exception)
   {
   }
 
   public void loginLog()
   {
     System.out.println("用户登录");
   }
 
   private Method getMethod(ProceedingJoinPoint joinPoint)
   {
     MethodSignature joinPointObject = (MethodSignature)joinPoint
       .getSignature();
 
     Method method = joinPointObject.getMethod();
 
     String name = method.getName();
     Class[] parameterTypes = method.getParameterTypes();
 
     Object target = joinPoint.getTarget();
     try
     {
       method = target.getClass().getMethod(name, parameterTypes);
     }
     catch (SecurityException e) {
       method = null;
       e.printStackTrace();
     }
     catch (NoSuchMethodException e) {
       e.printStackTrace();
     }
     return method;
   }
 
   public String adminOptionContent(Object[] args, String mName)
     throws Exception
   {
     if (args == null) {
       return null;
     }
     StringBuffer rs = new StringBuffer();
     rs.append(mName);
     String className = null;
     int index = 1;
 
     for (Object info : args)
     {
       className = info.getClass().getName();
       className = className.substring(className.lastIndexOf(".") + 1);
       boolean cal = false;
       LogFieldType[] types = LogFieldType.values();
       for (LogFieldType type : types) {
         if (type.toString().equals(className)) {
           cal = true;
         }
       }
       if (cal) {
         rs.append("[参数" + index + "，类型：" + className + "，值：");
 
         Method[] methods = info.getClass().getDeclaredMethods();
 
         for (Method method : methods) {
           String methodName = method.getName();
 
           if (methodName.indexOf("get") == -1) {
             continue;
           }
           Object rsValue = null;
           try
           {
             rsValue = method.invoke(info, new Object[0]);
             if (rsValue != null);
           }
           catch (Exception e)
           {
           }
           rs.append("(" + methodName + " : " + rsValue.toString() + 
             ")");
         }
         rs.append("]");
         index++;
       }
     }
     return rs.toString();
   }
 }



 
 