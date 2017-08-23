 package com.shopping.manage.admin.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.Group;
 import com.shopping.foundation.domain.GroupGoods;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.query.GroupGoodsQueryObject;
 import com.shopping.foundation.domain.query.GroupQueryObject;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.IGroupGoodsService;
 import com.shopping.foundation.service.IGroupService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
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
 public class GroupManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGroupService groupService;
 
   @Autowired
   private IGroupGoodsService groupGoodsService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @SecurityMapping(display = false, rsequence = 0, title="团购列表", value="/admin/group_list.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_list.htm"})
   public ModelAndView group_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView("admin/blue/group_list.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     GroupQueryObject qo = new GroupQueryObject(currentPage, mv, orderBy, 
       orderType);
 
     IPageList pList = this.groupService.list(qo);
     CommUtil.saveIPageList2ModelAndView(url + "/admin/group_list.htm", "", 
       params, pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购增加", value="/admin/group_add.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_add.htm"})
   public ModelAndView group_add(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/group_add.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     mv.addObject("currentPage", currentPage);
     Map params = new HashMap();
     params.put("status", Integer.valueOf(0));
     List groups = this.groupService
       .query("select obj from Group obj where obj.status=:status order by obj.endTime desc", 
       params, 0, 1);
     if (groups.size() > 0) {
       Group group = (Group)groups.get(0);
       mv.addObject("group", group);
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购编辑", value="/admin/group_edit.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_edit.htm"})
   public ModelAndView group_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/group_add.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       Group group = this.groupService.getObjById(Long.valueOf(Long.parseLong(id)));
       mv.addObject("obj", group);
       mv.addObject("currentPage", currentPage);
       mv.addObject("edit", Boolean.valueOf(true));
       Map params = new HashMap();
       params.put("status", Integer.valueOf(0));
       List groups = this.groupService
         .query("select obj from Group obj where obj.status=:status order by obj.endTime desc", 
         params, 0, 1);
       if (groups.size() > 0) {
         Group group1 = (Group)groups.get(0);
         mv.addObject("group", group1);
       }
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购保存", value="/admin/group_save.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_save.htm"})
   public ModelAndView group_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String cmd, String begin_hour, String end_hour, String join_hour)
   {
     WebForm wf = new WebForm();
     Group group = null;
     if (id.equals("")) {
       group = (Group)wf.toPo(request, Group.class);
       group.setAddTime(new Date());
     } else {
       Group obj = this.groupService.getObjById(Long.valueOf(Long.parseLong(id)));
       group = (Group)wf.toPo(request, obj);
     }
     Date beginTime = group.getBeginTime();
     beginTime.setHours(CommUtil.null2Int(begin_hour));
     group.setBeginTime(beginTime);
     Date endTime = group.getEndTime();
     endTime.setHours(CommUtil.null2Int(end_hour));
     group.setEndTime(endTime);
     Date joinEndTime = group.getJoinEndTime();
     joinEndTime.setHours(CommUtil.null2Int(join_hour));
     group.setJoinEndTime(joinEndTime);
     if (beginTime.after(new Date())) {
       group.setStatus(1);
     }
     if (id.equals(""))
       this.groupService.save(group);
     else
       this.groupService.update(group);
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/group_list.htm");
     mv.addObject("op_title", "保存团购成功");
     mv.addObject("add_url", CommUtil.getURL(request) + 
       "/admin/group_add.htm" + "?currentPage=" + currentPage);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="团购删除", value="/admin/group_del.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_del.htm"})
   public String group_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         Group group = this.groupService.getObjById(
           CommUtil.null2Long(id));
         for (Goods goods : group.getGoods_list()) {
           goods.setGroup_buy(0);
           goods.setGroup(null);
           this.goodsService.update(goods);
         }
         for (GroupGoods gg : group.getGg_list()) {
           this.groupGoodsService.delete(gg.getId());
         }
         this.groupService.delete(CommUtil.null2Long(id));
       }
     }
     return "redirect:group_list.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="团购关闭", value="/admin/group_close.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_close.htm"})
   public String group_close(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         Group group = this.groupService.getObjById(Long.valueOf(Long.parseLong(id)));
         group.setStatus(-1);
         this.groupService.update(group);
         for (GroupGoods gg : group.getGg_list()) {
           gg.setGg_status(-1);
           this.groupGoodsService.update(gg);
         }
         for (Goods goods : group.getGoods_list()) {
           if (goods.getGroup().getId().equals(group.getId())) {
             goods.setGroup(null);
             goods.setGroup_buy(0);
             this.goodsService.update(goods);
           }
         }
       }
     }
     return "redirect:group_list.htm?currentPage=" + currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购申请列表", value="/admin/group_goods_list.htm*", rtype="seller", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_goods_list.htm"})
   public ModelAndView group_goods_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String group_id, String gg_status) {
     ModelAndView mv = new JModelAndView("admin/blue/group_goods_list.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GroupGoodsQueryObject qo = new GroupGoodsQueryObject(currentPage, mv, 
       "addTime", "desc");
     qo.addQuery("obj.group.id", 
       new SysMap("group_id", CommUtil.null2Long(group_id)), "=");
     if ((gg_status == null) || (gg_status.equals("")))
       qo.addQuery("obj.gg_status", new SysMap("gg_status", Integer.valueOf(0)), "=");
     else {
       qo.addQuery("obj.gg_status", 
         new SysMap("gg_status", Integer.valueOf(CommUtil.null2Int(gg_status))), "=");
     }
     IPageList pList = this.groupGoodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("group_id", group_id);
     mv.addObject("gg_status", Integer.valueOf(CommUtil.null2Int(gg_status)));
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购商品审核通过", value="/admin/group_goods_audit.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_goods_audit.htm"})
   public String group_goods_audit(HttpServletRequest request, HttpServletResponse response, String mulitId, String group_id, String gg_status, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         GroupGoods gg = this.groupGoodsService.getObjById(
           CommUtil.null2Long(id));
         gg.setGg_status(1);
         gg.setGg_audit_time(new Date());
         this.groupGoodsService.update(gg);
         Goods goods = gg.getGg_goods();
         goods.setGroup_buy(2);
         goods.setGroup(this.groupService.getObjById(
           CommUtil.null2Long(group_id)));
         goods.setGoods_current_price(gg.getGg_price());
         this.goodsService.update(goods);
       }
     }
     return "redirect:group_goods_list.htm?group_id=" + group_id + 
       "&gg_status=" + gg_status + "&currentPage=" + currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购商品审核拒绝", value="/admin/group_goods_refuse.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_goods_refuse.htm"})
   public String group_goods_refuse(HttpServletRequest request, HttpServletResponse response, String mulitId, String group_id, String gg_status, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         GroupGoods gg = this.groupGoodsService.getObjById(
           CommUtil.null2Long(id));
         Goods goods = gg.getGg_goods();
         goods.setGroup_buy(0);
         goods.setGroup(null);
         goods.setGoods_current_price(goods.getStore_price());
         this.goodsService.update(goods);
         gg.setGg_status(-1);
         this.groupGoodsService.update(gg);
       }
     }
     return "redirect:group_goods_list.htm?group_id=" + group_id + 
       "&gg_status=" + gg_status + "&currentPage=" + currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购商品审核推荐", value="/admin/group_goods_recommend.htm*", rtype="admin", rname="团购管理", rcode="group_admin", rgroup="运营")
   @RequestMapping({"/admin/group_goods_recommend.htm"})
   public String group_goods_recommend(HttpServletRequest request, HttpServletResponse response, String mulitId, String group_id, String gg_status, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         GroupGoods gg = this.groupGoodsService.getObjById(
           CommUtil.null2Long(id));
         if (gg.getGg_recommend() == 0)
           gg.setGg_recommend(1);
         else {
           gg.setGg_recommend(0);
         }
         gg.setGg_recommend_time(new Date());
         this.groupGoodsService.update(gg);
       }
     }
     return "redirect:group_goods_list.htm?group_id=" + group_id + 
       "&gg_status=" + gg_status + "&currentPage=" + currentPage;
   }
 }


 
 
 