 package com.shopping.pay.alipay.util;
 
 import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
 import org.apache.commons.httpclient.HttpException;
 import org.apache.commons.httpclient.NameValuePair;
 import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
 import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.shopping.pay.alipay.config.AlipayConfig;
import com.shopping.pay.alipay.util.httpClient.HttpProtocolHandler;
import com.shopping.pay.alipay.util.httpClient.HttpRequest;
import com.shopping.pay.alipay.util.httpClient.HttpResponse;
import com.shopping.pay.alipay.util.httpClient.HttpResultType;
 
 public class AlipaySubmit
 {
   private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
   private static final String WAP_ALIPAY_GATEWAY_NEW = "http://wappaygw.alipay.com/service/rest.htm?";
 
   public static String buildForm(AlipayConfig config, Map<String, String> sParaTemp, String gateway, String strMethod, String strButtonName)
   {
     Map sPara = buildRequestPara(config, sParaTemp);
     List keys = new ArrayList(sPara.keySet());
     StringBuffer sbHtml = new StringBuffer();
     sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" enctype=\"multipart/form-data\" action=\"" + 
       gateway + "_input_charset=" + config.getInput_charset() + "\" method=\"" + strMethod + "\">");
 
     for (int i = 0; i < keys.size(); i++) {
       String name = (String)keys.get(i);
       String value = (String)sPara.get(name);
 
       sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
     }
 
     sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + 
       "\" style=\"display:none;\"></form>");
     sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");
 
     return sbHtml.toString();
   }
 
   private static NameValuePair[] generatNameValuePair(Map<String, String> properties)
   {
     NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
     int i = 0;
     for (Map.Entry entry : properties.entrySet()) {
       nameValuePair[(i++)] = 
         new NameValuePair((String)entry.getKey(), 
         (String)entry.getValue());
     }
 
     return nameValuePair;
   }
 
   public static String sendPostInfo(AlipayConfig config, Map<String, String> sParaTemp, String gateway)
     throws Exception
   {
     Map sPara = buildRequestPara(config, sParaTemp);
 
     HttpProtocolHandler httpProtocolHandler = 
       HttpProtocolHandler.getInstance();
 
     HttpRequest request = new HttpRequest(HttpResultType.BYTES);
 
     request.setCharset(config.getInput_charset());
 
     request.setParameters(generatNameValuePair(sPara));
     request.setUrl(gateway + "_input_charset=" + config.getInput_charset());
 
     HttpResponse response = httpProtocolHandler.execute(request);
     if (response == null) {
       return null;
     }
 
     String strResult = response.getStringResult();
 
     return strResult;
   }
 
   public static String buildRequest(AlipayConfig config, String type, Map<String, String> sParaTemp, String strParaFileName, String strFilePath)
     throws HttpException, IOException {
     Map sPara = buildRequestPara(config, sParaTemp);
     HttpProtocolHandler httpProtocolHandler = 
       HttpProtocolHandler.getInstance();
     HttpRequest request = new HttpRequest(HttpResultType.BYTES);
 
     request.setCharset(config.getInput_charset());
     request.setParameters(generatNameValuePair(sPara));
     if (type.equals("web")) {
    	 request.setUrl("https://mapi.alipay.com/gateway.do?_input_charset=" + 
         config.getInput_charset());
     }
     if (type.equals("wap")) {
       request.setUrl("http://wappaygw.alipay.com/service/rest.htm?_input_charset=" + 
         config.getInput_charset());
     }
     HttpResponse response = httpProtocolHandler.execute(request, 
       strParaFileName, strFilePath);
     if (response == null) {
       return null;
     }
     String strResult = response.getStringResult();
     return strResult;
   }

   /**
    * wap建立请求，以表单HTML形式构造（默认）
    * @param sParaTemp 请求参数数组
    * @param strMethod 提交方式。两个值可选：post、get
    * @param strButtonName 确认按钮显示文字
    * @return 提交表单HTML文本
    */
   public static String buildRequestWap(AlipayConfig config, Map<String, String> sParaTemp, String strMethod, String strButtonName) {
       //待请求参数数组
       Map<String, String> sPara = buildRequestParaWap(config, sParaTemp);
       List<String> keys = new ArrayList<String>(sPara.keySet());
       
       StringBuffer sbHtml = new StringBuffer();

       sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + ALIPAY_GATEWAY_NEW
                     + "_input_charset=" + config.getInput_charset() + "\" method=\"" + strMethod
                     + "\">");

       for (int i = 0; i < keys.size(); i++) {
           String name = (String) keys.get(i);
           String value = (String) sPara.get(name);

           sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
       }

       //submit按钮控件请不要含有name属性
       sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
       sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");

       return sbHtml.toString();
   }

   /**
    * 生成要请求给支付宝的参数数组
    * @param sParaTemp 请求前的参数数组
    * @return 要请求的参数数组
    */
   private static Map<String, String> buildRequestParaWap(AlipayConfig config, Map<String, String> sParaTemp) {
       //除去数组中的空值和签名参数
       Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
       //生成签名结果
       String mysign = buildRequestMysign(config, sPara);

       //签名结果与签名方式加入请求提交参数组中
       sPara.put("sign", mysign);
       //sPara.put("sign_type", AlipayConfig.sign_type);
       sPara.put("sign_type", config.getSign_type());

       return sPara;
   }

   private static Map<String, String> buildRequestPara(AlipayConfig config, Map<String, String> sParaTemp)
   {
     Map sPara = AlipayCore.paraFilter(sParaTemp);
 
     String mysign = AlipayCore.buildMysign(config, sPara);
 
     sPara.put("sign", mysign);
     if (!((String)sPara.get("service")).equals("alipay.wap.trade.create.direct"))
     {
       if (!((String)sPara.get("service")).equals(
         "alipay.wap.auth.authAndExecute"))
         sPara.put("sign_type", config.getSign_type());
     }
     return sPara;
   }
 
   /**
    * 生成签名结果
    * @param sPara 要签名的数组
    * @return 签名结果字符串
    */
	public static String buildRequestMysign(AlipayConfig config, Map<String, String> sPara) {
   	String prestr = AlipayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
       String mysign = "";
       if(config.getSign_type().equals("RSA") ){
    	   mysign = RSA.signWap(prestr, AlipayConfig.private_key, config.getInput_charset());
       }
       return mysign;
   }
	

    /**
     * 用于防钓鱼，调用接口query_timestamp来获取时间戳的处理函数
     * 注意：远程解析XML出错，与服务器是否支持SSL等配置有关
     * @return 时间戳字符串
     * @throws IOException
     * @throws DocumentException
     * @throws MalformedURLException
     */
	public static String query_timestamp(AlipayConfig config) throws Exception {

        //构造访问query_timestamp接口的URL串
        String strUrl = ALIPAY_GATEWAY_NEW + "service=query_timestamp&partner=" + config.getPartner() + "&_input_charset" + config.getInput_charset();
        StringBuffer result = new StringBuffer();

        SAXReader reader = new SAXReader();
        Document doc = reader.read(new URL(strUrl).openStream());

        List<Node> nodeList = doc.selectNodes("//alipay/*");

        for (Node node : nodeList) {
            // 截取部分不需要解析的信息
            if (node.getName().equals("is_success") && node.getText().equals("T")) {
                // 判断是否有成功标示
                List<Node> nodeList1 = doc.selectNodes("//response/timestamp/*");
                for (Node node1 : nodeList1) {
                    result.append(node1.getText());
                }
            }
        }

        return result.toString();
    }
 
   public static String getRequestToken(AlipayConfig config, String text)
     throws Exception
   {
     String request_token = "";
 
     String[] strSplitText = text.split("&");
 
     Map paraText = new HashMap();
     for (int i = 0; i < strSplitText.length; i++)
     {
       int nPos = strSplitText[i].indexOf("=");
 
       int nLen = strSplitText[i].length();
 
       String strKey = strSplitText[i].substring(0, nPos);
 
       String strValue = strSplitText[i].substring(nPos + 1, nLen);
 
       paraText.put(strKey, strValue);
     }
 
     if (paraText.get("res_data") != null) {
       String res_data = (String)paraText.get("res_data");
 
       if (config.getSign_type().equals("0001")) {
         res_data = RSA.decrypt(res_data, AlipayConfig.getPrivate_key(), 
           config.getInput_charset());
       }
 
       Document document = DocumentHelper.parseText(res_data);
       request_token = document.selectSingleNode(
         "//direct_trade_create_res/request_token").getText();
     }
     return request_token;
   }
 }


 
 
 