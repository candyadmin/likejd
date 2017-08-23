package com.shopping.view.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jdom.JDOMException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.annotation.SecurityMapping;
import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.mv.JModelAndView;
import com.shopping.core.query.support.IPageList;
import com.shopping.core.security.support.SecurityUserHolder;
import com.shopping.core.tools.CommUtil;
import com.shopping.core.tools.QRCodeEncoderHandler;
import com.shopping.core.tools.WebForm;
import com.shopping.core.tools.WxAdvancedUtil;
import com.shopping.core.tools.WxCommonUtil;
import com.shopping.core.tools.bean.WxOauth2Token;
import com.shopping.core.tools.bean.WxToken;
import com.shopping.foundation.domain.Address;
import com.shopping.foundation.domain.Area;
import com.shopping.foundation.domain.CouponInfo;
import com.shopping.foundation.domain.Goods;
import com.shopping.foundation.domain.GoodsCart;
import com.shopping.foundation.domain.GoodsSpecProperty;
import com.shopping.foundation.domain.GroupGoods;
import com.shopping.foundation.domain.OrderForm;
import com.shopping.foundation.domain.OrderFormLog;
import com.shopping.foundation.domain.Payment;
import com.shopping.foundation.domain.PredepositLog;
import com.shopping.foundation.domain.Store;
import com.shopping.foundation.domain.StoreCart;
import com.shopping.foundation.domain.User;
import com.shopping.foundation.domain.query.AddressQueryObject;
import com.shopping.foundation.service.IAddressService;
import com.shopping.foundation.service.IAreaService;
import com.shopping.foundation.service.ICouponInfoService;
import com.shopping.foundation.service.IGoodsCartService;
import com.shopping.foundation.service.IGoodsService;
import com.shopping.foundation.service.IGoodsSpecPropertyService;
import com.shopping.foundation.service.IGroupGoodsService;
import com.shopping.foundation.service.IOrderFormLogService;
import com.shopping.foundation.service.IOrderFormService;
import com.shopping.foundation.service.IPaymentService;
import com.shopping.foundation.service.IPredepositLogService;
import com.shopping.foundation.service.IStoreCartService;
import com.shopping.foundation.service.IStoreService;
import com.shopping.foundation.service.ISysConfigService;
import com.shopping.foundation.service.ITemplateService;
import com.shopping.foundation.service.IUserConfigService;
import com.shopping.foundation.service.IUserService;
import com.shopping.manage.admin.tools.MsgTools;
import com.shopping.manage.admin.tools.PaymentTools;
import com.shopping.manage.seller.Tools.TransportTools;
import com.shopping.pay.alipay.config.AlipayConfig;
import com.shopping.pay.alipay.util.AlipaySubmit;
import com.shopping.pay.tools.PayTools;
import com.shopping.view.web.tools.GoodsViewTools;

@Controller
public class CartViewAction {

	@Autowired
	private ISysConfigService configService;

	@Autowired
	private IUserConfigService userConfigService;

	@Autowired
	private IGoodsService goodsService;

	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;

	@Autowired
	private IAddressService addressService;

	@Autowired
	private IAreaService areaService;

	@Autowired
	private IPaymentService paymentService;

	@Autowired
	private IOrderFormService orderFormService;

	@Autowired
	private IGoodsCartService goodsCartService;

	@Autowired
	private IStoreService storeService;

	@Autowired
	private IOrderFormLogService orderFormLogService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ITemplateService templateService;

	@Autowired
	private IPredepositLogService predepositLogService;

	@Autowired
	private IGroupGoodsService groupGoodsService;

	@Autowired
	private ICouponInfoService couponInfoService;

	@Autowired
	private IStoreCartService storeCartService;

	@Autowired
	private MsgTools msgTools;

	@Autowired
	private PaymentTools paymentTools;

	@Autowired
	private PayTools payTools;

	@Autowired
	private TransportTools transportTools;

	@Autowired
	private GoodsViewTools goodsViewTools;

	private static Logger logger = LoggerFactory.getLogger(CartViewAction.class);

	private List<StoreCart> cart_calc(HttpServletRequest request) {
		List<StoreCart> cart = new ArrayList<StoreCart>();
		List<StoreCart> user_cart = new ArrayList<StoreCart>();
		List<StoreCart> cookie_cart = new ArrayList<StoreCart>();
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		}
		String cart_session_id = "";
		Map params = new HashMap();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}
		if (user != null) {
			if (!cart_session_id.equals("")) {
				if (user.getStore() != null) {
					params.clear();
					params.put("cart_session_id", cart_session_id);
					params.put("user_id", user.getId());
					params.put("sc_status", Integer.valueOf(0));
					params.put("store_id", user.getStore().getId());
					List<StoreCart> store_cookie_cart = this.storeCartService.query(
							"select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
							params, -1, -1);
					for (StoreCart sc : store_cookie_cart) {
						// sc = (StoreCart)localIterator1.next();
						for (GoodsCart gc : ((StoreCart) sc).getGcs()) {
							gc.getGsps().clear();
							this.goodsCartService.delete(gc.getId());
						}
						this.storeCartService.delete(((StoreCart) sc).getId());
					}
				}

				params.clear();
				params.put("cart_session_id", cart_session_id);
				params.put("sc_status", Integer.valueOf(0));
				cookie_cart = this.storeCartService.query(
						"select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
						params, -1, -1);

				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", Integer.valueOf(0));
				user_cart = this.storeCartService.query(
						"select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params,
						-1, -1);
			} else {
				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", Integer.valueOf(0));
				user_cart = this.storeCartService.query(
						"select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params,
						-1, -1);
			}

		} else if (!cart_session_id.equals("")) {
			params.clear();
			params.put("cart_session_id", cart_session_id);
			params.put("sc_status", Integer.valueOf(0));
			cookie_cart = this.storeCartService.query(
					"select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
					params, -1, -1);
		}

		for (StoreCart sc : user_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					sc_add = false;
				}
			}
			if (sc_add) {
				cart.add(sc);
			}
		}
		for (StoreCart sc : cookie_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					sc_add = false;
					for (GoodsCart gc : sc.getGcs()) {
						gc.setSc(sc1);
						this.goodsCartService.update(gc);
					}
					this.storeCartService.delete(sc.getId());
				}
			}
			if (sc_add) {
				cart.add(sc);
			}
		}
		return (List<StoreCart>) cart;
	}

	@RequestMapping({ "/cart_menu_detail.htm" })
	public ModelAndView cart_menu_detail(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("cart_menu_detail.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<StoreCart> cart = cart_calc(request);
		List<GoodsCart> list = new ArrayList<GoodsCart>();
		if (cart != null) {
			for (StoreCart sc : cart) {
				if (sc != null)
					list.addAll(sc.getGcs());
			}
		}
		float total_price = 0.0F;
		for (GoodsCart gc : list) {
			Goods goods = this.goodsService.getObjById(gc.getGoods().getId());
			if (CommUtil.null2String(gc.getCart_type()).equals("combin"))
				total_price = CommUtil.null2Float(goods.getCombin_price());
			else {
				total_price = CommUtil.null2Float(Double.valueOf(CommUtil.mul(Integer.valueOf(gc.getCount()), gc.getPrice()))) + total_price;
			}
		}
		mv.addObject("total_price", Float.valueOf(total_price));
		mv.addObject("cart", list);
		return mv;
	}

	/**
	 * 添加购物车
	 * @param request
	 * @param response
	 * @param id
	 * @param count
	 * @param price
	 * @param gsp
	 * @param buy_type
	 */
	@RequestMapping({ "/add_goods_cart.htm" })
	public void add_goods_cart(HttpServletRequest request, HttpServletResponse response, String id, String count,
			String price, String gsp, String buy_type) {
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}

		if (cart_session_id.equals("")) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			cookie.setDomain(CommUtil.generic_domain(request));
			response.addCookie(cookie);
		}
		List<StoreCart> cart = new ArrayList<StoreCart>();
		List<StoreCart> user_cart = new ArrayList<StoreCart>();
		List<StoreCart> cookie_cart = new ArrayList<StoreCart>();
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		}
		Map params = new HashMap();
		StoreCart sc;
		if (user != null) {
			if (!cart_session_id.equals("")) {
				if (user.getStore() != null) {
					params.clear();
					params.put("cart_session_id", cart_session_id);
					params.put("user_id", user.getId());
					params.put("sc_status", Integer.valueOf(0));
					params.put("store_id", user.getStore().getId());
					List store_cookie_cart = this.storeCartService.query(
							"select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
							params, -1, -1);
					for (Iterator localIterator1 = store_cookie_cart.iterator(); localIterator1.hasNext();) {
						sc = (StoreCart) localIterator1.next();
						for (GoodsCart gc : sc.getGcs()) {
							gc.getGsps().clear();
							this.goodsCartService.delete(gc.getId());
						}
						this.storeCartService.delete(sc.getId());
					}
				}

				params.clear();
				params.put("cart_session_id", cart_session_id);
				params.put("sc_status", Integer.valueOf(0));
				cookie_cart = this.storeCartService.query(
						"select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
						params, -1, -1);

				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", Integer.valueOf(0));
				user_cart = this.storeCartService.query(
						"select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params,
						-1, -1);
			} else {
				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", Integer.valueOf(0));
				user_cart = this.storeCartService.query(
						"select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params,
						-1, -1);
			}

		} else if (!cart_session_id.equals("")) {
			params.clear();
			params.put("cart_session_id", cart_session_id);
			params.put("sc_status", Integer.valueOf(0));
			cookie_cart = this.storeCartService.query(
					"select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
					params, -1, -1);
		}

		for (StoreCart sc12 : user_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc12.getStore().getId())) {
					sc_add = false;
				}
			}
			if (sc_add) {
				cart.add(sc12);
			}
		}
		for (StoreCart sc11 : cookie_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc11.getStore().getId().equals(sc1.getStore().getId())) {
					sc_add = false;
					for (GoodsCart gc : sc1.getGcs()) {
						gc.setSc(sc1);
						this.goodsCartService.update(gc);
					}
					this.storeCartService.delete(sc1.getId());
				}
			}
			if (sc_add) {
				cart.add(sc11);
			}
		}

		String[] gsp_ids = gsp.split(",");
		Arrays.sort(gsp_ids);
		boolean add = true;
		double total_price = 0.0D;
		int total_count = 0;
		String[] gsp_ids1;
		for (StoreCart sc1 : cart)
			for (GoodsCart gc : sc1.getGcs())
				if ((gsp_ids != null) && (gsp_ids.length > 0) && (gc.getGsps() != null) && (gc.getGsps().size() > 0)) {
					gsp_ids1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = (gc.getGsps().get(i) != null ? ((GoodsSpecProperty) gc.getGsps().get(i)).getId().toString() : "");
					}
					Arrays.sort(gsp_ids1);
					if ((!gc.getGoods().getId().toString().equals(id)) || (!Arrays.equals(gsp_ids, gsp_ids1)))
						continue;
					add = false;
				} else if (gc.getGoods().getId().toString().equals(id)) {
					add = false;
				}

		Object obj;
		if (add) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			String type = "save";
			StoreCart sc33 = new StoreCart();
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(goods.getGoods_store().getId())) {
					sc33 = sc1;
					type = "update";
					break;
				}
			}
			sc33.setStore(goods.getGoods_store());
			if (((String) type).equals("save")) {
				sc33.setAddTime(new Date());
				this.storeCartService.save(sc33);
			} else {
				this.storeCartService.update(sc33);
			}

			obj = new GoodsCart();
			((GoodsCart) obj).setAddTime(new Date());
			if (CommUtil.null2String(buy_type).equals("")) {
				((GoodsCart) obj).setCount(CommUtil.null2Int(count));
				((GoodsCart) obj).setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
			}
			if (CommUtil.null2String(buy_type).equals("combin")) {
				((GoodsCart) obj).setCount(1);
				((GoodsCart) obj).setCart_type("combin");
				((GoodsCart) obj).setPrice(goods.getCombin_price());
			}
			((GoodsCart) obj).setGoods(goods);
			String spec_info = "";
			GoodsSpecProperty spec_property;
			for (String gsp_id : gsp_ids) {
				spec_property = this.goodsSpecPropertyService.getObjById(CommUtil.null2Long(gsp_id));
				((GoodsCart) obj).getGsps().add(spec_property);
				if (spec_property != null) {
					spec_info = spec_property.getSpec().getName() + ":" + spec_property.getValue() + " " + spec_info;
				}
			}
			((GoodsCart) obj).setSc(sc33);
			((GoodsCart) obj).setSpec_info(spec_info);
			this.goodsCartService.save((GoodsCart) obj);
			sc33.getGcs().add((GoodsCart) obj);
			
			double cart_total_price = 0.0D;

			for (GoodsCart gc1 : sc33.getGcs()) {
				// GoodsCart gc1 = (GoodsCart)((Iterator)???).next();
				if (CommUtil.null2String(gc1.getCart_type()).equals("")) {
					/*cart_total_price = cart_total_price + CommUtil.null2Double(gc1.getGoods().getGoods_current_price()) * gc1.getCount();*/
					cart_total_price = cart_total_price + CommUtil.null2Double(gc1.getPrice()) * gc1.getCount();
				}
				if (!CommUtil.null2String(gc1.getCart_type()).equals("combin"))
					continue;
				cart_total_price = cart_total_price
						+ CommUtil.null2Double(gc1.getGoods().getCombin_price()) * gc1.getCount();
			}

			sc33.setTotal_price(BigDecimal.valueOf(CommUtil.formatMoney(Double.valueOf(cart_total_price))));
			if (user == null)
				sc33.setCart_session_id(cart_session_id);
			else {
				sc33.setUser(user);
			}
			if (((String) type).equals("save")) {
				sc33.setAddTime(new Date());
				this.storeCartService.save(sc33);
			} else {
				this.storeCartService.update(sc33);
			}
			boolean cart_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc33.getStore().getId())) {
					cart_add = false;
				}
			}
			if (cart_add) {
				cart.add(sc33);
			}
		}
		for (Object type = cart.iterator(); ((Iterator) type).hasNext();) {
			StoreCart sc1 = (StoreCart) ((Iterator) type).next();

			total_count += sc1.getGcs().size();
			for (obj = sc1.getGcs().iterator(); ((Iterator) obj).hasNext();) {
				GoodsCart gc1 = (GoodsCart) ((Iterator) obj).next();

				total_price = total_price + CommUtil.mul(gc1.getPrice(), Integer.valueOf(gc1.getCount()));
			}
		}
		Map map = new HashMap();
		map.put("count", Integer.valueOf(total_count));
		map.put("total_price", Double.valueOf(total_price));
		String ret = Json.toJson(map, JsonFormat.compact());
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

	/**
	 * 从购物车删除商品
	 * @param request
	 * @param response
	 * @param id
	 * @param store_id
	 */
	@RequestMapping({ "/remove_goods_cart.htm" })
	public void remove_goods_cart(HttpServletRequest request, HttpServletResponse response, String id,
			String store_id) {
		GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(id));
		StoreCart the_sc = gc.getSc();
		gc.getGsps().clear();

		this.goodsCartService.delete(CommUtil.null2Long(id));
		if (the_sc.getGcs().size() == 0) {
			this.storeCartService.delete(the_sc.getId());
		}
		List<StoreCart> cart = cart_calc(request);
		double total_price = 0.0D;
		double sc_total_price = 0.0D;
		double count = 0.0D;
		for (StoreCart sc2 : cart) {
			for (GoodsCart gc1 : sc2.getGcs()) {
				total_price = CommUtil.null2Double(gc1.getPrice()) * gc1.getCount() + total_price;
				count += 1.0D;
				if ((store_id == null) || (store_id.equals(""))
						|| (!sc2.getStore().getId().toString().equals(store_id)))
					continue;
				sc_total_price = sc_total_price + CommUtil.null2Double(gc1.getPrice()) * gc1.getCount();
				sc2.setTotal_price(BigDecimal.valueOf(sc_total_price));
			}

			this.storeCartService.update(sc2);
		}
		request.getSession(false).setAttribute("cart", cart);
		Map map = new HashMap();
		map.put("count", Double.valueOf(count));
		map.put("total_price", Double.valueOf(total_price));
		map.put("sc_total_price", Double.valueOf(sc_total_price));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/goods_count_adjust.htm" })
	public void goods_count_adjust(HttpServletRequest request, HttpServletResponse response, String cart_id,
			String store_id, String count) {
		List<StoreCart> cart = cart_calc(request);

		double goods_total_price = 0.0D;
		String error = "100";
		Goods goods = null;
		String cart_type = "";
		GoodsCart gc;
		for (StoreCart sc : cart)
			for (Iterator localIterator2 = sc.getGcs().iterator(); localIterator2.hasNext();) {
				gc = (GoodsCart) localIterator2.next();
				if (gc.getId().toString().equals(cart_id)) {
					goods = gc.getGoods();
					cart_type = CommUtil.null2String(gc.getCart_type());
				}
			}
		Object sc;
		if (cart_type.equals("")) {
			if (goods.getGroup_buy() == 2) {
				GroupGoods gg = new GroupGoods();
				for (GroupGoods gg1 : goods.getGroup_goods_list()) {
					if (gg1.getGg_goods().equals(goods.getId())) {
						gg = gg1;
					}
				}
				if (gg.getGg_count() >= CommUtil.null2Int(count))
					for (StoreCart sc1 : cart) { // sc = (StoreCart)gc.next();
						for (int i = 0; i < ((StoreCart) sc1).getGcs().size(); i++) {
							GoodsCart art = (GoodsCart) ((StoreCart) sc1).getGcs().get(i);
							GoodsCart gc1 = art;
							if (art.getId().toString().equals(cart_id)) {
								((StoreCart) sc1).setTotal_price(
										BigDecimal.valueOf(CommUtil.add(((StoreCart) sc1).getTotal_price(),
												Double.valueOf((CommUtil.null2Int(count) - art.getCount())
														* CommUtil.null2Double(art.getPrice())))));
								art.setCount(CommUtil.null2Int(count));
								gc1 = art;
								((StoreCart) sc1).getGcs().remove(art);
								((StoreCart) sc1).getGcs().add(gc1);
								goods_total_price = CommUtil.null2Double(gc1.getPrice()) * gc1.getCount();
								this.storeCartService.update((StoreCart) sc1);
							}
						}
					}
				else {
					error = "300";
				}
			} else if (goods.getGoods_inventory() >= CommUtil.null2Int(count)) {
				for (StoreCart scart : cart) {
					for (int i = 0; i < scart.getGcs().size(); i++) {
						GoodsCart gcart = (GoodsCart) scart.getGcs().get(i);
						GoodsCart gc1 = gcart;
						if (gcart.getId().toString().equals(cart_id)) {
							scart.setTotal_price(BigDecimal.valueOf(CommUtil.add(scart.getTotal_price(),
									Double.valueOf((CommUtil.null2Int(count) - gcart.getCount())
											* Double.parseDouble(gcart.getPrice().toString())))));
							gcart.setCount(CommUtil.null2Int(count));
							gc1 = gcart;
							scart.getGcs().remove(gcart);
							scart.getGcs().add(gc1);
							goods_total_price = Double.parseDouble(gc1.getPrice().toString()) * gc1.getCount();
							this.storeCartService.update(scart);
						}
					}
				}
			} else {
				error = "200";
			}
		}

		if (cart_type.equals("combin")) {
			if (goods.getGoods_inventory() >= CommUtil.null2Int(count))
				for (StoreCart sscart : cart) {
					for (int i = 0; i < sscart.getGcs().size(); i++) {
						gc = (GoodsCart) sscart.getGcs().get(i);
						GoodsCart gc1 = (GoodsCart) gc;
						if (((GoodsCart) gc).getId().toString().equals(cart_id)) {
							sscart.setTotal_price(BigDecimal.valueOf(CommUtil.add(sscart.getTotal_price(),
									Float.valueOf((CommUtil.null2Int(count) - ((GoodsCart) gc).getCount())
											* CommUtil.null2Float(((GoodsCart) gc).getGoods().getCombin_price())))));
							((GoodsCart) gc).setCount(CommUtil.null2Int(count));
							gc1 = (GoodsCart) gc;
							sscart.getGcs().remove(gc);
							sscart.getGcs().add(gc1);
							goods_total_price = Double.parseDouble(gc1.getPrice().toString()) * gc1.getCount();
							this.storeCartService.update(sscart);
						}
					}
				}
			else {
				error = "200";
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		Object map = new HashMap();
		((Map) map).put("count", count);
		for (StoreCart ssscart : cart) {

			if (ssscart.getStore().getId().equals(CommUtil.null2Long(store_id))) {
				((Map) map).put("sc_total_price", Float.valueOf(CommUtil.null2Float(ssscart.getTotal_price())));
			}
		}
		((Map) map).put("goods_total_price", Double.valueOf(df.format(goods_total_price)));
		((Map) map).put("error", error);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();

			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查看购物车
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(display = false, rsequence = 0, title = "查看购物车", value = "/goods_cart1.htm*", rtype = "buyer", rname = "购物流程1", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/goods_cart1.htm" })
	public ModelAndView goods_cart1(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("goods_cart1.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			mv = new JModelAndView("wap/goods_cart1.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
		}
		List<StoreCart> cart = cart_calc(request);
		if (cart != null) {
			Store store = SecurityUserHolder.getCurrentUser().getStore() != null
					? SecurityUserHolder.getCurrentUser().getStore() : null;
			if (store != null) {
				for (StoreCart sc : cart) {
					if (sc.getStore().getId().equals(store.getId())) {
						for (GoodsCart gc : sc.getGcs()) {
							gc.getGsps().clear();
							this.goodsCartService.delete(gc.getId());
						}
						sc.getGcs().clear();
						this.storeCartService.delete(sc.getId());
					}
				}
			}
			request.getSession(false).setAttribute("cart", cart);
			mv.addObject("cart", cart);
			mv.addObject("goodsViewTools", this.goodsViewTools);
		} else {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
				mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
			}
			mv.addObject("op_title", "购物车信息为空");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}

		if (this.configService.getSysConfig().isZtc_status()) {
			List ztc_goods = null;
			Map ztc_map = new HashMap();
			ztc_map.put("ztc_status", Integer.valueOf(3));
			ztc_map.put("now_date", new Date());
			ztc_map.put("ztc_gold", Integer.valueOf(0));
			List goods = this.goodsService.query(
					"select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc", ztc_map, -1, -1);

			ztc_goods = randomZtcGoods(goods);
			mv.addObject("ztc_goods", ztc_goods);
		}
		return mv;
	}

	private List<Goods> randomZtcGoods(List<Goods> goods) {
		Random random = new Random();
		int random_num = 0;
		int num = 0;
		if (goods.size() - 8 > 0) {
			num = goods.size() - 8;
			random_num = random.nextInt(num);
		}
		Map ztc_map = new HashMap();
		ztc_map.put("ztc_status", Integer.valueOf(3));
		ztc_map.put("now_date", new Date());
		ztc_map.put("ztc_gold", Integer.valueOf(0));
		List ztc_goods = this.goodsService.query(
				"select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",
				ztc_map, random_num, 8);
		Collections.shuffle(ztc_goods);
		return ztc_goods;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "确认购物车填写地址", value = "/goods_cart2.htm*", rtype = "buyer", rname = "购物流程2", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/goods_cart2.htm" })
	public ModelAndView goods_cart2(HttpServletRequest request, HttpServletResponse response, String store_id) {
		ModelAndView mv = new JModelAndView("goods_cart2.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			mv = new JModelAndView("wap/goods_cart2.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
		}
		List<StoreCart> cart = cart_calc(request);
		StoreCart sc = null;
		if (cart != null) {
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(CommUtil.null2Long(store_id))) {
					sc = sc1;
					break;
				}
			}
		}
		if (sc != null) {
			Map params = new HashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			List addrs = this.addressService.query(
					"select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc", params, -1, -1);
			mv.addObject("addrs", addrs);
			if ((store_id == null) || (store_id.equals(""))) {
				store_id = sc.getStore().getId().toString();
			}
			String cart_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("cart_session", cart_session);
			params.clear();
			params.put("coupon_order_amount", sc.getTotal_price());
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("coupon_begin_time", new Date());
			params.put("coupon_end_time", new Date());
			params.put("status", Integer.valueOf(0));
			List couponinfos = this.couponInfoService.query(
					"select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount and obj.status=:status and obj.user.id=:user_id and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
					params, -1, -1);
			mv.addObject("couponinfos", couponinfos);
			mv.addObject("sc", sc);
			mv.addObject("cart_session", cart_session);
			mv.addObject("store_id", store_id);
			mv.addObject("transportTools", this.transportTools);
			mv.addObject("goodsViewTools", this.goodsViewTools);

			boolean goods_delivery = false;
			List<GoodsCart> goodCarts = sc.getGcs();
			for (GoodsCart gc : goodCarts) {
				if (gc.getGoods().getGoods_choice_type() == 0) {
					goods_delivery = true;
					break;
				}
			}
			mv.addObject("goods_delivery", Boolean.valueOf(goods_delivery));
		} else {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
				mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
			}
			mv.addObject("op_title", "购物车信息为空");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "完成订单提交进入支付", value = "/goods_cart3.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/goods_cart3.htm" })
	public ModelAndView goods_cart3(HttpServletRequest request, HttpServletResponse response, String cart_session,
			String store_id, String addr_id, String coupon_id) throws Exception {
		ModelAndView mv = new JModelAndView("goods_cart3.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			mv = new JModelAndView("wap/goods_cart3.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
		}
		String cart_session1 = (String) request.getSession(false).getAttribute("cart_session");
		List<StoreCart> cart = cart_calc(request);
		if (cart != null) {
			if (CommUtil.null2String(cart_session1).equals(cart_session)) {
				request.getSession(false).removeAttribute("cart_session");
				WebForm wf = new WebForm();
				OrderForm of = (OrderForm) wf.toPo(request, OrderForm.class);
				of.setAddTime(new Date());
				of.setOrder_id(SecurityUserHolder.getCurrentUser().getId()
						+ CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
				Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
				of.setAddr(addr);
				of.setOrder_status(10);
				of.setUser(SecurityUserHolder.getCurrentUser());
				of.setStore(this.storeService.getObjById(CommUtil.null2Long(store_id)));
				of.setTotalPrice(BigDecimal.valueOf(CommUtil.add(of.getGoods_amount(), of.getShip_price())));
				if (!CommUtil.null2String(coupon_id).equals("")) {
					CouponInfo ci = this.couponInfoService.getObjById(CommUtil.null2Long(coupon_id));
					ci.setStatus(1);
					this.couponInfoService.update(ci);
					of.setCi(ci);
					of.setTotalPrice(BigDecimal.valueOf(CommUtil.subtract(of.getTotalPrice(), ci.getCoupon().getCoupon_amount())));
				}
				of.setOrder_type("web");
				this.orderFormService.save(of);
				GoodsCart gc;
				for (StoreCart sc : cart) {
					if (sc.getStore().getId().toString().equals(store_id)) {
						for (Iterator localIterator2 = sc.getGcs().iterator(); localIterator2.hasNext();) {
							gc = (GoodsCart) localIterator2.next();
							gc.setOf(of);
							this.goodsCartService.update(gc);
						}
						sc.setCart_session_id(null);
						sc.setUser(SecurityUserHolder.getCurrentUser());
						sc.setSc_status(1);
						this.storeCartService.update(sc);
						break;
					}
				}
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					
					for (int i = 0; i < cookies.length; i++) {
						Cookie cookie = cookies[i];
						if (cookie.getName().equals("cart_session_id")) {
							cookie.setDomain(CommUtil.generic_domain(request));
							cookie.setValue("");
							cookie.setMaxAge(0);
							response.addCookie(cookie);
						}
					}
				}
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setOf(of);
				ofl.setLog_info("提交订单");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				this.orderFormLogService.save(ofl);
				mv.addObject("of", of);
				mv.addObject("paymentTools", this.paymentTools);
				if (this.configService.getSysConfig().isEmailEnable()) {
					send_email(request, of, of.getUser().getEmail(), "email_tobuyer_order_submit_ok_notify");
				}
				if (this.configService.getSysConfig().isSmsEnbale())
					send_sms(request, of, of.getUser().getMobile(), "sms_tobuyer_order_submit_ok_notify");
			} else {
				mv = new JModelAndView("error.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
					mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
				}
				mv.addObject("op_title", "订单已经失效");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			}
		} else {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
				mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
			}
			mv.addObject("op_title", "订单信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "订单支付详情", value = "/order_pay_view.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_pay_view.htm" })
	public ModelAndView order_pay_view(HttpServletRequest request, HttpServletResponse response, String id) {
		
		ModelAndView mv = new JModelAndView("order_pay.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			mv = new JModelAndView("wap/order_pay.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
		}
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if (of.getOrder_status() == 10) {
			mv.addObject("of", of);
			mv.addObject("paymentTools", this.paymentTools);
			mv.addObject("url", CommUtil.getURL(request));
		} else if (of.getOrder_status() < 10) {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "该订单已经取消！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		} else {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
				mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
			}
			mv.addObject("op_title", "该订单已经付款！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "订单支付", value = "/order_pay.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_pay.htm" })
	public ModelAndView order_pay(HttpServletRequest request, HttpServletResponse response, String payType, String order_id) {
		ModelAndView mv = null;
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		if (of.getOrder_status() == 10) {
			if (CommUtil.null2String(payType).equals("")) {
				mv = new JModelAndView("error.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
					mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
				}
				mv.addObject("op_title", "支付方式错误！");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			} else {
				List payments = new ArrayList();
				Map params = new HashMap();
				//判断是否平台支付
				if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
					params.put("mark", payType);
					params.put("type", "admin");
					payments = this.paymentService.query(
							"select obj from Payment obj where obj.mark=:mark and obj.type=:type", params, -1, -1);
				} else {
					params.put("mark", payType);
					params.put("store_id", of.getStore().getId());
					payments = this.paymentService.query(
							"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
				}
				of.setPayment((Payment) payments.get(0));
				this.orderFormService.update(of);
				if (payType.equals("balance")) {
					mv = new JModelAndView("balance_pay.html", this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
				} else if (payType.equals("outline")) {
					mv = new JModelAndView("outline_pay.html", this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					String pay_session = CommUtil.randomString(32);
					request.getSession(false).setAttribute("pay_session", pay_session);
					mv.addObject("paymentTools", this.paymentTools);
					mv.addObject("store_id",
							this.orderFormService.getObjById(CommUtil.null2Long(order_id)).getStore().getId());
					mv.addObject("pay_session", pay_session);
				} else if (payType.equals("payafter")) {
					mv = new JModelAndView("payafter_pay.html", this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					String pay_session = CommUtil.randomString(32);
					request.getSession(false).setAttribute("pay_session", pay_session);
					mv.addObject("paymentTools", this.paymentTools);
					mv.addObject("store_id",
							this.orderFormService.getObjById(CommUtil.null2Long(order_id)).getStore().getId());
					mv.addObject("pay_session", pay_session);
				} else {
					mv = new JModelAndView("line_pay.html", this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("payType", payType);
					mv.addObject("url", CommUtil.getURL(request));
					mv.addObject("payTools", this.payTools);
					mv.addObject("type", "goods");
					mv.addObject("payment_id", of.getPayment().getId());
				}
				mv.addObject("order_id", order_id);
			}
		} else {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
				mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
			}
			mv.addObject("op_title", "该订单不能进行付款！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}
	/**
	 * wap支付提交
	 */
	@SecurityMapping(display = false, rsequence = 0, title = "wap订单支付", value = "/wxwap_submit.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/pay_submit.htm" })
	public String paymentSubmit(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id) {

		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));

		if (of != null && of.getOrder_status() == 10) {

			List payments = new ArrayList();
			Map params = new HashMap();
			// 1为平台支付:
			if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
				params.put("mark", payType);
				params.put("type", "admin");
				payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark and obj.type=:type", params, -1, -1);
			} else {
				params.put("store_id", of.getStore().getId());
				params.put("mark", payType);
				payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
			}
			// 支付方式已经配置:wap支持支付宝wap支付以及微信公众号支付
			if (payments.size() > 0) {

				of.setPayment((Payment) payments.get(0));

				this.orderFormService.update(of);
				// 微信公众号支付
				if (payType.equals("weixin_wap")) {

					String APPID = of.getPayment().getWeixin_appId();
					String siteURL = CommUtil.getURL(request);
					String out_trade_no = of.getId().toString();

					return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid="
							+ APPID
							+ "&redirect_uri="
							+ siteURL
							+ "/wechat/oauthCode.htm?sn="
							+ out_trade_no
							+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";

				} else if (payType.equals("alipay_wap")) {

					// ////////////////////////////////////////////////////////////////////////////////
					String siteURL = CommUtil.getURL(request);
					AlipayConfig config = new AlipayConfig();

			        config.setSeller_email(of.getPayment().getSeller_email());
			        config.setKey(of.getPayment().getSafeKey());
			        config.setPartner(of.getPayment().getPartner());
			        config.setSign_type("RSA");
					// 把请求参数打包成数组
					Map<String, String> sParaTemp = new HashMap<String, String>();
					// 调用的接口名，无需修改
					sParaTemp.put("service", "alipay.wap.create.direct.pay.by.user");
					// 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
					sParaTemp.put("partner", of.getPayment().getPartner());
					// 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
					sParaTemp.put("seller_id", of.getPayment().getPartner());
					sParaTemp.put("_input_charset", config.getInput_charset());
					// 支付类型 ，无需修改
					sParaTemp.put("payment_type", "1");
					
					sParaTemp.put("notify_url", siteURL + "/alipay/alipay_notify.htm");
					sParaTemp.put("return_url", siteURL + "/alipay/alipay_retrun.htm");
					sParaTemp.put("out_trade_no", of.getId().toString());
					sParaTemp.put("subject", "订单号为" + of.getOrder_id());
					// 价格测试改为1分钱
					//sParaTemp.put("total_fee", "0.01");
					sParaTemp.put("total_fee", of.getTotalPrice().toPlainString());
					sParaTemp.put("show_url", "/index.htm");
					sParaTemp.put("body", "支付宝wap支付");
					// 其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.2Z6TSk&treeId=60&articleId=103693&docType=1
			        
					String sHtmlText = AlipaySubmit.buildRequestWap(config, sParaTemp, "get", "确定");
					
					try {
						response.setCharacterEncoding("UTF-8");
						response.setContentType("text/html");
						response.getWriter().print(sHtmlText);
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					// 支付方式错误
					return "redirect:" + CommUtil.getURL(request) + "/index.htm?payMethodError";
				}

			} else {
				// 支付方式未配置
				return "redirect:" + CommUtil.getURL(request) + "/index.htm?noPayMethod";
			}

		} else {
			// 该订单状态不正确，不能进行付款！
			return "redirect:"
					+ CommUtil.getURL(request)
					+ "/index.htm?orderError";
		}
		return null;
	}
	
	/**
     * 微信CODE回调JSP并进行微信授权接口认证获取用户openid
     */
    @RequestMapping({"/wechat/oauthCode.htm"})
    public ModelAndView oauthCode(HttpServletRequest request, HttpServletResponse response) {
        logger.info("支付收到微信code回调请求");
        ModelAndView mv = new JModelAndView("wap/wxpay.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
        // 用户同意授权后，能获取到code
        String code = request.getParameter("code");
        String sn = request.getParameter("sn");
        HttpSession session = request.getSession();
        String scode = (String)session.getAttribute("wxcode");
        
        if(code!=null&&code.equalsIgnoreCase(scode)) {
            
        } else {
            session.setAttribute("wxcode",code);
        }
        String openId = null;
        // 用户同意授权
        if (null != code && !"".equals(code) && !"authdeny".equals(code)) {
        	
        	OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(sn));
        	
            // 获取网页授权access_token
            WxOauth2Token wxOauth2Token = WxAdvancedUtil.getOauth2AccessToken(of.getPayment().getWeixin_appId(), of.getPayment().getWeixin_appSecret(), code);
            // 用户标识
            if(null!=wxOauth2Token){
                openId = wxOauth2Token.getOpenId();
            }
            logger.info("微信code回调请求:openId={}"+openId);
            logger.info("微信code回调请求:sn={}"+sn);
            
            String prodName = "网上购物";
            String amount = of.getTotalPrice().toString();
            
            mv.addObject("openId", openId);
            mv.addObject("sn", sn);
            mv.addObject("amount", amount);
            mv.addObject("siteName", this.configService.getSysConfig().getWebsiteName());
            mv.addObject("productName", prodName);
            
        } else {
        	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
    				this.userConfigService.getUserConfig(), 1, request, response);
    		mv.addObject("op_title", "用户未授权！");
    		mv.addObject("url", CommUtil.getURL(request) + "/index.htm?authdeny");
        }
        return mv;
    }
    
    /**
     * 生成微信订单数据以及微信支付需要的签名等信息，传输到前端，发起调用JSAPI支付
     * 作者: YUKE 日期：2016年1月14日 上午10:39:49
     *
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping({"/wechat/wxpay.htm"})
    public void wxpay(HttpServletRequest request, HttpServletResponse response, String openId,String sn,String productName,String totalPrice,String clientUrl) throws Exception {
        
        String APPID = null;
        String APP_SECRET = null;
        String MCH_ID = null;
        String API_KEY = null;
        String siteURL = CommUtil.getURL(request);
        String UNI_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

        logger.info("微信确认支付获取:openId="+openId);
        logger.info("微信确认支付获取:sn="+sn);

        OrderForm of = null;
        String amount = null;
        try {
        	of = this.orderFormService.getObjById(CommUtil.null2Long(sn));
        } catch (Exception e) {
            logger.error("微信确认支付查询paymentLog异常="+e.getMessage());
            e.printStackTrace();
        }
        if(of==null){
            amount = "";
            logger.info("微信确认支付查询orderForm=null");
        }
        else {
        	APPID = of.getPayment().getWeixin_appId();
            APP_SECRET = of.getPayment().getWeixin_appSecret();
            MCH_ID = of.getPayment().getWeixin_partnerId();
            API_KEY = of.getPayment().getWeixin_paySignKey();
        	
            /** 将元转换为分 */
            amount = of.getTotalPrice().multiply(new BigDecimal(100)).setScale(0).toString();
            logger.info("微信确认支付元转分成功amount={}",amount);
        }

        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("appid", APPID);
        parameters.put("mch_id", MCH_ID);
        parameters.put("nonce_str", WxCommonUtil.createNoncestr());
        parameters.put("body", productName);// 商品名称

        /** 订单号 */
        parameters.put("out_trade_no", sn);
        /** 订单金额以分为单位，只能为整数 */
        //parameters.put("total_fee", "1");//测试用的金额1分钱
        parameters.put("total_fee", amount);
        /** 客户端本地ip */
        parameters.put("spbill_create_ip", request.getRemoteAddr());
        /** 支付回调地址 */
        parameters.put("notify_url", siteURL + "/wechat/paynotify.htm");
        /** 支付方式为JSAPI支付 */
        parameters.put("trade_type", "JSAPI");
        /** 用户微信的openid，当trade_type为JSAPI的时候，该属性字段必须设置 */
        parameters.put("openid", openId);

        /** 使用MD5进行签名，编码必须为UTF-8 */
        String sign = WxCommonUtil.createSignMD5("UTF-8", parameters, API_KEY);

        /**将签名结果加入到map中，用于生成xml格式的字符串*/
        parameters.put("sign", sign);

        /** 生成xml结构的数据，用于统一下单请求的xml请求数据 */
        String requestXML = WxCommonUtil.getRequestXml(parameters);
        logger.info("请求统一支付requestXML：" + requestXML);

        try {
            /** 1、使用POST请求统一下单接口，获取预支付单号prepay_id */
            String result = WxCommonUtil.httpsRequestString(UNI_URL, "POST", requestXML);
            logger.info("请求统一支付result:" + result);
            //解析微信返回的信息，以Map形式存储便于取值
            Map<String, String> map = WxCommonUtil.doXMLParse(result);
            logger.info("预支付单号prepay_id为:" + map.get("prepay_id"));
            //全局map，该map存放前端ajax请求的返回值信息，包括wx.config中的配置参数值，也包括wx.chooseWXPay中的配置参数值
            SortedMap<Object, Object> params = new TreeMap<Object, Object>();
            params.put("appId", APPID);
            params.put("timeStamp", new Date().getTime()+""); //时间戳
            params.put("nonceStr", WxCommonUtil.createNoncestr()); //随机字符串
            params.put("package", "prepay_id=" + map.get("prepay_id")); //格式必须为 prepay_id=***
            params.put("signType", "MD5"); //签名的方式必须是MD5
            /**
             * 获取预支付prepay_id后，需要再次签名，此次签名是用于前端js中的wx.chooseWXPay中的paySign。
             * 参与签名的参数有5个，分别是：appId、timeStamp、nonceStr、package、signType 注意参数名称的大小写
             */
            String paySign = WxCommonUtil.createSignMD5("UTF-8", params, API_KEY);
            //预支付单号
            params.put("packageValue", "prepay_id=" + map.get("prepay_id"));
            params.put("paySign", paySign); //支付签名
            //付款成功后同步请求的URL，请求我们自定义的支付成功的页面，展示给用户
            params.put("sendUrl", siteURL+"/wechat/paysuccess.htm?totalPrice="+totalPrice);

            //获取用户的微信客户端版本号，用于前端支付之前进行版本判断，微信版本低于5.0无法使用微信支付
            String userAgent = request.getHeader("user-agent");
            char agent = userAgent.charAt(userAgent.indexOf("MicroMessenger") + 15);
            params.put("agent", new String(new char[] { agent }));

            /**2、获取access_token作为参数传递,由于access_token有有效期限制，和调用次数限制，可以缓存到session或者数据库中.有效期设置为小于7200秒*/
            WxToken wxtoken = WxCommonUtil.getToken(APPID, APP_SECRET);
            String token = wxtoken.getAccessToken();
            logger.info("获取的token值为:" + token);

            /**3、获取凭证ticket发起GET请求 */
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=" + token;
            logger.info("接口调用凭证ticket的requestUrl：" + requestUrl);

            String ticktresult = WxCommonUtil.httpsRequestString(requestUrl, "GET", null);
            JSONObject jsonresult = JSONObject.fromObject(ticktresult);
            //JSONObject jsonObject = WxCommonUtil.httpsRequest(requestUrl, "GET", null);
            //使用JSSDK支付，需要另一个凭证，也就是jsapi_ticket。这个是JSSDK中使用到的。
            String jsapi_ticket = jsonresult.getString("ticket");
            logger.info("jsapi_ticket：" + jsapi_ticket);
            // 获取到ticket凭证之后，需要进行一次签名
            String config_nonceStr = WxCommonUtil.createNoncestr();// 获取随机字符串
            long config_timestamp = new Date().getTime();// 时间戳
            // 加入签名的参数有4个，分别是： noncestr、jsapi_ticket、timestamp、url，注意字母全部为小写
            SortedMap<Object, Object> configMap = new TreeMap<Object, Object>();
            configMap.put("noncestr", config_nonceStr);
            configMap.put("jsapi_ticket", jsapi_ticket);
            configMap.put("timestamp", config_timestamp+"");
            configMap.put("url", clientUrl);
            //该签名是用于前端js中wx.config配置中的signature值。
            String config_sign = WxCommonUtil.createSignSHA1("UTF-8", configMap);
            // 将config_nonceStr、jsapi_ticket 、config_timestamp、config_sign一同传递到前端
            // 这几个参数名称和上面获取预支付prepay_id使用的参数名称是不一样的，不要混淆了。
            // 这几个参数是提供给前端js代码在调用wx.config中进行配置的参数，wx.config里面的signature值就是这个config_sign的值，以此类推
            params.put("config_nonceStr", config_nonceStr);
            params.put("config_timestamp", config_timestamp+"");
            params.put("config_sign", config_sign);
            // 将map转换为json字符串，传递给前端ajax回调
            String json = JSONArray.fromObject(params).toString();
            logger.info("用于wx.config配置的json：" + json);
            
            response.setContentType("text/plain");
    		response.setHeader("Cache-Control", "no-cache");
    		response.setCharacterEncoding("UTF-8");
    		
    		try {
    			PrintWriter writer = response.getWriter();
    			writer.print(json);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 微信扫码支付
     * @param request
     * @param response
     * @param order_id
     * @return
     */
    @RequestMapping({"/wechat/wxcodepay.htm"})
    public void wxcodepay(HttpServletRequest request, HttpServletResponse response, String order_id){
    	
    	String UNI_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    	OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		String returnhtml = null;
		if (of.getOrder_status() == 10) {
			
			List payments = new ArrayList();
			Map params = new HashMap();
			//判断是否平台支付
			if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
				params.put("mark", "wxcodepay");
				params.put("type", "admin");
				payments = this.paymentService.query(
						"select obj from Payment obj where obj.mark=:mark and obj.type=:type", params, -1, -1);
			} else {
				params.put("mark", "wxcodepay");
				params.put("store_id", of.getStore().getId());
				payments = this.paymentService.query(
						"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
			}
			Payment payment = (Payment) payments.get(0);
			of.setPayment(payment);
			this.orderFormService.update(of);

			String codeUrl = "";//微信返回的二维码地址信息

			SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
			parameters.put("appid", payment.getWeixin_appId());// 公众账号id
			parameters.put("mch_id", payment.getWeixin_partnerId());// 商户号
			parameters.put("nonce_str", WxCommonUtil.createNoncestr());// 随机字符串
			parameters.put("body", "在线购物");// 商品描述
			parameters.put("out_trade_no", order_id);// 商户订单号
			parameters.put("total_fee", of.getTotalPrice().multiply(new BigDecimal(100)).setScale(0).toString());// 总金额
			//parameters.put("total_fee", "1");
			parameters.put("spbill_create_ip", WxCommonUtil.localIp());// 终端IP.Native支付填调用微信支付API的机器IP。
			// 支付成功后回调的action，与JSAPI相同
			//parameters.put("notify_url", basePath + NOTIFY_URL);// 支付成功后回调的action
			parameters.put("notify_url", CommUtil.getURL(request)+"/wechat/paynotify.htm");//支付成功后回调的action，与JSAPI相同
			parameters.put("trade_type", "NATIVE");// 交易类型
			parameters.put("product_id", order_id);// 商品ID。商品号要唯一,trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义
			//String sign = WxPayUtil.createSign2("UTF-8", parameters, API_KEY);
			String sign = WxCommonUtil.createSignMD5("UTF-8", parameters, payment.getWeixin_paySignKey());
			parameters.put("sign", sign);// 签名
			String requestXML = WxCommonUtil.getRequestXml(parameters);
			logger.info("requestXML"+requestXML);
			String result = WxCommonUtil.httpsRequestString(UNI_URL, "POST", requestXML);//WxCommonUtil.httpsRequest(WxConstants.UNIFIED_ORDER_URL, "POST", requestXML);
			// System.out.println(" 微信支付二维码生成" + result);
			Map<String, String> map = new HashMap<String, String>();
			try {
				map = WxCommonUtil.doXMLParse(result);
				logger.info("------------------code_url="+map.get("code_url")+";      result_code="+map.get("code_url")+"------------------------------");
			} catch (Exception e) {
				logger.error("doXMLParse()--error",e);
			}
			String returnCode = map.get("return_code");
			String resultCode = map.get("result_code");
			
			if (returnCode.equalsIgnoreCase("SUCCESS")
					&& resultCode.equalsIgnoreCase("SUCCESS")) {
				codeUrl = map.get("code_url");
				// 拿到codeUrl，生成二维码图片
				byte[] imgs = QRCodeEncoderHandler.createQRCode(codeUrl);

				String urls = "/"+this.configService.getSysConfig().getUploadFilePath()
						+ java.io.File.separator + "weixin_qr" + java.io.File.separator + "wxpay"
						+ java.io.File.separator;
				// 图片的实际路径
				String imgfile = urls + order_id + ".png";
				
				QRCodeEncoderHandler.saveImage(imgs, imgfile, "png");
				
				// 图片的网路路径
				String imgUrl = CommUtil.getURL(request) + "/"
						+ this.configService.getSysConfig().getUploadFilePath() 
						+ "/weixin_qr/wxpay/" + order_id + ".png";

				logger.info("图片的网路路径imgurl={}",imgUrl);
				
				returnhtml = "<img src='"+imgUrl+"' style='width:200px;height:200px;'/>";
				
			} else {
				returnhtml = "支付状态不正确";
			}
		}
		response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
			PrintWriter writer = response.getWriter();
			writer.print(returnhtml);
        } catch (IOException e) {
			e.printStackTrace();
        }
    	//return returnhtml;
    }
    
	@SecurityMapping(display = false, rsequence = 0, title = "订单线下支付", value = "/order_pay_outline.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_pay_outline.htm" })
	public ModelAndView order_pay_outline(HttpServletRequest request, HttpServletResponse response, String payType,
			String order_id, String pay_msg, String pay_session) throws Exception {
		ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false).getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
			of.setPay_msg(pay_msg);
			Map params = new HashMap();
			params.put("mark", "outline");
			params.put("store_id", of.getStore().getId());
			List payments = this.paymentService.query(
					"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
			if (payments.size() > 0) {
				of.setPayment((Payment) payments.get(0));
				of.setPayTime(new Date());
			}
			of.setOrder_status(15);
			this.orderFormService.update(of);
			if (this.configService.getSysConfig().isSmsEnbale()) {
				send_sms(request, of, of.getStore().getUser().getMobile(), "sms_toseller_outline_pay_ok_notify");
			}
			if (this.configService.getSysConfig().isEmailEnable()) {
				send_email(request, of, of.getStore().getUser().getEmail(), "email_toseller_outline_pay_ok_notify");
			}

			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交线下支付申请");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(of);
			this.orderFormLogService.save(ofl);
			request.getSession(false).removeAttribute("pay_session");
			mv.addObject("op_title", "线下支付提交成功，等待卖家审核！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		} else {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "订单货到付款", value = "/order_pay_payafter.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_pay_payafter.htm" })
	public ModelAndView order_pay_payafter(HttpServletRequest request, HttpServletResponse response, String payType,
			String order_id, String pay_msg, String pay_session) throws Exception {
		ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false).getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
			of.setPay_msg(pay_msg);
			Map params = new HashMap();
			params.put("mark", "payafter");
			params.put("store_id", of.getStore().getId());
			List payments = this.paymentService.query(
					"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
			if (payments.size() > 0) {
				of.setPayment((Payment) payments.get(0));
				of.setPayTime(new Date());
			}
			of.setOrder_status(16);
			this.orderFormService.update(of);
			if (this.configService.getSysConfig().isSmsEnbale()) {
				send_sms(request, of, of.getStore().getUser().getMobile(), "sms_toseller_payafter_pay_ok_notify");
			}
			if (this.configService.getSysConfig().isEmailEnable()) {
				send_email(request, of, of.getStore().getUser().getEmail(), "email_toseller_payafter_pay_ok_notify");
			}

			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交货到付款申请");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(of);
			this.orderFormLogService.save(ofl);
			request.getSession(false).removeAttribute("pay_session");
			mv.addObject("op_title", "货到付款提交成功，等待卖家发货！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		} else {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "订单预付款支付", value = "/order_pay_balance.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_pay_balance.htm" })
	public ModelAndView order_pay_balance(HttpServletRequest request, HttpServletResponse response, String payType,
			String order_id, String pay_msg) throws Exception {
		ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());

		if (CommUtil.null2Double(user.getAvailableBalance()) > CommUtil.null2Double(of.getTotalPrice())) {
			of.setPay_msg(pay_msg);
			of.setOrder_status(20);
			Map params = new HashMap();
			params.put("mark", "balance");
			params.put("store_id", of.getStore().getId());
			List payments = this.paymentService.query(
					"select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
			if (payments.size() > 0) {
				of.setPayment((Payment) payments.get(0));
				of.setPayTime(new Date());
			}
			boolean ret = this.orderFormService.update(of);
			if (this.configService.getSysConfig().isEmailEnable()) {
				send_email(request, of, of.getStore().getUser().getEmail(), "email_toseller_balance_pay_ok_notify");
				send_email(request, of, of.getStore().getUser().getEmail(), "email_tobuyer_balance_pay_ok_notify");
			}
			if (this.configService.getSysConfig().isSmsEnbale()) {
				send_sms(request, of, of.getStore().getUser().getMobile(), "sms_toseller_balance_pay_ok_notify");
				send_sms(request, of, of.getUser().getMobile(), "sms_tobuyer_balance_pay_ok_notify");
			}
			if (ret) {
				user.setAvailableBalance(
						BigDecimal.valueOf(CommUtil.subtract(user.getAvailableBalance(), of.getTotalPrice())));
				user.setFreezeBlance(BigDecimal.valueOf(CommUtil.add(user.getFreezeBlance(), of.getTotalPrice())));
				this.userService.update(user);
				PredepositLog log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_user(user);
				log.setPd_op_type("消费");
				log.setPd_log_amount(BigDecimal.valueOf(-CommUtil.null2Double(of.getTotalPrice())));
				log.setPd_log_info("订单" + of.getOrder_id() + "购物减少可用预存款");
				log.setPd_type("可用预存款");
				this.predepositLogService.save(log);

				for (GoodsCart gc : of.getGcs()) {
					Goods goods = gc.getGoods();
					if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
						for (GroupGoods gg : goods.getGroup_goods_list()) {
							if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
								gg.setGg_count(gg.getGg_count() - gc.getCount());
								gg.setGg_def_count(gg.getGg_def_count() + gc.getCount());
								this.groupGoodsService.update(gg);
							}
						}
					}
					List gsps = new ArrayList();
					for (GoodsSpecProperty gsp : gc.getGsps()) {
						gsps.add(gsp.getId().toString());
					}
					String[] gsp_list = new String[gsps.size()];
					gsps.toArray(gsp_list);
					goods.setGoods_salenum(goods.getGoods_salenum() + gc.getCount());
					Map temp;
					if (goods.getInventory_type().equals("all")) {
						goods.setGoods_inventory(goods.getGoods_inventory() - gc.getCount());
					} else {
						List list = (List) Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
						for (Iterator localIterator4 = list.iterator(); localIterator4.hasNext();) {
							temp = (Map) localIterator4.next();
							String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
							Arrays.sort(temp_ids);
							Arrays.sort(gsp_list);
							if (Arrays.equals(temp_ids, gsp_list)) {
								temp.put("count",
										Integer.valueOf(CommUtil.null2Int(temp.get("count")) - gc.getCount()));
							}
						}
						goods.setGoods_inventory_detail(Json.toJson(list, JsonFormat.compact()));
					}
					for (GroupGoods gg : goods.getGroup_goods_list()) {
						if ((!gg.getGroup().getId().equals(goods.getGroup().getId())) || (gg.getGg_count() != 0))
							continue;
						goods.setGroup_buy(3);
					}

					this.goodsService.update(goods);
				}

			}

			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("预付款支付");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(of);
			this.orderFormLogService.save(ofl);
			mv.addObject("op_title", "预付款支付成功！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		} else {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "可用余额不足，支付失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "订单支付结果", value = "/order_finish.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_finish.htm" })
	public ModelAndView order_finish(HttpServletRequest request, HttpServletResponse response, String order_id) {
		ModelAndView mv = new JModelAndView("order_finish.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			mv = new JModelAndView("wap/order_finish.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
		}
		OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "地址新增", value = "/cart_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/cart_address.htm" })
	public ModelAndView cart_address(HttpServletRequest request, HttpServletResponse response, String id, String store_id) {
		
		ModelAndView mv = new JModelAndView("cart_address.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			mv = new JModelAndView("wap/cart_address.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
		}
		List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
		mv.addObject("areas", areas);
		mv.addObject("store_id", store_id);
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "购物车中收货地址保存", value = "/cart_address_save.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/cart_address_save.htm" })
	public String cart_address_save(HttpServletRequest request, HttpServletResponse response, String id, String area_id, String store_id) {
		WebForm wf = new WebForm();
		Address address = null;
		if (id.equals("")) {
			address = (Address) wf.toPo(request, Address.class);
			address.setAddTime(new Date());
		} else {
			Address obj = this.addressService.getObjById(Long.valueOf(Long.parseLong(id)));
			address = (Address) wf.toPo(request, obj);
		}
		address.setUser(SecurityUserHolder.getCurrentUser());
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		address.setArea(area);
		if (id.equals(""))
			this.addressService.save(address);
		else
			this.addressService.update(address);
		return "redirect:goods_cart2.htm?store_id=" + store_id;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "地址切换", value = "/order_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_address.htm" })
	public void order_address(HttpServletRequest request, HttpServletResponse response, String addr_id,
			String store_id) {
		List<StoreCart> cart = (List) request.getSession(false).getAttribute("cart");
		StoreCart sc = null;
		if (cart != null) {
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(CommUtil.null2Long(store_id))) {
					sc = sc1;
					break;
				}
			}
		}
		Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
		List sms = this.transportTools.query_cart_trans(sc, CommUtil.null2String(addr.getArea().getId()));

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(Json.toJson(sms, JsonFormat.compact()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	   @SecurityMapping(display = false, rsequence = 0, title="收货地址列表", value="/address.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
	   @RequestMapping({"/address.htm"})
	   public ModelAndView address(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String store_id)
	   {
	     ModelAndView mv = new JModelAndView("address.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
	     String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			 mv = new JModelAndView("wap/address.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
		 }
	     String url = this.configService.getSysConfig().getAddress();
	     if ((url == null) || (url.equals(""))) {
	       url = CommUtil.getURL(request);
	     }
	     String params = "";
	     AddressQueryObject qo = new AddressQueryObject(currentPage, mv, orderBy, orderType);
	     qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
	     IPageList pList = this.addressService.list(qo);
	     CommUtil.saveIPageList2ModelAndView(url + "/address.htm", "", params, pList, mv);
	     List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
	     mv.addObject("areas", areas);
	     mv.addObject("store_id", store_id);
	     return mv;
	   }
	   
	   @SecurityMapping(display = false, rsequence = 0, title="修改收货地址", value="/address_edit.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
	   @RequestMapping({"/address_edit.htm"})
	   public ModelAndView address_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String store_id) {
	     
		 ModelAndView mv = new JModelAndView("cart_address.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
		 String shopping_view_type = CommUtil.null2String( request.getSession().getAttribute( "shopping_view_type" ) );
		 if( (shopping_view_type != null) && (!shopping_view_type.equals( "" )) && (shopping_view_type.equals( "wap" )) ) {
			 mv = new JModelAndView("wap/cart_address.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
		 }
	     List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
	     Address obj = this.addressService.getObjById(CommUtil.null2Long(id));
	     mv.addObject("obj", obj);
	     mv.addObject("areas", areas);
	     mv.addObject("store_id", store_id);
	     mv.addObject("currentPage", currentPage);
	     return mv;
	   }
	 
	   @SecurityMapping(display = false, rsequence = 0, title="收货地址删除", value="/address_del.htm*", rtype="buyer", rname="用户中心", rcode="user_center", rgroup="用户中心")
	   @RequestMapping({"/address_del.htm"})
	   public String address_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage, String store_id) {
	     String[] ids = mulitId.split(",");
	     for (String id : ids) {
	       if (!id.equals("")) {
	         Address address = this.addressService.getObjById(Long.valueOf(Long.parseLong(id)));
	         this.addressService.delete(Long.valueOf(Long.parseLong(id)));
	       }
	     }
	     return "redirect:goods_cart2.htm?store_id=" + store_id;
	   }
	   

	private void send_email(HttpServletRequest request, OrderForm order, String email, String mark) throws Exception {
		com.shopping.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
		if ((template != null) && (template.isOpen())) {
			String subject = template.getTitle();
			String path = request.getSession().getServletContext().getRealPath("") + File.separator + "vm"
					+ File.separator;
			if (!CommUtil.fileExist(path)) {
				CommUtil.createFolder(path);
			}
			PrintWriter pwrite = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();

			Properties p = new Properties();
			p.setProperty("file.resource.loader.path", request.getRealPath("/") + "vm" + File.separator);
			p.setProperty("input.encoding", "UTF-8");
			p.setProperty("output.encoding", "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
			VelocityContext context = new VelocityContext();
			context.put("buyer", order.getUser());
			context.put("seller", order.getStore().getUser());
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", CommUtil.getURL(request));
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);

			String content = writer.toString();
			this.msgTools.sendEmail(email, subject, content);
		}
	}

	private void send_sms(HttpServletRequest request, OrderForm order, String mobile, String mark) throws Exception {
		com.shopping.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
		if ((template != null) && (template.isOpen())) {
			String path = request.getSession().getServletContext().getRealPath("") + File.separator + "vm"
					+ File.separator;
			if (!CommUtil.fileExist(path)) {
				CommUtil.createFolder(path);
			}
			PrintWriter pwrite = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();

			Properties p = new Properties();
			p.setProperty("file.resource.loader.path", request.getRealPath("/") + "vm" + File.separator);
			p.setProperty("input.encoding", "UTF-8");
			p.setProperty("output.encoding", "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
			VelocityContext context = new VelocityContext();
			context.put("buyer", order.getUser());
			context.put("seller", order.getStore().getUser());
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", CommUtil.getURL(request));
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);

			String content = writer.toString();
			this.msgTools.sendSMS(mobile, content);
		}
	}
}
