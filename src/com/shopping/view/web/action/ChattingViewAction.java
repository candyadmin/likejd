 package com.shopping.view.web.action;
 
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Chatting;
 import com.shopping.foundation.domain.ChattingFriend;
 import com.shopping.foundation.domain.ChattingLog;
 import com.shopping.foundation.domain.SnsFriend;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.query.ChattingLogQueryObject;
 import com.shopping.foundation.service.IChattingFriendService;
 import com.shopping.foundation.service.IChattingLogService;
 import com.shopping.foundation.service.IChattingService;
 import com.shopping.foundation.service.ISnsFriendService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import com.shopping.manage.admin.tools.UserTools;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class ChattingViewAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private ISnsFriendService snsFriendService;
 
   @Autowired
   private UserTools userTools;
 
   @Autowired
   private IChattingFriendService chattingFriendService;
 
   @Autowired
   private IChattingLogService chattinglogService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IChattingService chattingService;
 
   @RequestMapping({"/chatting.htm"})
   public ModelAndView chatting(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView("chatting.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
 
     Map params = new HashMap();
     params.put("uid", SecurityUserHolder.getCurrentUser().getId());
     List<SnsFriend> Friends = this.snsFriendService.query(
       "select obj from SnsFriend obj where obj.fromUser.id=:uid ", 
       params, -1, -1);
     mv.addObject("Friends", Friends);
     mv.addObject("userTools", this.userTools);
 
     if (Friends.size() > 0) {
       int count = 0;
       for (SnsFriend friend : Friends) {
         boolean flag = this.userTools.userOnLine(friend.getToUser()
           .getUserName());
         if (flag) {
           count++;
         }
         mv.addObject("OnlineCount", Integer.valueOf(count));
       }
     }
 
     params.clear();
     params.put("uid", SecurityUserHolder.getCurrentUser().getId());
     List Contactings = this.chattingFriendService
       .query(
       "select obj from ChattingFriend obj where obj.user.id=:uid order by addTime desc", 
       params, 0, 15);
     mv.addObject("Contactings", Contactings);
 
     params.clear();
     params.put("mark", Integer.valueOf(0));
     params.put("uid", SecurityUserHolder.getCurrentUser().getId());
     List unreads = this.chattinglogService
       .query(
       "select obj from ChattingLog obj where obj.chatting.user1.id=:uid and obj.mark=:mark or obj.chatting.user2.id=:uid and obj.mark=:mark ", 
       params, -1, -1);
     mv.addObject("unreads", unreads);
     Object list = new ArrayList();
     for (int i = 1; i <= 60; i++) {
       ((List)list).add(Integer.valueOf(i));
     }
     mv.addObject("emoticons", list);
     return (ModelAndView)mv;
   }
 
   @RequestMapping({"/chatting_refresh.htm"})
   public ModelAndView chatting_refresh(HttpServletRequest request, HttpServletResponse response, String user_id)
   {
     ModelAndView mv = new JModelAndView("chatting_logs.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     Chatting chatting = null;
     User user = this.userService.getObjById(CommUtil.null2Long(user_id));
     if ((SecurityUserHolder.getCurrentUser() != null) && 
       (!SecurityUserHolder.getCurrentUser().equals(""))) {
       Map map = new HashMap();
       map.put("uid", SecurityUserHolder.getCurrentUser().getId());
       map.put("user_id", CommUtil.null2Long(user_id));
       List chattings = this.chattingService
         .query(
         "select obj from Chatting obj where obj.user1.id=:uid and obj.user2.id=:user_id or obj.user1.id=:user_id and obj.user2.id=:uid", 
         map, -1, -1);
       if (chattings.size() > 0) {
         chatting = (Chatting)chattings.get(0);
 
         map.clear();
         map.put("chat_id", chatting.getId());
         map.put("mark", Integer.valueOf(0));
         map.put("user_id", CommUtil.null2Long(user_id));
         List<ChattingLog> logs = this.chattinglogService
           .query(
           "select obj from ChattingLog obj where obj.chatting.id=:chat_id and obj.mark=:mark and obj.user.id=:user_id order by addTime asc", 
           map, -1, -1);
         mv.addObject("logs", logs);
         for (ChattingLog log : logs) {
           if (log.getUser().getId() == 
             SecurityUserHolder.getCurrentUser().getId()) continue;
           log.setMark(1);
           this.chattinglogService.update(log);
         }
 
       }
 
     }
 
     return mv;
   }
 
   @RequestMapping({"/chatting_ShowHistory.htm"})
   public ModelAndView chatting_ShowHistory(HttpServletRequest request, HttpServletResponse response, String user_id, String currentPage)
   {
     ModelAndView mv = new JModelAndView("chatting_logs.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     Chatting chatting = null;
     if ((SecurityUserHolder.getCurrentUser() != null) && 
       (!SecurityUserHolder.getCurrentUser().equals(""))) {
       Map map = new HashMap();
       map.put("uid", SecurityUserHolder.getCurrentUser().getId());
       map.put("user_id", CommUtil.null2Long(user_id));
       List chattings = this.chattingService
         .query(
         "select obj from Chatting obj where obj.user1.id=:uid and obj.user2.id=:user_id or obj.user1.id=:user_id and obj.user2.id=:uid", 
         map, -1, -1);
       if (chattings.size() > 0) {
         chatting = (Chatting)chattings.get(0);
 
         ChattingLogQueryObject qo = new ChattingLogQueryObject(
           currentPage, mv, null, null);
         qo.addQuery("obj.chatting.id", 
           new SysMap("chatting_id", 
           chatting.getId()), "=");
         qo.setOrderBy("addTime");
         qo.setOrderType("desc");
         qo.setPageSize(Integer.valueOf(10));
         IPageList pList = this.chattinglogService.list(qo);
 
         mv.addObject("historys", pList.getResult());
         String Ajax_url = CommUtil.getURL(request) + 
           "/chatting_ShowHistory.htm";
         mv
           .addObject("gotoPageAjaxHTML", 
           CommUtil.showPageAjaxHtml(Ajax_url, "", pList
           .getCurrentPage(), pList.getPages()));
       }
     }
     return mv;
   }
 
   @RequestMapping({"/chatting_save.htm"})
   public ModelAndView chatting_save(HttpServletRequest request, HttpServletResponse response, String user_id, String content)
   {
     ModelAndView mv = new JModelAndView("chatting_logs.html", this.configService
       .getSysConfig(), this.userConfigService.getUserConfig(), 1, 
       request, response);
     Chatting chatting = null;
     User user = this.userService.getObjById(CommUtil.null2Long(user_id));
     Map map = new HashMap();
     map.put("uid", SecurityUserHolder.getCurrentUser().getId());
     map.put("user_id", CommUtil.null2Long(user_id));
     List chattings = this.chattingService
       .query(
       "select obj from Chatting obj where obj.user1.id=:uid and obj.user2.id=:user_id or obj.user1.id=:user_id and obj.user2.id=:uid", 
       map, -1, -1);
     if (chattings.size() > 0) {
       chatting = (Chatting)chattings.get(0);
     } else {
       chatting = new Chatting();
       chatting.setAddTime(new Date());
       chatting.setUser1(SecurityUserHolder.getCurrentUser());
       chatting.setUser2(user);
       this.chattingService.save(chatting);
     }
     ChattingLog log = new ChattingLog();
     log.setAddTime(new Date());
     log.setUser(SecurityUserHolder.getCurrentUser());
     log.setContent(content);
     log.setChatting(chatting);
     this.chattinglogService.save(log);
 
     map.clear();
     map.put("uid", SecurityUserHolder.getCurrentUser().getId());
     map.put("user_id", CommUtil.null2Long(user_id));
     List ChattingFriends = this.chattingFriendService
       .query(
       "select obj from ChattingFriend obj where obj.user.id=:uid and obj.friendUser.id=:user_id", 
       map, -1, -1);
     if (ChattingFriends.size() == 0) {
       ChattingFriend contact = new ChattingFriend();
       contact.setAddTime(new Date());
       contact.setUser(SecurityUserHolder.getCurrentUser());
       contact.setFriendUser(user);
       this.chattingFriendService.save(contact);
     }
 
     map.clear();
     map.put("uid", CommUtil.null2Long(user_id));
     map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
     List ChattingFriends2 = this.chattingFriendService
       .query(
       "select obj from ChattingFriend obj where obj.user.id=:uid and obj.friendUser.id=:user_id", 
       map, -1, -1);
     if (ChattingFriends2.size() == 0) {
       ChattingFriend contact = new ChattingFriend();
       contact.setAddTime(new Date());
       contact.setUser(user);
       contact.setFriendUser(SecurityUserHolder.getCurrentUser());
       this.chattingFriendService.save(contact);
     }
 
     map.clear();
     map.put("chat_id", chatting.getId());
     map.put("uid", SecurityUserHolder.getCurrentUser().getId());
     List logs = this.chattinglogService
       .query(
       "select obj from ChattingLog obj where obj.chatting.id=:chat_id  and obj.user.id=:uid order by addTime desc", 
       map, 0, 1);
     mv.addObject("logs", logs);
     return mv;
   }
 }


 
 
 