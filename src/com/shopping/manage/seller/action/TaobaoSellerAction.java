 package com.shopping.manage.seller.action;
 
 import com.csvreader.CsvReader;
 import com.shopping.core.annotation.SecurityMapping;
 import com.shopping.core.mv.JModelAndView;
 import com.shopping.core.security.support.SecurityUserHolder;
 import com.shopping.core.tools.CommUtil;
 import com.shopping.foundation.domain.Accessory;
 import com.shopping.foundation.domain.Album;
 import com.shopping.foundation.domain.Goods;
 import com.shopping.foundation.domain.GoodsClass;
 import com.shopping.foundation.domain.Store;
 import com.shopping.foundation.domain.StoreGrade;
 import com.shopping.foundation.domain.SysConfig;
 import com.shopping.foundation.domain.User;
 import com.shopping.foundation.domain.UserGoodsClass;
 import com.shopping.foundation.domain.WaterMark;
 import com.shopping.foundation.service.IAccessoryService;
 import com.shopping.foundation.service.IAlbumService;
 import com.shopping.foundation.service.IGoodsClassService;
 import com.shopping.foundation.service.IGoodsService;
 import com.shopping.foundation.service.ISysConfigService;
 import com.shopping.foundation.service.IUserConfigService;
 import com.shopping.foundation.service.IUserGoodsClassService;
 import com.shopping.foundation.service.IUserService;
 import com.shopping.foundation.service.IWaterMarkService;
 import com.shopping.manage.admin.tools.StoreTools;
 import java.awt.Font;
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.math.BigDecimal;
 import java.nio.charset.Charset;
 import java.util.ArrayList;
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
 import org.springframework.web.multipart.MultipartHttpServletRequest;
 import org.springframework.web.multipart.commons.CommonsMultipartFile;
 import org.springframework.web.servlet.ModelAndView;
 
 @Controller
 public class TaobaoSellerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IGoodsClassService goodsClassService;
 
   @Autowired
   private IUserGoodsClassService userGoodsClassService;
 
   @Autowired
   private IGoodsService goodsService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private IAlbumService albumService;
 
   @Autowired
   private IAccessoryService accessoryService;
 
   @Autowired
   private IWaterMarkService waterMarkService;
 
   @Autowired
   private StoreTools storeTools;
 
   @SecurityMapping(display = false, rsequence = 0, title="导入淘宝CSV", value="/seller/taobao.htm*", rtype="seller", rname="淘宝导入", rcode="taobao_seller", rgroup="淘宝管理")
   @RequestMapping({"/seller/taobao.htm"})
   public ModelAndView taobao(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/taobao.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String taobao_upload_status = CommUtil.null2String(request.getSession(
       false).getAttribute("taobao_upload_status"));
     if (taobao_upload_status.equals("")) {
       Map params = new HashMap();
       params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
       params.put("display", Boolean.valueOf(true));
       List ugcs = this.userGoodsClassService
         .query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc", 
         params, -1, -1);
       params.clear();
       params.put("display", Boolean.valueOf(true));
       List gcs = this.goodsClassService
         .query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc", 
         params, -1, -1);
       mv.addObject("gcs", gcs);
       mv.addObject("ugcs", ugcs);
     }
     if (taobao_upload_status.equals("upload_img")) {
       mv = new JModelAndView(
         "user/default/usercenter/taobao_import_img.html", 
         this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 0, request, 
         response);
       HashMap params = new HashMap();
       params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
       List alubms = this.albumService.query(
         "select obj from Album obj where obj.user.id=:user_id", 
         params, -1, -1);
       mv.addObject("alubms", alubms);
       mv.addObject("already_import_count", request.getSession(false)
         .getAttribute("already_import_count"));
       mv.addObject("no_import_count", request.getSession(false)
         .getAttribute("no_import_count"));
       mv.addObject("jsessionid", request.getSession().getId());
     }
     if (taobao_upload_status.equals("upload_finish")) {
       mv = new JModelAndView(
         "user/default/usercenter/taobao_import_finish.html", 
         this.configService.getSysConfig(), 
         this.userConfigService.getUserConfig(), 0, request, 
         response);
     }
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="导入淘宝CSV", value="/seller/taobao_import_csv.htm*", rtype="seller", rname="淘宝导入", rcode="taobao_seller", rgroup="淘宝管理")
   @RequestMapping({"/seller/taobao_import_csv.htm"})
   public ModelAndView taobao_import_csv(HttpServletRequest request, HttpServletResponse response, String gc_id3, String ugc_ids) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/taobao_import_img.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String taobao_upload_status = CommUtil.null2String(request.getSession(
       false).getAttribute("taobao_upload_status"));
     String path = request.getSession().getServletContext().getRealPath("") + 
       File.separator + "csv";
     int already_import_count = 0;
     int no_import_count = 0;
     List taobao_goods_list = new ArrayList();
     try {
       Map map = CommUtil.saveFileToServer(request, "taobao_cvs", path, 
         "taobao.cvs", null);
       if (!map.get("fileName").equals("")) {
         String csvFilePath = path + File.separator + "taobao.cvs";
         CsvReader reader = new CsvReader(csvFilePath, '\t', 
           Charset.forName("UTF-16LE"));
         reader.readHeaders();
         int goods_name_pos = 0;
         int goods_price_pos = 7;
         int goods_count_pos = 9;
         int goods_status_pos = 20;
         int goods_transfee_pos = 11;
         int goods_recommend_pos = 21;
         int goods_detail_pos = 24;
         int goods_photo_pos = 35;
         User user = SecurityUserHolder.getCurrentUser();
         Album album = this.albumService.getDefaultAlbum(user.getId());
         if (album == null) {
           album = new Album();
           album.setAddTime(new Date());
           album.setAlbum_name("默认相册");
           album.setAlbum_sequence(-10000);
           album.setAlbum_default(true);
           this.albumService.save(album);
         }
         String img_path = this.storeTools.createUserFolderURL(
           this.configService.getSysConfig(), user.getStore());
         while (reader.readRecord()) {
           Store store = this.userService.getObjById(user.getId())
             .getStore();
           StoreGrade grade = store.getGrade();
           if ((grade.getGoodsCount() == 0) || 
             (store.getGoods_list().size() < grade
             .getGoodsCount())) {
             Goods goods = new Goods();
             goods.setGoods_name(reader.get(goods_name_pos));
             goods.setStore_price(BigDecimal.valueOf(
               CommUtil.null2Double(reader.get(goods_price_pos))));
             goods.setGoods_price(goods.getStore_price());
             goods.setGoods_inventory(CommUtil.null2Int(reader
               .get(goods_count_pos)));
             goods.setGoods_status(CommUtil.null2Int(reader
               .get(goods_status_pos)));
             goods.setGoods_recommend(CommUtil.null2Boolean(reader
               .get(goods_recommend_pos)));
             goods.setGoods_details(reader.get(goods_detail_pos));
             goods.setGoods_store(store);
             goods.setGoods_transfee(CommUtil.null2Int(reader
               .get(goods_transfee_pos)) - 1);
             goods.setGoods_current_price(goods.getStore_price());
             goods.setAddTime(new Date());
             goods.setGoods_seller_time(new Date());
             GoodsClass gc = this.goodsClassService
               .getObjById(CommUtil.null2Long(gc_id3));
             goods.setGc(gc);
             String[] ugc_id_list = ugc_ids.split(",");
             for (String ugc_id : ugc_id_list) {
               UserGoodsClass ugc = this.userGoodsClassService
                 .getObjById(CommUtil.null2Long(ugc_id));
               goods.getGoods_ugcs().add(ugc);
             }
             this.goodsService.save(goods);
             taobao_goods_list.add(goods);
             already_import_count++;
           } else {
             no_import_count++;
             mv = new JModelAndView("error.html", 
               this.configService.getSysConfig(), 
               this.userConfigService.getUserConfig(), 1, 
               request, response);
             mv.addObject("op_title", 
               "您的店铺等级只允许上传" + grade.getGoodsCount() + "件商品!");
             mv.addObject("url", CommUtil.getURL(request) + 
               "/seller/store_grade.htm");
             break;
           }
         }
         reader.close();
       }
     }
     catch (IOException e) {
       e.printStackTrace();
     }
     if (already_import_count > 0) {
       HashMap params = new HashMap();
       params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
       List alubms = this.albumService.query(
         "select obj from Album obj where obj.user.id=:user_id", 
         params, -1, -1);
       mv.addObject("alubms", alubms);
       mv.addObject("jsessionid", request.getSession().getId());
       request.getSession(false).setAttribute("taobao_goods_list", 
         taobao_goods_list);
       request.getSession(false).setAttribute("taobao_upload_status", 
         "upload_img");
       request.getSession(false).setAttribute("already_import_count", 
         Integer.valueOf(already_import_count));
       request.getSession(false).setAttribute("no_import_count", 
         Integer.valueOf(no_import_count));
     }
     mv.addObject("already_import_count", Integer.valueOf(already_import_count));
     mv.addObject("no_import_count", Integer.valueOf(no_import_count));
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="上传淘宝图片", value="/seller/taobao_img_upload.htm*", rtype="seller", rname="淘宝导入", rcode="taobao_seller", rgroup="淘宝管理")
   @RequestMapping({"/seller/taobao_img_upload.htm"})
   public void taobao_img_upload(HttpServletRequest request, HttpServletResponse response, String user_id, String album_id) {
     String csv_path = request.getSession().getServletContext()
       .getRealPath("") + 
       File.separator + "csv";
     try {
       String csvFilePath = csv_path + File.separator + "taobao.cvs";
       CsvReader reader = new CsvReader(csvFilePath, '\t', 
         Charset.forName("UTF-16LE"));
       reader.readHeaders();
       int goods_name_pos = 0;
       int goods_price_pos = 7;
       int goods_photo_pos = 35;
       User user = this.userService
         .getObjById(CommUtil.null2Long(user_id));
       Store store = this.userService.getObjById(user.getId()).getStore();
       StoreGrade grade = store.getGrade();
       String photo_path = this.storeTools.createUserFolder(request, 
         this.configService.getSysConfig(), user.getStore());
       String photo_url = this.storeTools.createUserFolderURL(
         this.configService.getSysConfig(), user.getStore());
       MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
       CommonsMultipartFile file = (CommonsMultipartFile)multipartRequest
         .getFile("imgFile");
       String upload_img_name = file.getOriginalFilename();
       upload_img_name = upload_img_name.substring(0, 
         upload_img_name.indexOf("."));
       double fileSize = Double.valueOf(file.getSize()).doubleValue();
       fileSize /= 1048576.0D;
       double csize = CommUtil.fileSize(new File(photo_path));
       double remainSpace = 0.0D;
       if (user.getStore().getGrade().getSpaceSize() != 0.0F)
         remainSpace = (user.getStore().getGrade().getSpaceSize() * 1024.0F - csize) * 1024.0D;
       else {
         remainSpace = 10000000.0D;
       }
       Map json_map = new HashMap();
       List<Goods> goods_list = (List)request.getSession(false)
         .getAttribute("taobao_goods_list");
       System.out.println(goods_list);
       System.out.println(SecurityUserHolder.getCurrentUser());
       Goods goods = new Goods();
       String[] photo_list = new String[0];
       while (reader.readRecord()) {
         if (reader.get(goods_photo_pos).indexOf(upload_img_name) >= 0) {
           String goods_name = reader.get(goods_name_pos);
           double goods_price = CommUtil.null2Double(reader
             .get(goods_price_pos));
           photo_list = reader.get(goods_photo_pos).split(";");
           for (Goods temp_goods : goods_list) {
             if ((!temp_goods.getGoods_name().equals(goods_name)) || 
               (CommUtil.null2Double(temp_goods
               .getGoods_price()) != goods_price)) continue;
             goods = temp_goods;
           }
         }
       }
 
       reader.close();
       if (goods != null)
       {
         if (remainSpace > fileSize) {
           try {
             Map map = CommUtil.saveFileToServer(request, "imgFile", 
               photo_path, upload_img_name + ".tbi", null);
             Map params = new HashMap();
             params.put("store_id", user.getStore().getId());
             List wms = this.waterMarkService
               .query("select obj from WaterMark obj where obj.store.id=:store_id", 
               params, -1, -1);
             if (wms.size() > 0) {
               WaterMark mark = (WaterMark)wms.get(0);
               if (mark.isWm_image_open()) {
                 String pressImg = request.getSession()
                   .getServletContext().getRealPath("") + 
                   File.separator + 
                   mark.getWm_image().getPath() + 
                   File.separator + 
                   mark.getWm_image().getName();
                 String targetImg = photo_path + File.separator + 
                   map.get("fileName");
                 int pos = mark.getWm_image_pos();
                 float alpha = mark.getWm_image_alpha();
                 CommUtil.waterMarkWithImage(pressImg, 
                   targetImg, pos, alpha);
               }
               if (mark.isWm_text_open()) {
                 String targetImg = photo_path + File.separator + 
                   map.get("fileName");
                 int pos = mark.getWm_text_pos();
                 String text = mark.getWm_text();
                 String markContentColor = mark
                   .getWm_text_color();
                 CommUtil.waterMarkWithText(
                   targetImg, 
                   targetImg, 
                   text, 
                   markContentColor, 
                   new Font(mark.getWm_text_font(), 
                   1, mark
                   .getWm_text_font_size()), 
                   pos, 100.0F);
               }
             }
             Accessory image = new Accessory();
             image.setAddTime(new Date());
             image.setExt((String)map.get("mime"));
             image.setPath(photo_url);
             image.setWidth(CommUtil.null2Int(map.get("width")));
             image.setHeight(CommUtil.null2Int(map.get("height")));
             image.setName(CommUtil.null2String(map.get("fileName")));
             image.setUser(user);
             Album album = null;
             if ((album_id != null) && (!album_id.equals(""))) {
               album = this.albumService.getObjById(
                 CommUtil.null2Long(album_id));
             } else {
               album = this.albumService.getDefaultAlbum(
                 CommUtil.null2Long(user_id));
               if (album == null) {
                 album = new Album();
                 album.setAddTime(new Date());
                 album.setAlbum_name("默认相册");
                 album.setAlbum_sequence(-10000);
                 album.setAlbum_default(true);
                 this.albumService.save(album);
               }
             }
             image.setAlbum(album);
             this.accessoryService.save(image);
             goods.getGoods_photos().add(image);
             this.goodsService.update(goods);
             json_map.put("url", CommUtil.getURL(request) + "/" + 
               photo_url + "/" + image.getName());
             json_map.put("id", image.getId());
             json_map.put("remainSpace", 
               Double.valueOf(remainSpace == 10000000.0D ? 0.0D : 
               remainSpace));
 
             String ext = image.getExt().indexOf(".") < 0 ? "." + 
               image.getExt() : image.getExt();
             String source = request.getSession()
               .getServletContext().getRealPath("/") + 
               image.getPath() + 
               File.separator + 
               image.getName();
             String target = source + "_small" + ext;
             CommUtil.createSmall(source, target, this.configService
               .getSysConfig().getSmallWidth(), 
               this.configService.getSysConfig()
               .getSmallHeight());
           }
           catch (IOException e) {
             e.printStackTrace();
           }
         } else {
           json_map.put("url", "");
           json_map.put("id", "");
           json_map.put("remainSpace", Integer.valueOf(0));
         }
       }
 
     }
     catch (IOException e)
     {
       e.printStackTrace();
     }
     request.getSession(false).setAttribute("taobao_upload_status", 
       "upload_finish");
   }
   @SecurityMapping(display = false, rsequence = 0, title="淘宝导入完成", value="/seller/taobao_import_finish.htm*", rtype="seller", rname="淘宝导入", rcode="taobao_seller", rgroup="淘宝管理")
   @RequestMapping({"/seller/taobao_import_finish.htm"})
   public ModelAndView taobao_import_finish(HttpServletRequest request, HttpServletResponse response) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/taobao_import_finish.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     request.getSession(false).removeAttribute("taobao_upload_status");
     request.getSession(false).removeAttribute("taobao_goods_list");
     request.getSession(false).removeAttribute("already_import_count");
     request.getSession(false).removeAttribute("no_import_count");
     return mv;
   }
 
   @RequestMapping({"/seller/taobao_authorize.htm"})
   public ModelAndView taobao_authorize(HttpServletRequest request, HttpServletResponse response, String code, String state)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/taobao_import_finish.html", 
       this.configService.getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
 
     return mv;
   }
 }


 
 
 