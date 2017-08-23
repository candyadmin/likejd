 package com.shopping.manage.admin.action;
 
 import com.easyjf.beans.BeanUtils;
 import com.easyjf.beans.BeanWrapper;
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.GoodsBrand;
 import com.shopping.foundation.domain.GoodsClass;
 import com.shopping.foundation.domain.GoodsSpecification;
 import com.shopping.foundation.domain.GoodsType;
 import com.shopping.foundation.domain.GoodsTypeProperty;
 import com.shopping.foundation.domain.query.GoodsTypeQueryObject;
 import com.shopping.foundation.service.IGoodsBrandCategoryService;
 import com.shopping.foundation.service.IGoodsBrandService;
 import com.shopping.foundation.service.IGoodsClassService;
 import com.shopping.foundation.service.IGoodsSpecificationService;
 import com.shopping.foundation.service.IGoodsTypePropertyService;
 import com.shopping.foundation.service.IGoodsTypeService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.manage.admin.tools.StoreTools;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.lang.reflect.Field;
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
 public class GoodsTypeManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGoodsTypeService goodsTypeService;
 
   @Autowired
   private IGoodsBrandService goodsBrandService;
 
   @Autowired
   private IGoodsBrandCategoryService goodsBrandCategoryService;
 
   @Autowired
   private IGoodsSpecificationService goodsSpecificationService;
 
   @Autowired
   private IGoodsTypePropertyService goodsTypePropertyService;
 
   @Autowired
   private IGoodsClassService goodsClassService;
 
   @Autowired
   private StoreTools shopTools;
 
   @SecurityMapping(display = false, rsequence = 0, title="商品类型列表", value="/admin/goods_type_list.htm*", rtype="admin", rname="类型管理", rcode="goods_type", rgroup="商品")
   @RequestMapping({"/admin/goods_type_list.htm"})
   public ModelAndView list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView("admin/blue/goods_type_list.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     GoodsTypeQueryObject qo = new GoodsTypeQueryObject(currentPage, mv, 
       orderBy, orderType);
     WebForm wf = new WebForm();
     wf.toQueryPo(request, qo, GoodsType.class, mv);
     qo.setOrderBy("sequence");
     qo.setOrderType("asc");
     IPageList pList = this.goodsTypeService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品类型添加", value="/admin/goods_type_add.htm*", rtype="admin", rname="类型管理", rcode="goods_type", rgroup="商品")
   @RequestMapping({"/admin/goods_type_add.htm"})
   public ModelAndView add(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView("admin/blue/goods_type_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     List gbcs = this.goodsBrandCategoryService.query(
       "select obj from GoodsBrandCategory obj order by sequence asc", 
       null, -1, -1);
     List gss = this.goodsSpecificationService
       .query(
       "select obj from GoodsSpecification obj order by obj.sequence asc", 
       null, -1, -1);
     mv.addObject("gss", gss);
     mv.addObject("gbcs", gbcs);
     mv.addObject("shopTools", this.shopTools);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品类型编辑", value="/admin/goods_type_edit.htm*", rtype="admin", rname="类型管理", rcode="goods_type", rgroup="商品")
   @RequestMapping({"/admin/goods_type_edit.htm"})
   public ModelAndView edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView("admin/blue/goods_type_add.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       GoodsType goodsType = this.goodsTypeService.getObjById(
         Long.valueOf(Long.parseLong(id)));
       List gbcs = this.goodsBrandCategoryService
         .query(
         "select obj from GoodsBrandCategory obj order by sequence asc", 
         null, -1, -1);
       List gss = this.goodsSpecificationService
         .query(
         "select obj from GoodsSpecification obj order by obj.sequence asc", 
         null, -1, -1);
       mv.addObject("gss", gss);
       mv.addObject("gbcs", gbcs);
       mv.addObject("shopTools", this.shopTools);
       mv.addObject("obj", goodsType);
       mv.addObject("edit", Boolean.valueOf(true));
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品类型保存", value="/admin/goods_type_save.htm*", rtype="admin", rname="类型管理", rcode="goods_type", rgroup="商品")
   @RequestMapping({"/admin/goods_type_save.htm"})
   public ModelAndView save(HttpServletRequest request, HttpServletResponse response, String id, String cmd, String currentPage, String list_url, String add_url, String spec_ids, String brand_ids, String count)
   {
     WebForm wf = new WebForm();
     GoodsType goodsType = null;
     if (id.equals("")) {
       goodsType = (GoodsType)wf.toPo(request, GoodsType.class);
       goodsType.setAddTime(new Date());
     } else {
       GoodsType obj = this.goodsTypeService
         .getObjById(Long.valueOf(Long.parseLong(id)));
       goodsType = (GoodsType)wf.toPo(request, obj);
     }
     goodsType.getGss().clear();
     goodsType.getGbs().clear();
     String[] gs_ids = spec_ids.split(",");
     GoodsSpecification gs;
     for (String gs_id : gs_ids) {
       if (!gs_id.equals("")) {
         gs = this.goodsSpecificationService
           .getObjById(Long.valueOf(Long.parseLong(gs_id)));
         goodsType.getGss().add(gs);
       }
     }
     String[] gb_ids = brand_ids.split(",");
     for (String gb_id : gb_ids) {
       if (!gb_id.equals("")) {
         GoodsBrand gb = this.goodsBrandService.getObjById(
           Long.valueOf(Long.parseLong(gb_id)));
         goodsType.getGbs().add(gb);
       }
     }
     if (id.equals(""))
       this.goodsTypeService.save(goodsType);
     else
       this.goodsTypeService.update(goodsType);
     genericProperty(request, goodsType, count);
     Object mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     ((ModelAndView)mv).addObject("list_url", list_url + "?currentPage=" + currentPage);
     ((ModelAndView)mv).addObject("op_title", "保存商品类型成功");
     if (add_url != null) {
       ((ModelAndView)mv).addObject("add_url", add_url);
     }
     return (ModelAndView)mv;
   }
 
   public void genericProperty(HttpServletRequest request, GoodsType type, String count)
   {
     for (int i = 1; i <= CommUtil.null2Int(count); i++) {
       int sequence = CommUtil.null2Int(request
         .getParameter("gtp_sequence_" + i));
       String name = CommUtil.null2String(request.getParameter("gtp_name_" + 
         i));
       String value = CommUtil.null2String(request
         .getParameter("gtp_value_" + i));
       boolean display = CommUtil.null2Boolean(request
         .getParameter("gtp_display_" + i));
       if ((!name.equals("")) && (!value.equals(""))) {
         GoodsTypeProperty gtp = null;
         String id = CommUtil.null2String(request.getParameter("gtp_id_" + 
           i));
         if (id.equals(""))
           gtp = new GoodsTypeProperty();
         else {
           gtp = this.goodsTypePropertyService.getObjById(
             Long.valueOf(Long.parseLong(id)));
         }
         gtp.setAddTime(new Date());
         gtp.setDisplay(display);
         gtp.setGoodsType(type);
         gtp.setName(name);
         gtp.setSequence(sequence);
         gtp.setValue(value);
         if (id.equals(""))
           this.goodsTypePropertyService.save(gtp);
         else
           this.goodsTypePropertyService.update(gtp);
       }
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品类型删除", value="/admin/goods_type_del.htm*", rtype="admin", rname="类型管理", rcode="goods_type", rgroup="商品")
   @RequestMapping({"/admin/goods_type_del.htm"})
   public String delete(HttpServletRequest request, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         GoodsType goodsType = this.goodsTypeService.getObjById(
           Long.valueOf(Long.parseLong(id)));
         goodsType.getGbs().clear();
         goodsType.getGss().clear();
         for (GoodsClass gc : goodsType.getGcs()) {
           gc.setGoodsType(null);
           this.goodsClassService.update(gc);
         }
         this.goodsTypeService.delete(Long.valueOf(Long.parseLong(id)));
       }
     }
     return "redirect:goods_type_list.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="商品类型属性AJAX删除", value="/admin/goods_type_property_delete.htm*", rtype="admin", rname="类型管理", rcode="goods_type", rgroup="商品")
   @RequestMapping({"/admin/goods_type_property_delete.htm"})
   public void goods_type_property_delete(HttpServletRequest request, HttpServletResponse response, String id) {
     boolean ret = true;
     if (!id.equals("")) {
       GoodsTypeProperty property = this.goodsTypePropertyService
         .getObjById(Long.valueOf(Long.parseLong(id)));
       property.setGoodsType(null);
       ret = this.goodsTypePropertyService.delete(property.getId());
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(ret);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="商品类型AJAX更新", value="/admin/goods_type_ajax.htm*", rtype="admin", rname="类型管理", rcode="goods_type", rgroup="商品")
   @RequestMapping({"/admin/goods_type_ajax.htm"})
   public void ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
     GoodsType obj = this.goodsTypeService.getObjById(Long.valueOf(Long.parseLong(id)));
     Field[] fields = GoodsType.class.getDeclaredFields();
     BeanWrapper wrapper = new BeanWrapper(obj);
     Object val = null;
     for (Field field : fields) {
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
     this.goodsTypeService.update(obj);
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
 
   @RequestMapping({"/admin/goods_type_verify.htm"})
   public void goods_type_verify(HttpServletRequest request, HttpServletResponse response, String name, String id)
   {
     boolean ret = true;
     Map params = new HashMap();
     params.put("name", name);
     params.put("id", CommUtil.null2Long(id));
     List gts = this.goodsTypeService
       .query(
       "select obj from GoodsType obj where obj.name=:name and obj.id!=:id", 
       params, -1, -1);
     if ((gts != null) && (gts.size() > 0)) {
       ret = false;
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(ret);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 }


 
 
 