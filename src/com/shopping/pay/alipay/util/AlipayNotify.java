 package com.shopping.pay.alipay.util;
 
 import java.io.BufferedReader;
 import java.io.InputStreamReader;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.util.Map;
 import org.dom4j.Document;
 import org.dom4j.DocumentHelper;
 import org.dom4j.Node;

 import com.shopping.pay.alipay.config.AlipayConfig;
 
 public class AlipayNotify
 {
   //private static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";
   private static final String HTTP_VERIFY_URL = "http://notify.alipay.com/trade/notify_query.do?";
   /**
	 * 支付宝消息验证地址
	 */
	private static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";

	/**
	 * 验证消息是否是支付宝发出的合法消息
	 * 
	 * @param params
	 *            通知返回来的参数数组
	 * @return 验证结果
	 */
	public static boolean verifyWap(AlipayConfig config, Map<String, String> params) {
		
		params.put("subject", "测试");
		// 判断responsetTxt是否为true，isSign是否为true
		// responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
		// isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
		String responseTxt = "false";
		if (params.get("notify_id") != null) {
			String notify_id = params.get("notify_id");
			responseTxt = verifyResponse(config, notify_id);
		}
		String sign = "";
		if (params.get("sign") != null) {
			sign = params.get("sign");
		}
		boolean isSign = getSignVeryfyWap(config, params, sign);

		// 写日志记录（若要调试，请取消下面两行注释）
		// String sWord = "responseTxt=" + responseTxt + "\n isSign=" + isSign +
		// "\n 返回回来的参数：" + AlipayCore.createLinkString(params);
		// AlipayCore.logResult(sWord);

		if (isSign && responseTxt.equals("true")) {
			return true;
		} else {
			return false;
		}
	}

   public static boolean verify(AlipayConfig config, Map<String, String> params)
   {
     String responseTxt = "true";
     if (params.get("notify_id") != null) {
       String notify_id = (String)params.get("notify_id");
       responseTxt = verifyResponse(config, notify_id);
     }
     String sign = "";
     if (params.get("sign") != null) {
       sign = (String)params.get("sign");
     }
     boolean isSign = getSignVeryfy(config, params, sign, true);
 
     String sWord = "responseTxt=" + responseTxt + "\n isSign=" + isSign + 
       "\n 返回回来的参数：" + AlipayCore.createLinkString(params);
 
     return (isSign) && (responseTxt.equals("true"));
   }
 
   public static Map<String, String> decrypt(AlipayConfig config, Map<String, String> inputPara) throws Exception {
	   inputPara.put("notify_data", RSA.decrypt((String)inputPara.get("notify_data"), 
       AlipayConfig.private_key, config.getInput_charset()));
	   return inputPara;
   }
 
   public static boolean verifyNotifyWap(AlipayConfig config, Map<String, String> params) throws Exception {
	   
     if (config.getSign_type().equals("0001")) {
       params = decrypt(config, params);
     }
 
     String responseTxt = "true";
     try {
       Document document = DocumentHelper.parseText((String)params.get("notify_data"));
       String notify_id = document.selectSingleNode("//notify/notify_id").getText();
       responseTxt = verifyResponse(config, notify_id);
     } catch (Exception e) {
       responseTxt = e.toString();
     }
 
     String sign = "";
     if (params.get("sign") != null) {
       sign = (String)params.get("sign");
     }
     boolean isSign = getSignVeryfy(config, params, sign, false);
 
     return (isSign) && (responseTxt.equals("true"));
   }
 
   private static boolean getSignVeryfy(AlipayConfig config, Map<String, String> Params, String sign, boolean isSort)
   {
     Map sParaNew = AlipayCore.paraFilter(Params);
 
     String preSignStr = "";
     if (isSort)
       preSignStr = AlipayCore.createLinkString(sParaNew);
     else {
       preSignStr = AlipayCore.createLinkStringNoSort(sParaNew);
     }
 
     boolean isSign = false;
     if (config.getSign_type().equals("MD5")) {
       isSign = MD5.verify(preSignStr, sign, config.getKey(), 
         config.getInput_charset());
     }
     if (config.getSign_type().equals("0001")) {
       isSign = RSA.verify(preSignStr, sign, AlipayConfig.getAli_public_key(), 
         config.getInput_charset());
     }
     return isSign;
   }

	/**
	 * 根据反馈回来的信息，生成签名结果
	 * 
	 * @param Params
	 *            通知返回来的参数数组
	 * @param sign
	 *            比对的签名结果
	 * @return 生成的签名结果
	 */
	private static boolean getSignVeryfyWap(AlipayConfig config, Map<String, String> Params, String sign) {
		// 过滤空值、sign与sign_type参数
		Map<String, String> sParaNew = AlipayCore.paraFilter(Params);
		// 获取待签名字符串
		String preSignStr = AlipayCore.createLinkString(sParaNew);
		// 获得签名验证结果
		boolean isSign = false;
		if (config.getSign_type().equals("RSA")) {
			isSign = RSA.verifyWap(preSignStr, sign, AlipayConfig.getAli_public_key(), config.getInput_charset());
		}
		return isSign;
	}

	/**
	 * 获取远程服务器ATN结果,验证返回URL
	 * 
	 * @param notify_id
	 *            通知校验ID
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
   private static String verifyResponse(AlipayConfig config, String notify_id) {
	   
     String veryfy_url = "";
     if (config.getTransport().equalsIgnoreCase("https"))
       veryfy_url = "https://mapi.alipay.com/gateway.do?service=notify_verify&";
     else {
       veryfy_url = "http://notify.alipay.com/trade/notify_query.do?";
     }
     String partner = config.getPartner();
     veryfy_url = veryfy_url + "partner=" + partner + "&notify_id=" + notify_id;
 
     return checkUrl(veryfy_url);
   }
 

	/*
	private static String verifyResponse(String notify_id) {
		// 获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求

		String partner = AlipayConfig.partner;
		String veryfy_url = HTTPS_VERIFY_URL + "partner=" + partner
				+ "&notify_id=" + notify_id;

		return checkUrl(veryfy_url);
	}*/

	/**
	 * 获取远程服务器ATN结果
	 * 
	 * @param urlvalue
	 *            指定URL路径地址
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
	 *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	private static String checkUrl(String urlvalue) {
		String inputLine = "";

		try {
			URL url = new URL(urlvalue);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			inputLine = in.readLine().toString();
		} catch (Exception e) {
			e.printStackTrace();
			inputLine = "";
		}

		return inputLine;
	}
	

	   /*private static String checkUrl(String urlvalue)
	   {
	     String inputLine = "";
	     try
	     {
	       URL url = new URL(urlvalue);
	       HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
	       BufferedReader in = new BufferedReader(
	         new InputStreamReader(urlConnection.getInputStream()));
	       inputLine = in.readLine().toString();
	     } catch (Exception e) {
	       e.printStackTrace();
	       inputLine = "";
	     }
	 
	     return inputLine;
	   }*/
	   
	
 }


 
 
 