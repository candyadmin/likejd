 package com.shopping.manage.admin.action;
 
 import com.easyjf.beans.BeanUtils;
 import com.easyjf.beans.BeanWrapper;
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.TransArea;
 import com.shopping.foundation.domain.query.AreaQueryObject;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.ITransAreaService;
 import com.shopping.foundation.service.IUserConfigService;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.lang.reflect.Field;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class TransAreaManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private ITransAreaService transareaService;
 
   @SecurityMapping(display = false, rsequence = 0, title="运费地区列表", value="/admin/trans_area_list.htm*", rtype="admin", rname="运费区域", rcode="admin_trans_area", rgroup="设置")
   @RequestMapping({"/admin/trans_area_list.htm"})
   public ModelAndView trans_area_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String pid, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView("admin/blue/trans_area_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     AreaQueryObject qo = null;
     if ((pid == null) || (pid.equals(""))) {
       qo = new AreaQueryObject(currentPage, mv, orderBy, orderType);
       qo.addQuery("obj.parent.id is null", null);
     } else {
       qo = new AreaQueryObject(currentPage, mv, orderBy, orderType);
       qo.addQuery("obj.parent.id", 
         new SysMap("pid", Long.valueOf(Long.parseLong(pid))), "=");
       params = "&pid=" + pid;
       TransArea parent = this.transareaService.getObjById(
         Long.valueOf(Long.parseLong(pid)));
       mv.addObject("parent", parent);
       if (parent.getLevel() == 0) {
         Map map = new HashMap();
         map.put("pid", parent.getId());
         List seconds = this.transareaService
           .query(
           "select obj from TransArea obj where obj.parent.id=:pid", 
           map, -1, -1);
         mv.addObject("seconds", seconds);
         mv.addObject("first", parent);
       }
       if (parent.getLevel() == 1) {
         Map map = new HashMap();
         map.put("pid", parent.getId());
         List thirds = this.transareaService
           .query(
           "select obj from TransArea obj where obj.parent.id=:pid", 
           map, -1, -1);
         map.clear();
         map.put("pid", parent.getParent().getId());
         List seconds = this.transareaService
           .query(
           "select obj from TransArea obj where obj.parent.id=:pid", 
           map, -1, -1);
         mv.addObject("thirds", thirds);
         mv.addObject("seconds", seconds);
         mv.addObject("second", parent);
         mv.addObject("first", parent.getParent());
       }
       if (parent.getLevel() == 2) {
         Map map = new HashMap();
         map.put("pid", parent.getParent().getId());
         List thirds = this.transareaService
           .query(
           "select obj from TransArea obj where obj.parent.id=:pid", 
           map, -1, -1);
         map.clear();
         map.put("pid", parent.getParent().getParent().getId());
         List seconds = this.transareaService
           .query(
           "select obj from TransArea obj where obj.parent.id=:pid", 
           map, -1, -1);
         mv.addObject("thirds", thirds);
         mv.addObject("seconds", seconds);
         mv.addObject("third", parent);
         mv.addObject("second", parent.getParent());
         mv.addObject("first", parent.getParent().getParent());
       }
     }
     WebForm wf = new WebForm();
     wf.toQueryPo(request, qo, TransArea.class, mv);
     IPageList pList = this.transareaService.list(qo);
     CommUtil.saveIPageList2ModelAndView(url + "/admin/trans_area_list.htm", "", 
       params, pList, mv);
     List areas = this.transareaService.query(
       "select obj from TransArea obj where obj.parent.id is null", 
       null, -1, -1);
     mv.addObject("areas", areas);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="运费地区保存", value="/admin/trans_area_save.htm*", rtype="admin", rname="运费区域", rcode="admin_trans_area", rgroup="设置")
   @RequestMapping({"/admin/trans_area_save.htm"})
   public ModelAndView trans_area_save(HttpServletRequest request, HttpServletResponse response, String areaId, String pid, String count, String list_url, String currentPage)
   {
     if (areaId != null)
     {
       String[] ids = areaId.split(",");
       int i = 1;
       for (String id : ids) {
         String areaName = request.getParameter("areaName_" + i);
         TransArea area = this.transareaService.getObjById(
           Long.valueOf(Long.parseLong(request.getParameter("id_" + i))));
         area.setAreaName(areaName);
         area.setSequence(CommUtil.null2Int(request
           .getParameter("sequence_" + i)));
         this.transareaService.update(area);
         i++;
       }
 
     }
 
     TransArea parent = null;
     if (!pid.equals(""))
       parent = this.transareaService.getObjById(Long.valueOf(Long.parseLong(pid)));
     for (int i = 1; i <= CommUtil.null2Int(count); i++) {
       TransArea area = new TransArea();
       area.setAddTime(new Date());
       String areaName = request.getParameter("new_areaName_" + i);
       int sequence = CommUtil.null2Int(request
         .getParameter("new_sequence_" + i));
       if (parent != null) {
         area.setLevel(parent.getLevel() + 1);
         area.setParent(parent);
       }
       area.setAreaName(areaName);
       area.setSequence(sequence);
       this.transareaService.save(area);
     }
 
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("op_title", "更新配送区域成功");
     mv.addObject("list_url", list_url + "?currentPage=" + currentPage + 
       "&pid=" + pid);
     return mv;
   }
 
   private Set<Long> genericIds(TransArea obj)
   {
     Set ids = new HashSet();
     ids.add(obj.getId());
     for (TransArea child : obj.getChilds()) {
       Set<Long> cids = genericIds(child);
       for (Long cid : cids) {
         ids.add(cid);
       }
       ids.add(child.getId());
     }
     return ids;
   }
   @SecurityMapping(display = false, rsequence = 0, title="运费地区删除", value="/admin/trans_area_del.htm*", rtype="admin", rname="运费区域", rcode="admin_trans_area", rgroup="设置")
   @RequestMapping({"/admin/trans_area_del.htm"})
   public String trans_area_del(HttpServletRequest request, String mulitId, String currentPage, String pid) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         Set list = genericIds(this.transareaService
           .getObjById(Long.valueOf(Long.parseLong(id))));
         Map params = new HashMap();
         params.put("ids", list);
         List<TransArea> objs = this.transareaService.query(
           "select obj from TransArea obj where obj.id in (:ids)", 
           params, -1, -1);
         for (TransArea obj : objs) {
           obj.setParent(null);
           this.transareaService.delete(obj.getId());
         }
       }
     }
     return "redirect:trans_area_list.htm?pid=" + pid + "&currentPage=" + 
       currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="运费地区Ajax更新", value="/admin/trans_area_ajax.htm*", rtype="admin", rname="运费区域", rcode="admin_trans_area", rgroup="设置")
   @RequestMapping({"/admin/trans_area_ajax.htm"})
   public void trans_area_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
     TransArea obj = this.transareaService.getObjById(Long.valueOf(Long.parseLong(id)));
     Field[] fields = TransArea.class.getDeclaredFields();
     BeanWrapper wrapper = new BeanWrapper(obj);
     Object val = null;
     for (Field field : fields)
     {
       if (field.getName().equals(fieldName)) {
         Class clz = Class.forName("java.lang.String");
         if (field.getType().getName().equals("int")) {
           clz = Class.forName("java.lang.Integer");
         }
         if (field.getType().getName().equals("boolean")) {
           clz = Class.forName("java.lang.Boolean");
         }
         if (!value.equals(""))
           val = BeanUtils.convertType(value, clz);
         else {
           val = Boolean.valueOf(
             !CommUtil.null2Boolean(wrapper
             .getPropertyValue(fieldName)));
         }
         wrapper.setPropertyValue(fieldName, val);
       }
     }
     this.transareaService.update(obj);
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(val.toString());
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 }


 
 
 