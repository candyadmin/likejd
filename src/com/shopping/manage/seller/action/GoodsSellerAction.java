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
import com.shopping.foundation.domain.Evaluate;
import com.shopping.foundation.domain.Goods;
import com.shopping.foundation.domain.GoodsBrand;
import com.shopping.foundation.domain.GoodsCart;
import com.shopping.foundation.domain.GoodsClass;
import com.shopping.foundation.domain.GoodsClassStaple;
import com.shopping.foundation.domain.GoodsSpecProperty;
import com.shopping.foundation.domain.GoodsSpecification;
import com.shopping.foundation.domain.GoodsTypeProperty;
import com.shopping.foundation.domain.OrderForm;
import com.shopping.foundation.domain.Report;
import com.shopping.foundation.domain.Store;
import com.shopping.foundation.domain.StoreGrade;
import com.shopping.foundation.domain.SysConfig;
import com.shopping.foundation.domain.Transport;
import com.shopping.foundation.domain.User;
import com.shopping.foundation.domain.UserGoodsClass;
import com.shopping.foundation.domain.WaterMark;
import com.shopping.foundation.domain.query.AccessoryQueryObject;
import com.shopping.foundation.domain.query.GoodsQueryObject;
import com.shopping.foundation.domain.query.ReportQueryObject;
import com.shopping.foundation.domain.query.TransportQueryObject;
import com.shopping.foundation.service.IAccessoryService;
import com.shopping.foundation.service.IAlbumService;
import com.shopping.foundation.service.IEvaluateService;
import com.shopping.foundation.service.IGoodsBrandService;
import com.shopping.foundation.service.IGoodsCartService;
import com.shopping.foundation.service.IGoodsClassService;
import com.shopping.foundation.service.IGoodsClassStapleService;
import com.shopping.foundation.service.IGoodsService;
import com.shopping.foundation.service.IGoodsSpecPropertyService;
import com.shopping.foundation.service.IGoodsTypePropertyService;
import com.shopping.foundation.service.IOrderFormLogService;
import com.shopping.foundation.service.IOrderFormService;
import com.shopping.foundation.service.IPaymentService;
import com.shopping.foundation.service.IReportService;
import com.shopping.foundation.service.IStoreService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.ITransportService;
import com.shopping.foundation.service.IUserConfigService;
import com.shopping.foundation.service.IUserGoodsClassService;
import com.shopping.foundation.service.IUserService;
import com.shopping.foundation.service.IWaterMarkService;
import com.shopping.lucene.LuceneUtil;
import com.shopping.lucene.LuceneVo;
import com.shopping.manage.admin.tools.StoreTools;
import com.shopping.manage.seller.Tools.TransportTools;
import com.shopping.view.web.tools.GoodsViewTools;
import com.shopping.view.web.tools.StoreViewTools;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GoodsSellerAction {

	@Autowired
	private ISysConfigService configService;

	@Autowired
	private IUserConfigService userConfigService;

	@Autowired
	private IGoodsClassService goodsClassService;

	@Autowired
	private IGoodsClassStapleService goodsclassstapleService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IAccessoryService accessoryService;

	@Autowired
	private IUserGoodsClassService userGoodsClassService;

	@Autowired
	private IGoodsService goodsService;

	@Autowired
	private IStoreService storeService;

	@Autowired
	private IGoodsBrandService goodsBrandService;

	@Autowired
	private IGoodsSpecPropertyService specPropertyService;

	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;

	@Autowired
	private IWaterMarkService waterMarkService;

	@Autowired
	private IGoodsCartService goodsCartService;

	@Autowired
	private IAlbumService albumService;

	@Autowired
	private IReportService reportService;

	@Autowired
	private IOrderFormService orderFormService;

	@Autowired
	private IOrderFormLogService orderFormLogService;

	@Autowired
	private IEvaluateService evaluateService;

	@Autowired
	private ITransportService transportService;

	@Autowired
	private IPaymentService paymentService;

	@Autowired
	private TransportTools transportTools;

	@Autowired
	private StoreTools storeTools;

	@Autowired
	private StoreViewTools storeViewTools;

	@Autowired
	private GoodsViewTools goodsViewTools;

	@Autowired
	private DatabaseTools databaseTools;

	@SecurityMapping(display = false, rsequence = 0, title = "发布商品第一步", value = "/seller/add_goods_first.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/add_goods_first.htm" })
	public ModelAndView add_goods_first(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		List payments = new ArrayList();
		Map params = new HashMap();
		if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
			params.put("type", "admin");
			params.put("install", Boolean.valueOf(true));
			payments = this.paymentService
					.query("select obj from Payment obj where obj.type=:type and obj.install=:install",
							params, -1, -1);
		} else {
			params.put("store_id", user.getStore().getId());
			params.put("install", Boolean.valueOf(true));
			payments = this.paymentService
					.query("select obj from Payment obj where obj.store.id=:store_id and obj.install=:install",
							params, -1, -1);
		}
		if (payments.size() == 0) {
			mv.addObject("op_title", "请至少开通一种支付方式");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/payment.htm");
			return mv;
		}
		request.getSession(false).removeAttribute("goods_class_info");
		int store_status = user.getStore() == null ? 0 : user.getStore()
				.getStore_status();
		if (store_status == 2) {
			StoreGrade grade = user.getStore().getGrade();
			int user_goods_count = user.getStore().getGoods_list().size();
			if ((grade.getGoodsCount() == 0)
					|| (user_goods_count < grade.getGoodsCount())) {
				List gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
								null, -1, -1);
				mv = new JModelAndView(
						"user/default/usercenter/add_goods_first.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				params.clear();
				params.put("store_id", user.getStore().getId());
				List staples = this.goodsclassstapleService
						.query("select obj from GoodsClassStaple obj where obj.store.id=:store_id order by obj.addTime desc",
								params, -1, -1);
				mv.addObject("staples", staples);
				mv.addObject("gcs", gcs);
				mv.addObject("id", CommUtil.null2String(id));
			} else {
				mv.addObject("op_title", "您的店铺等级只允许上传" + grade.getGoodsCount()
						+ "件商品!");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/store_grade.htm");
			}
		}
		if (store_status == 0) {
			mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		if (store_status == 1) {
			mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		if (store_status == 3) {
			mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "商品运费模板分页显示", value = "/seller/goods_transport.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/goods_transport.htm" })
	public ModelAndView goods_transport(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String ajax) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/goods_transport.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Boolean(ajax)) {
			mv = new JModelAndView(
					"user/default/usercenter/goods_transport_list.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		TransportQueryObject qo = new TransportQueryObject(currentPage, mv,
				orderBy, orderType);
		Store store = store = this.userService.getObjById(
				SecurityUserHolder.getCurrentUser().getId()).getStore();
		qo.addQuery("obj.store.id", new SysMap("store_id", store.getId()), "=");
		qo.setPageSize(Integer.valueOf(1));
		IPageList pList = this.transportService.list(qo);
		CommUtil.saveIPageList2ModelAndView(
				url + "/seller/goods_transport.htm", "", params, pList, mv);
		mv.addObject("transportTools", this.transportTools);
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "发布商品第二步", value = "/seller/add_goods_second.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/add_goods_second.htm" })
	public ModelAndView add_goods_second(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		int store_status = this.storeService.getObjByProperty("user.id", user.getId()).getStore_status();
		if (store_status == 2) {
			mv = new JModelAndView(
					"user/default/usercenter/add_goods_second.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			if (request.getSession(false).getAttribute("goods_class_info") != null) {
				GoodsClass gc = (GoodsClass) request.getSession(false)
						.getAttribute("goods_class_info");
				gc = this.goodsClassService.getObjById(gc.getId());
				String goods_class_info = generic_goods_class_info(gc);
				mv.addObject("goods_class",
						this.goodsClassService.getObjById(gc.getId()));
				mv.addObject("goods_class_info", goods_class_info.substring(0,
						goods_class_info.length() - 1));
				request.getSession(false).removeAttribute("goods_class_info");
			}
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ File.separator
					+ "upload"
					+ File.separator
					+ "store"
					+ File.separator + user.getStore().getId();
			double img_remain_size = 0.0D;
			if (user.getStore().getGrade().getSpaceSize() > 0.0F) {
				img_remain_size = user.getStore().getGrade().getSpaceSize()
						- CommUtil.div(Double.valueOf(CommUtil
								.fileSize(new File(path))), Integer
								.valueOf(1024));
			}
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("display", Boolean.valueOf(true));
			List ugcs = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
							params, -1, -1);
			List gbs = this.goodsBrandService.query(
					"select obj from GoodsBrand obj order by obj.sequence asc",
					null, -1, -1);
			mv.addObject("gbs", gbs);
			mv.addObject("ugcs", ugcs);
			mv.addObject("img_remain_size", Double.valueOf(img_remain_size));
			mv.addObject("imageSuffix", this.storeViewTools
					.genericImageSuffix(this.configService.getSysConfig()
							.getImageSuffix()));
			String goods_session = CommUtil.randomString(32);
			mv.addObject("goods_session", goods_session);
			request.getSession(false).setAttribute("goods_session",
					goods_session);
		}
		if (store_status == 0) {
			mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		if (store_status == 1) {
			mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		if (store_status == 3) {
			mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "产品规格显示", value = "/seller/goods_inventory.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/goods_inventory.htm" })
	public ModelAndView goods_inventory(HttpServletRequest request,
			HttpServletResponse response, String goods_spec_ids) {
		ModelAndView mv = mv = new JModelAndView(
				"user/default/usercenter/goods_inventory.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String[] spec_ids = goods_spec_ids.split(",");
		List<GoodsSpecProperty> gsps = new ArrayList();
		// GoodsSpecProperty gsp;
		for (String spec_id : spec_ids) {
			if (!spec_id.equals("")) {
				GoodsSpecProperty gsp = this.specPropertyService
						.getObjById(Long.valueOf(Long.parseLong(spec_id)));
				gsps.add(gsp);
			}
		}
		Set<GoodsSpecification> specs = new HashSet<GoodsSpecification>();
		for (GoodsSpecProperty gsp : gsps) {
			specs.add(gsp.getSpec());
		}
		for (GoodsSpecification spec : specs) {
			spec.getProperties().clear();
			for (GoodsSpecProperty gsp : gsps) {
				if (gsp.getSpec().getId().equals(spec.getId())) {
					spec.getProperties().add(gsp);
				}
			}
		}
		GoodsSpecification[] spec_list = (GoodsSpecification[]) specs
				.toArray(new GoodsSpecification[specs.size()]);
		Arrays.sort(spec_list, new Comparator() {
			public int compare(Object obj1, Object obj2) {
				GoodsSpecification a = (GoodsSpecification) obj1;
				GoodsSpecification b = (GoodsSpecification) obj2;
				if (a.getSequence() == b.getSequence()) {
					return 0;
				}
				return a.getSequence() > b.getSequence() ? 1 : -1;
			}
		});
		List gsp_list = generic_spec_property(specs);
		mv.addObject("specs", Arrays.asList(spec_list));
		mv.addObject("gsps", gsp_list);
		return mv;
	}

	public static GoodsSpecProperty[][] list2group(
			List<List<GoodsSpecProperty>> list) {
		GoodsSpecProperty[][] gps = new GoodsSpecProperty[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			gps[i] = ((GoodsSpecProperty[]) ((List) list.get(i))
					.toArray(new GoodsSpecProperty[((List) list.get(i)).size()]));
		}
		return gps;
	}

	private List<List<GoodsSpecProperty>> generic_spec_property(
			Set<GoodsSpecification> specs) {
		List result_list = new ArrayList();
		List list = new ArrayList();
		int max = 1;
		for (GoodsSpecification spec : specs) {
			list.add(spec.getProperties());
		}

		GoodsSpecProperty[][] gsps = list2group(list);
		for (int i = 0; i < gsps.length; i++) {
			max *= gsps[i].length;
		}
		for (int i = 0; i < max; i++) {
			List temp_list = new ArrayList();
			int temp = 1;
			for (int j = 0; j < gsps.length; j++) {
				temp *= gsps[j].length;
				temp_list.add(j, gsps[j][(i / (max / temp) % gsps[j].length)]);
			}
			GoodsSpecProperty[] temp_gsps = (GoodsSpecProperty[]) temp_list
					.toArray(new GoodsSpecProperty[temp_list.size()]);
			Arrays.sort(temp_gsps, new Comparator() {
				public int compare(Object obj1, Object obj2) {
					GoodsSpecProperty a = (GoodsSpecProperty) obj1;
					GoodsSpecProperty b = (GoodsSpecProperty) obj2;
					if (a.getSpec().getSequence() == b.getSpec().getSequence()) {
						return 0;
					}
					return a.getSpec().getSequence() > b.getSpec()
							.getSequence() ? 1 : -1;
				}
			});
			result_list.add(Arrays.asList(temp_gsps));
		}
		return result_list;
	}

	@RequestMapping({ "/seller/swf_upload.htm" })
	public void swf_upload(HttpServletRequest request,
			HttpServletResponse response, String user_id, String album_id) {
		
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		String path = this.storeTools.createUserFolder(request, this.configService.getSysConfig(), user.getStore());
		String url = this.storeTools.createUserFolderURL(this.configService.getSysConfig(), user.getStore());
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("imgFile");
		double fileSize = Double.valueOf(file.getSize()).doubleValue();
		fileSize /= 1048576.0D;
		double csize = CommUtil.fileSize(new File(path));
		double remainSpace = 0.0D;
		if (user.getStore().getGrade().getSpaceSize() != 0.0F)
			remainSpace = (user.getStore().getGrade().getSpaceSize() * 1024.0F - csize) * 1024.0D;
		else {
			remainSpace = 10000000.0D;
		}
		Map json_map = new HashMap();
		if (remainSpace > fileSize) {
			try {
				Map map = CommUtil.saveFileToServer(request, "imgFile", path, null, null);
				Map params = new HashMap();
				params.put("store_id", user.getStore().getId());
				List wms = this.waterMarkService
						.query("select obj from WaterMark obj where obj.store.id=:store_id", params, -1, -1);
				if (wms.size() > 0) {
					WaterMark mark = (WaterMark) wms.get(0);
					if (mark.isWm_image_open()) {
						String pressImg = request.getSession().getServletContext().getRealPath("")
								+ File.separator + mark.getWm_image().getPath()
								+ File.separator + mark.getWm_image().getName();
						String targetImg = path + File.separator + map.get("fileName");
						int pos = mark.getWm_image_pos();
						float alpha = mark.getWm_image_alpha();
						CommUtil.waterMarkWithImage(pressImg, targetImg, pos, alpha);
					}
					if (mark.isWm_text_open()) {
						String targetImg = path + File.separator + map.get("fileName");
						int pos = mark.getWm_text_pos();
						String text = mark.getWm_text();
						String markContentColor = mark.getWm_text_color();
						CommUtil.waterMarkWithText(
								targetImg, targetImg, text, markContentColor,
								new Font(mark.getWm_text_font(), 1, mark.getWm_text_font_size()), pos, 100.0F);
					}
				}
				Accessory image = new Accessory();
				image.setAddTime(new Date());
				image.setExt((String) map.get("mime"));
				image.setPath(url);
				image.setWidth(CommUtil.null2Int(map.get("width")));
				image.setHeight(CommUtil.null2Int(map.get("height")));
				image.setName(CommUtil.null2String(map.get("fileName")));
				image.setUser(user);
				Album album = null;
				if ((album_id != null) && (!album_id.equals(""))) {
					album = this.albumService.getObjById(CommUtil.null2Long(album_id));
				} else {
					album = this.albumService.getDefaultAlbum(CommUtil.null2Long(user_id));
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
				json_map.put("url", CommUtil.getURL(request) + "/" + url + "/" + image.getName());
				json_map.put("id", image.getId());
				json_map.put("remainSpace", Double.valueOf(remainSpace == 10000.0D ? 0.0D : remainSpace));

				String ext = image.getExt().indexOf(".") < 0 ? "." + image.getExt() : image.getExt();
				String source = request.getSession().getServletContext().getRealPath("/")
						+ image.getPath() + File.separator + image.getName();
				String target = source + "_small" + ext;
				CommUtil.createSmall(source, target, this.configService
						.getSysConfig().getSmallWidth(), this.configService
						.getSysConfig().getSmallHeight());

				String midext = image.getExt().indexOf(".") < 0 ? "." + image.getExt() : image.getExt();
				String midtarget = source + "_middle" + ext;
				CommUtil.createSmall(source, midtarget, this.configService
						.getSysConfig().getMiddleWidth(), this.configService
						.getSysConfig().getMiddleHeight());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			json_map.put("url", "");
			json_map.put("id", "");
			json_map.put("remainSpace", Integer.valueOf(0));
		}

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(Json.toJson(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping({"/seller/upload.htm"})
    public void upload(HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException {
		
		/*String saveFilePathName = request.getSession().getServletContext().getRealPath("/") + 
				this.configService.getSysConfig().getUploadFilePath() + File.separator + "common";*/
		Store store = SecurityUserHolder.getCurrentUser().getStore();
		
		String saveFilePathName = this.storeTools.createUserFolder(request, this.configService.getSysConfig(), store);
		
		String url = this.storeTools.createUserFolderURL(this.configService.getSysConfig(), store);
		
		String webPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
     
	    if ((this.configService.getSysConfig().getAddress() != null) && 
	       (!this.configService.getSysConfig().getAddress().equals(""))) {
	       webPath = this.configService.getSysConfig().getAddress() + webPath;
	    }
	    JSONObject obj = new JSONObject();
	    try {
	       Map map = CommUtil.saveFileToServer(request, "imgFile", saveFilePathName, null, null);
	       //String url = webPath + "/" + this.configService.getSysConfig().getUploadFilePath() + "/common/" + map.get("fileName");
	       url = CommUtil.getURL(request) + "/" + url + "/" + map.get("fileName");
	       obj.put("error", Integer.valueOf(0));
	       obj.put("url", url);
	    } catch (IOException e) {
	       obj.put("error", Integer.valueOf(1));
	       obj.put("message", e.getMessage());
	       e.printStackTrace();
	    }
	    response.setContentType("text/html");
	    response.setHeader("Cache-Control", "no-cache");
	    response.setCharacterEncoding("UTF-8");
	    try {
	       PrintWriter writer = response.getWriter();
	       writer.print(obj.toJSONString());
	    } catch (IOException e) {
	       e.printStackTrace();
	    }
   }

	@SecurityMapping(display = false, rsequence = 0, title = "商品图片删除", value = "/seller/goods_image_del.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/goods_image_del.htm" })
	public void goods_image_del(HttpServletRequest request,
			HttpServletResponse response, String image_id) {
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		String path = this.storeTools.createUserFolder(request, this.configService.getSysConfig(), user.getStore());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			Map map = new HashMap();
			Accessory img = this.accessoryService.getObjById(CommUtil.null2Long(image_id));
			for (Goods goods : img.getGoods_main_list()) {
				goods.setGoods_main_photo(null);
				this.goodsService.update(goods);
			}
			for (Goods goods1 : img.getGoods_list()) {
				goods1.getGoods_photos().remove(img);
				this.goodsService.update(goods1);
			}
			boolean ret = this.accessoryService.delete(img.getId());
			if (ret) {
				CommUtil.del_acc(request, img);
			}
			double csize = CommUtil.fileSize(new File(path));
			double remainSpace = 10000.0D;
			if (user.getStore().getGrade().getSpaceSize() != 0.0F) {
				remainSpace = CommUtil.div(Double.valueOf(user.getStore().getGrade().getSpaceSize()
								* 1024.0F - csize), Integer.valueOf(1024));
			}
			map.put("result", Boolean.valueOf(ret));
			map.put("remainSpace", Double.valueOf(remainSpace));
			PrintWriter writer = response.getWriter();
			writer.print(Json.toJson(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String clearContent(String inputString) {
		String htmlStr = inputString;
		String textStr = "";
		try {
			String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>";
			String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>";
			String regEx_html = "<[^>]+>";
			String regEx_html1 = "<[^>]+";
			Pattern p_script = Pattern.compile(regEx_script, 2);
			Matcher m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll("");

			Pattern p_style = Pattern.compile(regEx_style, 2);
			Matcher m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll("");

			Pattern p_html = Pattern.compile(regEx_html, 2);
			Matcher m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll("");

			Pattern p_html1 = Pattern.compile(regEx_html1, 2);
			Matcher m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll("");

			textStr = htmlStr;
		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}
		return textStr;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "发布商品第三步", value = "/seller/add_goods_finish.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/add_goods_finish.htm" })
	public ModelAndView add_goods_finish(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_class_id,
			String image_ids, String goods_main_img_id, String user_class_ids,
			String goods_brand_id, String goods_spec_ids,
			String goods_properties, String intentory_details,
			String goods_session, String transport_type, String transport_id) {
		ModelAndView mv = null;
		String goods_session1 = CommUtil.null2String(request.getSession(false).getAttribute("goods_session"));
		if (goods_session1.equals("")) {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "禁止重复提交表单");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		} else if (goods_session1.equals(goods_session)) {
			if ((id == null) || (id.equals(""))) {
				mv = new JModelAndView("user/default/usercenter/add_goods_finish.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
			} else {
				mv = new JModelAndView("success.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "商品编辑成功");
				mv.addObject("url", CommUtil.getURL(request) + "/goods_" + id + ".htm");
			}
			WebForm wf = new WebForm();
			Goods goods = null;
			if (id.equals("")) {
				goods = (Goods) wf.toPo(request, Goods.class);
				goods.setAddTime(new Date());
				User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
				goods.setGoods_store(user.getStore());
			} else {
				Goods obj = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
				goods = (Goods) wf.toPo(request, obj);
			}
			if ((goods.getCombin_status() != 2)
					&& (goods.getDelivery_status() != 2)
					&& (goods.getBargain_status() != 2)
					&& (goods.getActivity_status() != 2)) {
				goods.setGoods_current_price(goods.getStore_price());
			}
			goods.setGoods_name(clearContent(goods.getGoods_name()));
			GoodsClass gc = this.goodsClassService.getObjById(Long.valueOf(Long.parseLong(goods_class_id)));
			goods.setGc(gc);
			Accessory main_img = null;
			if ((goods_main_img_id != null) && (!goods_main_img_id.equals(""))) {
				main_img = this.accessoryService.getObjById(Long.valueOf(Long.parseLong(goods_main_img_id)));
			}
			goods.setGoods_main_photo(main_img);
			goods.getGoods_ugcs().clear();
			String[] ugc_ids = user_class_ids.split(",");
			// String[] arrayOfString1;
			// UserGoodsClass localUserGoodsClass2 = (arrayOfString1 =
			// ugc_ids).length;
			for (int i = 0; i < ugc_ids.length; i++) {
				String ugc_id = ugc_ids[i];
				if (!ugc_id.equals("")) {
					UserGoodsClass ugc = this.userGoodsClassService
							.getObjById(Long.valueOf(Long.parseLong(ugc_id)));
					goods.getGoods_ugcs().add(ugc);
				}
			}
			String[] img_ids = image_ids.split(",");
			goods.getGoods_photos().clear();
			// UserGoodsClass localUserGoodsClass3 = (ugc = img_ids).length;
			for (int i = 0; i < img_ids.length; i++) {
				String img_id = img_ids[i];
				if (!img_id.equals("")) {
					Accessory img = this.accessoryService.getObjById(Long.valueOf(Long.parseLong(img_id)));
					goods.getGoods_photos().add(img);
				}
			}
			if ((goods_brand_id != null) && (!goods_brand_id.equals(""))) {
				GoodsBrand goods_brand = this.goodsBrandService.getObjById(Long.valueOf(Long.parseLong(goods_brand_id)));
				goods.setGoods_brand(goods_brand);
			}
			goods.getGoods_specs().clear();
			String[] spec_ids = goods_spec_ids.split(",");
			// UserGoodsClass ugc = (img = spec_ids).length;
			for (int i = 0; i < spec_ids.length; i++) {
				String spec_id = spec_ids[i];
				if (!spec_id.equals("")) {
					GoodsSpecProperty gsp = this.specPropertyService.getObjById(Long.valueOf(Long.parseLong(spec_id)));
					goods.getGoods_specs().add(gsp);
				}
			}
			Object maps = new ArrayList();
			String[] properties = goods_properties.split(";");
			// String[] arrayOfString2;
			// GoodsSpecProperty gsp = (arrayOfString2 = properties).length;
			String[] list;
			for (int i = 0; i < properties.length; i++) {
				String property = properties[i];
				if (!property.equals("")) {
					list = property.split(",");
					Map map = new HashMap();
					map.put("id", list[0]);
					map.put("val", list[1]);
					map.put("name", this.goodsTypePropertyService.getObjById(Long.valueOf(Long.parseLong(list[0]))).getName());
					((List) maps).add(map);
				}
			}
			goods.setGoods_property(Json.toJson(maps, JsonFormat.compact()));
			((List) maps).clear();
			String[] inventory_list = intentory_details.split(";");
			// GoodsSpecProperty localGoodsSpecProperty1 = (list =
			// inventory_list).length;
			for (int i = 0; i < inventory_list.length; i++) {
				String inventory = inventory_list[i];
				if (!inventory.equals("")) {
					String[] list1 = inventory.split(",");
					Map map = new HashMap();
					map.put("id", list1[0]);
					map.put("count", list1[1]);
					map.put("price", list1[2]);
					((List) maps).add(map);
				}
			}
			goods.setGoods_inventory_detail(Json.toJson(maps, JsonFormat.compact()));
			if (CommUtil.null2Int(transport_type) == 0) {
				Transport trans = this.transportService.getObjById(CommUtil.null2Long(transport_id));
				goods.setTransport(trans);
			}
			if (CommUtil.null2Int(transport_type) == 1) {
				goods.setTransport(null);
			}
			if (id.equals("")) {
				this.goodsService.save(goods);

				String goods_lucene_path = System.getProperty("user.dir") + File.separator + "luence" + File.separator + "goods";
				File file = new File(goods_lucene_path);
				if (!file.exists()) {
					CommUtil.createFolder(goods_lucene_path);
				}
				LuceneVo vo = new LuceneVo();
				vo.setVo_id(goods.getId());
				vo.setVo_title(goods.getGoods_name());
				vo.setVo_content(goods.getGoods_details());
				vo.setVo_type("goods");
				vo.setVo_store_price(CommUtil.null2Double(goods.getStore_price()));
				vo.setVo_add_time(goods.getAddTime().getTime());
				vo.setVo_goods_salenum(goods.getGoods_salenum());
				LuceneUtil lucene = LuceneUtil.instance();
				LuceneUtil.setIndex_path(goods_lucene_path);
				lucene.writeIndex(vo);
			} else {
				this.goodsService.update(goods);

				String goods_lucene_path = System.getProperty("user.dir")
						+ File.separator + "luence" + File.separator + "goods";
				File file = new File(goods_lucene_path);
				if (!file.exists()) {
					CommUtil.createFolder(goods_lucene_path);
				}
				LuceneVo vo = new LuceneVo();
				vo.setVo_id(goods.getId());
				vo.setVo_title(goods.getGoods_name());
				vo.setVo_content(goods.getGoods_details());
				vo.setVo_type("goods");
				vo.setVo_store_price(CommUtil.null2Double(goods.getStore_price()));
				vo.setVo_add_time(goods.getAddTime().getTime());
				vo.setVo_goods_salenum(goods.getGoods_salenum());
				LuceneUtil lucene = LuceneUtil.instance();
				LuceneUtil.setIndex_path(goods_lucene_path);
				lucene.update(CommUtil.null2String(goods.getId()), vo);
			}
			mv.addObject("obj", goods);
			request.getSession(false).removeAttribute("goods_session");
		} else {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}

		return (ModelAndView) mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "加载商品分类", value = "/seller/load_goods_class.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/load_goods_class.htm" })
	public void load_goods_class(HttpServletRequest request,
			HttpServletResponse response, String pid, String session) {
		GoodsClass obj = this.goodsClassService.getObjById(CommUtil.null2Long(pid));
		List list = new ArrayList();
		if (obj != null) {
			for (GoodsClass gc : obj.getChilds()) {
				Map map = new HashMap();
				map.put("id", gc.getId());
				map.put("className", gc.getClassName());
				list.add(map);
			}
			if (CommUtil.null2Boolean(session)) {
				request.getSession(false).setAttribute("goods_class_info", obj);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(Json.toJson(list));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SecurityMapping(display = false, rsequence = 0, title = "添加用户常用商品分类", value = "/seller/load_goods_class.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/add_goods_class_staple.htm" })
	public void add_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response) {
		String ret = "error";
		if (request.getSession(false).getAttribute("goods_class_info") != null) {
			GoodsClass gc = (GoodsClass) request.getSession(false)
					.getAttribute("goods_class_info");
			Map params = new HashMap();
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			params.put("store_id", user.getStore().getId());
			params.put("gc_id", gc.getId());
			List gcs = this.goodsclassstapleService
					.query("select obj from GoodsClassStaple obj where obj.store.id=:store_id and obj.gc.id=:gc_id",
							params, -1, -1);
			if (gcs.size() == 0) {
				GoodsClassStaple staple = new GoodsClassStaple();
				staple.setAddTime(new Date());
				staple.setGc(gc);
				String name = generic_goods_class_info(gc);
				staple.setName(name.substring(0, name.length() - 1));
				staple.setStore(user.getStore());
				boolean flag = this.goodsclassstapleService.save(staple);
				if (flag) {
					Map map = new HashMap();
					map.put("name", staple.getName());
					map.put("id", staple.getId());
					ret = Json.toJson(map);
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SecurityMapping(display = false, rsequence = 0, title = "删除用户常用商品分类", value = "/seller/del_goods_class_staple.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/del_goods_class_staple.htm" })
	public void del_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = this.goodsclassstapleService.delete(Long.valueOf(Long
				.parseLong(id)));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SecurityMapping(display = false, rsequence = 0, title = "根据用户常用商品分类加载分类信息", value = "/seller/del_goods_class_staple.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/load_goods_class_staple.htm" })
	public void load_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response, String id, String name) {
		GoodsClass obj = null;
		if ((id != null) && (!id.equals("")))
			obj = this.goodsclassstapleService.getObjById(Long.valueOf(Long.parseLong(id))).getGc();
		if ((name != null) && (!name.equals("")))
			obj = this.goodsClassService.getObjByProperty("className", name);
		List list = new ArrayList();
		if (obj != null) {
			request.getSession(false).setAttribute("goods_class_info", obj);
			Map params = new HashMap();
			List second_list = new ArrayList();
			List third_list = new ArrayList();
			List other_list = new ArrayList();
			Object gc;
			if (obj.getLevel() == 2) {
				params.put("pid", obj.getParent().getParent().getId());
				List<GoodsClass> second_gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc", params, -1, -1);
				for (GoodsClass gc1 : second_gcs) {
					Map map = new HashMap();
					map.put("id", gc1.getId());
					map.put("className", gc1.getClassName());
					second_list.add(map);
				}
				params.clear();
				params.put("pid", obj.getParent().getId());
				List<GoodsClass> third_gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc", params, -1, -1);
				for (GoodsClass gc1 : third_gcs) { // gc = (GoodsClass)map.next();
					Map map = new HashMap();
					map.put("id", ((GoodsClass) gc1).getId());
					map.put("className", ((GoodsClass) gc1).getClassName());
					third_list.add(map);
				}
			}

			if (obj.getLevel() == 1) {
				params.clear();
				params.put("pid", obj.getParent().getId());
				List<GoodsClass> third_gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc", params, -1, -1);
				for (GoodsClass gc1 : third_gcs) {
					// GoodsClass gc = (GoodsClass)((Iterator)gc).next();
					Map map = new HashMap();
					map.put("id", gc1.getId());
					map.put("className", gc1.getClassName());
					second_list.add(map);
				}
			}

			Map map = new HashMap();
			String staple_info = generic_goods_class_info(obj);
			map.put("staple_info", staple_info.substring(0, staple_info.length() - 1));
			other_list.add(map);

			list.add(second_list);
			list.add(third_list);
			list.add(other_list);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(Json.toJson(list, JsonFormat.compact()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String generic_goods_class_info(GoodsClass gc) {
		String goods_class_info = gc.getClassName() + ">";
		if (gc.getParent() != null) {
			String class_info = generic_goods_class_info(gc.getParent());
			goods_class_info = class_info + goods_class_info;
		}
		return goods_class_info;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "出售中的商品列表", value = "/seller/goods.htm*", rtype = "seller", rname = "出售中的商品", rcode = "goods_list_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/goods.htm" })
	public ModelAndView goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String user_class_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.goods_status",
				new SysMap("goods_status", Integer.valueOf(0)), "=");
		qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user
				.getStore().getId()), "=");
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		if ((goods_name != null) && (!goods_name.equals(""))) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
		}
		if ((user_class_id != null) && (!user_class_id.equals(""))) {
			UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long
					.valueOf(Long.parseLong(user_class_id)));
			qo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/seller/goods.htm", "",
				params, pList, mv);
		mv.addObject("storeTools", this.storeTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "仓库中的商品列表", value = "/seller/goods_storage.htm*", rtype = "seller", rname = "仓库中的商品", rcode = "goods_storage_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/goods_storage.htm" })
	public ModelAndView goods_storage(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String user_class_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/goods_storage.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.goods_status",
				new SysMap("goods_status", Integer.valueOf(1)), "=");
		qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user
				.getStore().getId()), "=");
		qo.setOrderBy("goods_seller_time");
		qo.setOrderType("desc");
		if ((goods_name != null) && (!goods_name.equals(""))) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
		}
		if ((user_class_id != null) && (!user_class_id.equals(""))) {
			UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long
					.valueOf(Long.parseLong(user_class_id)));
			qo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/seller/goods_storage.htm",
				"", params, pList, mv);
		mv.addObject("storeTools", this.storeTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "违规下架商品", value = "/seller/goods_out.htm*", rtype = "seller", rname = "违规下架商品", rcode = "goods_out_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/goods_out.htm" })
	public ModelAndView goods_out(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String user_class_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/goods_out.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.goods_status",
				new SysMap("goods_status", Integer.valueOf(-2)), "=");
		qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user
				.getStore().getId()), "=");
		qo.setOrderBy("goods_seller_time");
		qo.setOrderType("desc");
		if ((goods_name != null) && (!goods_name.equals(""))) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
		}
		if ((user_class_id != null) && (!user_class_id.equals(""))) {
			UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long
					.valueOf(Long.parseLong(user_class_id)));
			qo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/seller/goods_out.htm", "",
				params, pList, mv);
		mv.addObject("storeTools", this.storeTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "商品编辑", value = "/seller/goods_edit.htm*", rtype = "seller", rname = "商品编辑", rcode = "goods_edit_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/goods_edit.htm" })
	public ModelAndView goods_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/add_goods_second.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Goods obj = this.goodsService.getObjById(Long.valueOf(Long
				.parseLong(id)));

		if (obj.getGoods_store().getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ File.separator
					+ "upload"
					+ File.separator
					+ "store"
					+ File.separator + user.getStore().getId();
			double img_remain_size = user.getStore().getGrade().getSpaceSize()
					- CommUtil.div(
							Double.valueOf(CommUtil.fileSize(new File(path))),
							Integer.valueOf(1024));
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("display", Boolean.valueOf(true));
			List ugcs = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
							params, -1, -1);
			AccessoryQueryObject aqo = new AccessoryQueryObject();
			aqo.setPageSize(Integer.valueOf(8));
			aqo.addQuery("obj.user.id", new SysMap("user_id", user.getId()),
					"=");
			aqo.setOrderBy("addTime");
			aqo.setOrderType("desc");
			IPageList pList = this.accessoryService.list(aqo);
			String photo_url = CommUtil.getURL(request)
					+ "/seller/load_photo.htm";
			List gbs = this.goodsBrandService.query(
					"select obj from GoodsBrand obj order by obj.sequence asc",
					null, -1, -1);
			mv.addObject("gbs", gbs);
			mv.addObject("photos", pList.getResult());
			mv.addObject(
					"gotoPageAjaxHTML",
					CommUtil.showPageAjaxHtml(photo_url, "",
							pList.getCurrentPage(), pList.getPages()));
			mv.addObject("ugcs", ugcs);
			mv.addObject("img_remain_size", Double.valueOf(img_remain_size));
			mv.addObject("obj", obj);
			if (request.getSession(false).getAttribute("goods_class_info") != null) {
				GoodsClass session_gc = (GoodsClass) request.getSession(false)
						.getAttribute("goods_class_info");
				GoodsClass gc = this.goodsClassService.getObjById(session_gc
						.getId());
				mv.addObject("goods_class_info",
						this.storeTools.generic_goods_class_info(gc));
				mv.addObject("goods_class", gc);
				request.getSession(false).removeAttribute("goods_class_info");
			} else if (obj.getGc() != null) {
				mv.addObject("goods_class_info",
						this.storeTools.generic_goods_class_info(obj.getGc()));
				mv.addObject("goods_class", obj.getGc());
			}

			String goods_session = CommUtil.randomString(32);
			mv.addObject("goods_session", goods_session);
			request.getSession(false).setAttribute("goods_session",
					goods_session);
			mv.addObject("imageSuffix", this.storeViewTools
					.genericImageSuffix(this.configService.getSysConfig()
							.getImageSuffix()));
		} else {
			mv = new JModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有该商品信息！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "商品上下架", value = "/seller/goods_sale.htm*", rtype = "seller", rname = "商品上下架", rcode = "goods_sale_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/goods_sale.htm" })
	public String goods_sale(HttpServletRequest request,
			HttpServletResponse response, String mulitId) {
		String url = "/seller/goods.htm";
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Goods goods = this.goodsService.getObjById(Long.valueOf(Long
						.parseLong(id)));

				if (goods.getGoods_store().getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
					int goods_status = goods.getGoods_status() == 0 ? 1 : 0;
					goods.setGoods_status(goods_status);
					this.goodsService.update(goods);
					if (goods_status == 0) {
						url = "/seller/goods_storage.htm";

						String goods_lucene_path = System
								.getProperty("user.dir")
								+ File.separator
								+ "luence" + File.separator + "goods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneVo vo = new LuceneVo();
						vo.setVo_id(goods.getId());
						vo.setVo_title(goods.getGoods_name());
						vo.setVo_content(goods.getGoods_details());
						vo.setVo_type("goods");
						vo.setVo_store_price(CommUtil.null2Double(goods
								.getStore_price()));
						vo.setVo_add_time(goods.getAddTime().getTime());
						vo.setVo_goods_salenum(goods.getGoods_salenum());
						LuceneUtil lucene = LuceneUtil.instance();
						LuceneUtil.setIndex_path(goods_lucene_path);
						lucene.update(CommUtil.null2String(goods.getId()), vo);
					} else {
						String goods_lucene_path = System
								.getProperty("user.dir")
								+ File.separator
								+ "luence" + File.separator + "goods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.delete_index(CommUtil.null2String(goods.getId()));
					}
				}
			}
		}
		return "redirect:" + url;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "商品删除", value = "/seller/goods_del.htm*", rtype = "seller", rname = "商品删除", rcode = "goods_del_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/goods_del.htm" })
	public String goods_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String op) {
		String url = "/seller/goods.htm";
		if (CommUtil.null2String(op).equals("storage")) {
			url = "/seller/goods_storage.htm";
		}
		if (CommUtil.null2String(op).equals("out")) {
			url = "/seller/goods_out.htm";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(id));

				if (goods.getGoods_store().getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
					Map map = new HashMap();
					map.put("gid", goods.getId());
					List<GoodsCart> goodCarts = this.goodsCartService
							.query("select obj from GoodsCart obj where obj.goods.id = :gid",
									map, -1, -1);
					Long ofid = null;
					Long of_id;
					for (GoodsCart gc : goodCarts) {
						of_id = gc.getOf().getId();
						this.goodsCartService.delete(gc.getId());
						OrderForm of = this.orderFormService.getObjById(of_id);
						if (of.getGcs().size() == 0) {
							this.orderFormService.delete(of_id);
						}
					}

					List<Evaluate> evaluates = goods.getEvaluates();
					for (Evaluate e : evaluates) {
						this.evaluateService.delete(e.getId());
					}
					goods.getGoods_ugcs().clear();
					goods.getGoods_ugcs().clear();
					goods.getGoods_photos().clear();
					goods.getGoods_ugcs().clear();
					goods.getGoods_specs().clear();
					this.goodsService.delete(goods.getId());

					String goods_lucene_path = System.getProperty("user.dir")
							+ File.separator + "luence" + File.separator
							+ "goods";
					File file = new File(goods_lucene_path);
					if (!file.exists()) {
						CommUtil.createFolder(goods_lucene_path);
					}
					LuceneUtil lucene = LuceneUtil.instance();
					LuceneUtil.setIndex_path(goods_lucene_path);
					lucene.delete_index(CommUtil.null2String(id));
				}
			}
		}
		return "redirect:" + url;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "被举报禁售商品", value = "/seller/goods_report.htm*", rtype = "seller", rname = "被举报禁售商品", rcode = "goods_report_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/goods_report.htm" })
	public ModelAndView goods_report(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/goods_report.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReportQueryObject qo = new ReportQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.goods.goods_store.user.id", new SysMap("user_id",
				SecurityUserHolder.getCurrentUser().getId()), "=");
		qo.addQuery("obj.result", new SysMap("result", Integer.valueOf(1)), "=");
		IPageList pList = this.reportService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "举报图片查看", value = "/seller/report_img.htm*", rtype = "seller", rname = "被举报禁售商品", rcode = "goods_report_seller", rgroup = "商品管理")
	@RequestMapping({ "/seller/report_img.htm" })
	public ModelAndView report_img(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/report_img.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Report obj = this.reportService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		return mv;
	}

	@RequestMapping({ "/seller/goods_img_album.htm" })
	public ModelAndView goods_img_album(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/" + type
				+ ".html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AccessoryQueryObject aqo = new AccessoryQueryObject(currentPage, mv,
				"addTime", "desc");
		aqo.setPageSize(Integer.valueOf(16));
		aqo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		aqo.setOrderBy("addTime");
		aqo.setOrderType("desc");
		IPageList pList = this.accessoryService.list(aqo);
		String photo_url = CommUtil.getURL(request)
				+ "/seller/goods_img_album.htm";
		mv.addObject("photos", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url,
				"", pList.getCurrentPage(), pList.getPages()));

		return mv;
	}
}
