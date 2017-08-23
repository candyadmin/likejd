 package com.shopping.manage.seller.action;
 
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.domain.virtual.SysMap;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.query.support.IPageList;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.core.tools.WebForm;
 import com.shopping.core.tools.database.DatabaseTools;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.domain.Album;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.Store;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.WaterMark;
 import com.shopping.foundation.domain.query.AccessoryQueryObject;
 import com.shopping.foundation.domain.query.AlbumQueryObject;
 import com.shopping.foundation.service.IAccessoryService;
 import com.shopping.foundation.service.IAlbumService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserService;
 import com.shopping.foundation.service.IWaterMarkService;
 import com.shopping.view.web.tools.AlbumViewTools;
 import java.awt.Font;
 import java.io.File;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.servlet.ServletContext;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import javax.servlet.http.HttpSession;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class AlbumSellerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IAlbumService albumService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @Autowired
   private IWaterMarkService waterMarkService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IGoodsService goodsSerivce;
 
   @Autowired
   private AlbumViewTools albumViewTools;
 
   @Autowired
   private DatabaseTools databaseTools;
 
   @SecurityMapping(display = false, rsequence = 0, title="相册列表", value="/seller/album.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/album.htm"})
   public ModelAndView album(HttpServletRequest request, HttpServletResponse response, String currentPage)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/album.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     AlbumQueryObject aqo = new AlbumQueryObject();
     aqo.addQuery("obj.user.id", 
       new SysMap("user_id", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     aqo.setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
     aqo.setOrderBy("album_sequence");
     aqo.setOrderType("asc");
     IPageList pList = this.albumService.list(aqo);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     CommUtil.saveIPageList2ModelAndView(url + "/seller/album.htm", "", "", 
       pList, mv);
     mv.addObject("albumViewTools", this.albumViewTools);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="新增相册", value="/seller/album_add.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/album_add.htm"})
   public ModelAndView album_add(HttpServletRequest request, HttpServletResponse response, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/album_add.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="新增相册", value="/seller/album_edit.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/album_edit.htm"})
   public ModelAndView album_edit(HttpServletRequest request, HttpServletResponse response, String currentPage, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/album_add.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Album obj = this.albumService.getObjById(CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="相册保存", value="/seller/album_save.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/album_save.htm"})
   public String album_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     WebForm wf = new WebForm();
     Album album = null;
     if (id.equals("")) {
       album = (Album)wf.toPo(request, Album.class);
       album.setAddTime(new Date());
     } else {
       Album obj = this.albumService.getObjById(Long.valueOf(Long.parseLong(id)));
       album = (Album)wf.toPo(request, obj);
     }
     album.setUser(SecurityUserHolder.getCurrentUser());
     boolean ret = true;
     if (id.equals(""))
       ret = this.albumService.save(album);
     else
       ret = this.albumService.update(album);
     return "redirect:album.htm?currentPage=" + currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="图片上传", value="/seller/album_upload.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/album_upload.htm"})
   public ModelAndView album_upload(HttpServletRequest request, HttpServletResponse response, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/album_upload.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Map params = new HashMap();
     params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
     List objs = this.albumService
       .query("select obj from Album obj where obj.user.id=:user_id order by obj.album_sequence asc", 
       params, -1, -1);
     mv.addObject("objs", objs);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="相册删除", value="/seller/album_del.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/album_del.htm"})
   public String album_del(HttpServletRequest request, String mulitId) { String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         List<Accessory> accs = this.albumService.getObjById(
           Long.valueOf(Long.parseLong(id))).getPhotos();
         for (Accessory acc : accs) {
           CommUtil.del_acc(request, acc);
           this.databaseTools
             .execute("update shopping_album set album_cover_id=null where album_cover_id=" + 
             acc.getId());
         }
         this.albumService.delete(Long.valueOf(Long.parseLong(id)));
       }
     }
     return "redirect:album.htm"; }
 
   @SecurityMapping(display = false, rsequence = 0, title="相册封面设置", value="/seller/album_cover.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/album_cover.htm"})
   public String album_cover(HttpServletRequest request, String album_id, String id, String currentPage) {
     Accessory album_cover = this.accessoryService.getObjById(
       Long.valueOf(Long.parseLong(id)));
     Album album = this.albumService.getObjById(Long.valueOf(Long.parseLong(album_id)));
     album.setAlbum_cover(album_cover);
     this.albumService.update(album);
     return "redirect:album_image.htm?id=" + album_id + "&currentPage=" + 
       currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="相册转移", value="/seller/album_transfer.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/album_transfer.htm"})
   public ModelAndView album_transfer(HttpServletRequest request, HttpServletResponse response, String currentPage, String album_id, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/album_transfer.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Map params = new HashMap();
     params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
     List objs = this.albumService
       .query("select obj from Album obj where obj.user.id=:user_id order by obj.album_sequence asc", 
       params, -1, -1);
     mv.addObject("objs", objs);
     mv.addObject("currentPage", currentPage);
     mv.addObject("album_id", album_id);
     mv.addObject("mulitId", id);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="图片转移相册", value="/seller/album_transfer_save.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/album_transfer_save.htm"})
   public String album_transfer_save(HttpServletRequest request, String mulitId, String album_id, String to_album_id, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         Accessory acc = this.accessoryService.getObjById(
           Long.valueOf(Long.parseLong(id)));
         Album to_album = this.albumService.getObjById(
           Long.valueOf(Long.parseLong(to_album_id)));
         acc.setAlbum(to_album);
         this.accessoryService.update(acc);
       }
     }
     return "redirect:album_image.htm?id=" + album_id + "&currentPage=" + 
       currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="图片列表", value="/seller/album_image.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/album_image.htm"})
   public ModelAndView album_image(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/album_image.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Album album = this.albumService.getObjById(Long.valueOf(Long.parseLong(id)));
     AccessoryQueryObject aqo = new AccessoryQueryObject();
     if ((id != null) && (!id.equals("")))
       aqo.addQuery("obj.album.id", 
         new SysMap("album_id", CommUtil.null2Long(id)), "=");
     else {
       aqo.addQuery("obj.album.id is null", null);
     }
     aqo.addQuery("obj.album.user.id", 
       new SysMap("user_id", 
       SecurityUserHolder.getCurrentUser().getId()), "=");
     aqo.setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
     aqo.setPageSize(Integer.valueOf(15));
     IPageList pList = this.accessoryService.list(aqo);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     CommUtil.saveIPageList2ModelAndView(url + "/seller/album_image.htm", 
       "", "&id=" + id, pList, mv);
     Map params = new HashMap();
     params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
     List albums = this.albumService
       .query("select obj from Album obj where obj.user.id=:user_id order by obj.album_sequence asc", 
       params, -1, -1);
     mv.addObject("albums", albums);
     mv.addObject("album", album);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="图片幻灯查看", value="/seller/image_slide.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/image_slide.htm"})
   public ModelAndView image_slide(HttpServletRequest request, HttpServletResponse response, String album_id, String id) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/image_slide.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Album album = this.albumService
       .getObjById(CommUtil.null2Long(album_id));
     mv.addObject("album", album);
     Accessory current_img = this.accessoryService.getObjById(
       CommUtil.null2Long(id));
     mv.addObject("current_img", current_img);
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="相册内图片删除", value="/seller/album_img_del.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/album_img_del.htm"})
   public String album_img_del(HttpServletRequest request, String mulitId, String album_id, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         Accessory acc = this.accessoryService.getObjById(
           Long.valueOf(Long.parseLong(id)));
         if (acc.getCover_album() != null) {
           acc.getCover_album().setAlbum_cover(null);
           this.albumService.update(acc.getCover_album());
         }
         CommUtil.del_acc(request, acc);
         for (Goods goods : acc.getGoods_main_list()) {
           goods.setGoods_main_photo(null);
           this.goodsSerivce.update(goods);
         }
         for (Goods goods : acc.getGoods_list()) {
           goods.getGoods_photos().remove(acc);
           this.goodsSerivce.update(goods);
         }
         this.accessoryService.delete(acc.getId());
       }
     }
     return "redirect:album_image.htm?id=" + album_id + "&currentPage=" + 
       currentPage;
   }
   @SecurityMapping(display = false, rsequence = 0, title="图片转移相册", value="/seller/album_watermark.htm*", rtype="seller", rname="图片管理", rcode="album_seller", rgroup="其他设置")
   @RequestMapping({"/seller/album_watermark.htm"})
   public String album_watermark(HttpServletRequest request, String mulitId, String album_id, String to_album_id, String currentPage) {
     Long store_id = null;
     User user = this.userService.getObjById(
       SecurityUserHolder.getCurrentUser().getId());
     if (user.getStore() != null) {
       store_id = SecurityUserHolder.getCurrentUser().getStore().getId();
     }
     if (store_id != null) {
       WaterMark waterMark = this.waterMarkService.getObjByProperty(
         "store.id", store_id);
       if (waterMark != null) {
         String[] ids = mulitId.split(",");
         for (String id : ids) {
           if (!id.equals("")) {
             Accessory acc = this.accessoryService.getObjById(
               Long.valueOf(Long.parseLong(id)));
             String path = request.getSession().getServletContext()
               .getRealPath("/") + 
               acc.getPath() + 
               File.separator + 
               acc.getName();
             if (waterMark.isWm_image_open()) {
               String wm_path = request.getSession()
                 .getServletContext().getRealPath("/") + 
                 waterMark.getWm_image().getPath() + 
                 File.separator + 
                 waterMark.getWm_image().getName();
               CommUtil.waterMarkWithImage(wm_path, path, 
                 waterMark.getWm_image_pos(), 
                 waterMark.getWm_image_alpha());
             }
             if (waterMark.isWm_text_open()) {
               Font font = new Font(waterMark.getWm_text_font(), 
                 1, waterMark.getWm_text_font_size());
               CommUtil.waterMarkWithText(path, path, 
                 waterMark.getWm_text(), 
                 waterMark.getWm_text_color(), font, 
                 waterMark.getWm_text_pos(), 100.0F);
             }
           }
         }
       }
     }
     return "redirect:album_image.htm?id=" + album_id + "&currentPage=" + 
       currentPage;
   }
 }


 
 
 