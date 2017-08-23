 package com.shopping.manage.seller.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.Group;
 import com.shopping.foundation.domain.GroupArea;
 import com.shopping.foundation.domain.GroupClass;
 import com.shopping.foundation.domain.GroupGoods;
 import com.shopping.foundation.domain.Store;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.UserGoodsClass;
 import com.shopping.foundation.domain.query.GroupGoodsQueryObject;
 import com.shopping.foundation.service.IAccessoryService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.IGroupAreaService;
 import com.shopping.foundation.service.IGroupClassService;
 import com.shopping.foundation.service.IGroupGoodsService;
 import com.shopping.foundation.service.IGroupService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserGoodsClassService;
 import com.shopping.foundation.service.IUserService;
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.servlet.ServletContext;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import org.nutz.json.Json;
 import org.nutz.json.JsonFormat;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class GroupSellerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGroupService groupService;
 
   @Autowired
   private IGroupAreaService groupAreaService;
 
   @Autowired
   private IGroupClassService groupClassService;
 
   @Autowired
   private IGroupGoodsService groupGoodsService;
 
   @Autowired
   private IUserGoodsClassService userGoodsClassService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @Autowired
   private IUserService userService;
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家团购列表", value="/seller/group.htm*", rtype="seller", rname="团购管理", rcode="group_seller", rgroup="促销管理")
   @RequestMapping({"/seller/group.htm"})
   public ModelAndView group(HttpServletRequest request, HttpServletResponse response, String currentPage, String gg_name)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/group.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     GroupGoodsQueryObject qo = new GroupGoodsQueryObject(currentPage, mv, 
       "addTime", "desc");
     qo.addQuery("obj.gg_goods.goods_store.user.id", 
       new SysMap("user_id", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     if (!CommUtil.null2String(gg_name).equals("")) {
       qo.addQuery("obj.gg_name", 
         new SysMap("gg_name", "%" + gg_name + 
         "%"), "like");
     }
     IPageList pList = this.groupGoodsService.list(qo);
     CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("gg_name", gg_name);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家团购添加", value="/seller/group_add.htm*", rtype="seller", rname="团购管理", rcode="group_seller", rgroup="促销管理")
   @RequestMapping({"/seller/group_add.htm"})
   public ModelAndView group_add(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/group_add.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Map params = new HashMap();
     params.put("joinEndTime", new Date());
     List groups = this.groupService
       .query(
       "select obj from Group obj where obj.joinEndTime>=:joinEndTime", 
       params, -1, -1);
     List gas = this.groupAreaService
       .query(
       "select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc", 
       null, -1, -1);
     List gcs = this.groupClassService
       .query(
       "select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc", 
       null, -1, -1);
     mv.addObject("gcs", gcs);
     mv.addObject("gas", gas);
     mv.addObject("groups", groups);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家团购编辑", value="/seller/group_edit.htm*", rtype="seller", rname="团购管理", rcode="group_seller", rgroup="促销管理")
   @RequestMapping({"/seller/group_edit.htm"})
   public ModelAndView group_edit(HttpServletRequest request, HttpServletResponse response, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/group_add.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Map params = new HashMap();
     params.put("joinEndTime", new Date());
     List groups = this.groupService
       .query(
       "select obj from Group obj where obj.joinEndTime>=:joinEndTime", 
       params, -1, -1);
     List gas = this.groupAreaService
       .query(
       "select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc", 
       null, -1, -1);
     List gcs = this.groupClassService
       .query(
       "select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc", 
       null, -1, -1);
     GroupGoods obj = this.groupGoodsService.getObjById(
       CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     mv.addObject("gcs", gcs);
     mv.addObject("gas", gas);
     mv.addObject("groups", groups);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家团购商品", value="/seller/group_goods.htm*", rtype="seller", rname="团购管理", rcode="group_seller", rgroup="促销管理")
   @RequestMapping({"/seller/group_goods.htm"})
   public ModelAndView group_goods(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/group_goods.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Map params = new HashMap();
     params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
     List gcs = this.userGoodsClassService
       .query(
       "select obj from UserGoodsClass obj where obj.parent.id is null and obj.user.id=:user_id order by obj.sequence asc", 
       params, -1, -1);
     mv.addObject("gcs", gcs);
     return mv;
   }
 
   @RequestMapping({"/seller/group_goods_load.htm"})
   public void group_goods_load(HttpServletRequest request, HttpServletResponse response, String goods_name, String gc_id) {
     goods_name = CommUtil.convert(goods_name, "UTF-8");
     boolean ret = true;
     Map params = new HashMap();
     params.put("goods_name", "%" + goods_name.trim() + "%");
     params.put("group_buy", Integer.valueOf(0));
     params.put("as", Integer.valueOf(0));
     params.put("delivery_status", Integer.valueOf(0));
     params.put("combin_status", Integer.valueOf(0));
     Store store = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId()).getStore();
     params.put("store_id", store.getId());
     UserGoodsClass ugc = this.userGoodsClassService.getObjById(
       CommUtil.null2Long(gc_id));
     Set<Long> ids = genericUserGcIds(ugc);
     List ugc_list = new ArrayList();
     for (Long g_id : ids) {
       UserGoodsClass temp_ugc = this.userGoodsClassService
         .getObjById(g_id);
       ugc_list.add(temp_ugc);
     }
     String query = "select obj from Goods obj where obj.goods_name like:goods_name and obj.group_buy=:group_buy and obj.goods_store.id=:store_id and obj.activity_status=:as and obj.delivery_status=:delivery_status and obj.combin_status=:combin_status";
     for (int i = 0; i < ugc_list.size(); i++) {
       if (i == 0) {
         query = query + " and (:ugc" + i + " member of obj.goods_ugcs";
         if (ugc_list.size() == 1) {
           query = query + ")";
         }
       }
       else if (i == ugc_list.size() - 1) {
         query = query + " or :ugc" + i + 
           " member of obj.goods_ugcs)";
       } else {
         query = query + " or :ugc" + i + 
           " member of obj.goods_ugcs";
       }
       params.put("ugc" + i, ugc_list.get(i));
     }
     List<Goods> goods = this.goodsService.query(query, params, -1, -1);
     List list = new ArrayList();
     for (Goods obj : goods) {
       Map map = new HashMap();
       map.put("id", obj.getId());
       map.put("goods_name", obj.getGoods_name());
       map.put("store_price", obj.getStore_price());
       map.put("store_inventory", Integer.valueOf(obj.getGoods_inventory()));
       list.add(map);
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(Json.toJson(list, JsonFormat.compact()));
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="团购商品保存", value="/seller/group_goods_save.htm*", rtype="seller", rname="团购管理", rcode="group_seller", rgroup="促销管理")
   @RequestMapping({"/seller/group_goods_save.htm"})
   public String group_goods_save(HttpServletRequest request, HttpServletResponse response, String id, String group_id, String goods_id, String gc_id, String ga_id) {
     WebForm wf = new WebForm();
     GroupGoods gg = null;
     if (id.equals("")) {
       gg = (GroupGoods)wf.toPo(request, GroupGoods.class);
       gg.setAddTime(new Date());
     } else {
       GroupGoods obj = this.groupGoodsService.getObjById(
         CommUtil.null2Long(id));
       gg = (GroupGoods)wf.toPo(request, obj);
     }
     Group group = this.groupService
       .getObjById(CommUtil.null2Long(group_id));
     gg.setGroup(group);
     Goods goods = this.goodsService
       .getObjById(CommUtil.null2Long(goods_id));
     gg.setGg_goods(goods);
     GroupClass gc = this.groupClassService.getObjById(
       CommUtil.null2Long(gc_id));
     gg.setGg_gc(gc);
     GroupArea ga = this.groupAreaService.getObjById(
       CommUtil.null2Long(ga_id));
     gg.setGg_ga(ga);
     String uploadFilePath = this.configService.getSysConfig()
       .getUploadFilePath();
     String saveFilePathName = request.getSession().getServletContext()
       .getRealPath("/") + 
       uploadFilePath + File.separator + "group";
     Map map = new HashMap();
     try {
       String fileName = gg.getGg_img() == null ? "" : gg.getGg_img()
         .getName();
       map = CommUtil.saveFileToServer(request, "gg_acc", 
         saveFilePathName, fileName, null);
       if (fileName.equals("")) {
         if (map.get("fileName") != "") {
           Accessory gg_img = new Accessory();
           gg_img.setName(CommUtil.null2String(map.get("fileName")));
           gg_img.setExt(CommUtil.null2String(map.get("mime")));
           gg_img.setSize(CommUtil.null2Float(map.get("fileSize")));
           gg_img.setPath(uploadFilePath + "/group");
           gg_img.setWidth(CommUtil.null2Int(map.get("width")));
           gg_img.setHeight(CommUtil.null2Int(map.get("height")));
           gg_img.setAddTime(new Date());
           this.accessoryService.save(gg_img);
           gg.setGg_img(gg_img);
         }
       }
       else if (map.get("fileName") != "") {
         Accessory gg_img = gg.getGg_img();
         gg_img.setName(CommUtil.null2String(map.get("fileName")));
         gg_img.setExt(CommUtil.null2String(map.get("mime")));
         gg_img.setSize(CommUtil.null2Float(map.get("fileSize")));
         gg_img.setPath(uploadFilePath + "/group");
         gg_img.setWidth(CommUtil.null2Int(map.get("width")));
         gg_img.setHeight(CommUtil.null2Int(map.get("height")));
         gg_img.setAddTime(new Date());
         this.accessoryService.update(gg_img);
       }
 
     }
     catch (IOException e)
     {
       e.printStackTrace();
     }
     gg.setGg_rebate(BigDecimal.valueOf(CommUtil.div(Double.valueOf(CommUtil.mul(gg
       .getGg_price(), Integer.valueOf(10))), gg.getGg_goods().getGoods_price())));
     if (id.equals(""))
       this.groupGoodsService.save(gg);
     else {
       this.groupGoodsService.update(gg);
     }
     goods.setGroup_buy(1);
 
     this.goodsService.update(goods);
     request.getSession(false).setAttribute("url", 
       CommUtil.getURL(request) + "/seller/group.htm");
     request.getSession(false).setAttribute("op_title", "团购商品保存成功");
     return "redirect:" + CommUtil.getURL(request) + "/success.htm";
   }
 
   private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
     Set ids = new HashSet();
     if (ugc != null) {
       ids.add(ugc.getId());
       for (UserGoodsClass child : ugc.getChilds()) {
         Set<Long> cids = genericUserGcIds(child);
         for (Long cid : cids) {
           ids.add(cid);
         }
         ids.add(child.getId());
       }
     }
     return ids;
   }
   @SecurityMapping(display = false, rsequence = 0, title="团购商品删除", value="/seller/group_del.htm*", rtype="seller", rname="团购管理", rcode="group_seller", rgroup="促销管理")
   @RequestMapping({"/seller/group_del.htm"})
   public String group_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       GroupGoods gg = this.groupGoodsService.getObjById(
         CommUtil.null2Long(id));
       Goods goods = gg.getGg_goods();
       goods.setGroup_buy(0);
       this.goodsService.update(goods);
       CommUtil.del_acc(request, gg.getGg_img());
       this.groupGoodsService.delete(CommUtil.null2Long(id));
     }
     return "redirect:group.htm?currentPage=" + currentPage;
   }
 }


 
 
 