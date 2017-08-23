package com.shopping.core.tools;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shopping.core.tools.bean.WxToken;

/**
 * Created by John on 2016/1/13.
 */
public class WxCommonUtil {

    private static Logger log = LoggerFactory.getLogger(WxCommonUtil.class);

    // 凭证获取（GET）
    public final static String WxToken_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /**
     * 发送https请求
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return 返回微信服务器响应的信息
     */
    public static String httpsRequestString(String requestUrl, String requestMethod, String outputStr) {
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            return buffer.toString();
        } catch (ConnectException ce) {
            log.error("连接超时：{}", ce);
        } catch (Exception e) {
            log.error("https请求异常：{}", e);
        }
        return null;
    }
    /**
     * 发送https请求
     *
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }

            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            jsonObject = JSONObject.fromObject(buffer.toString());
        } catch (ConnectException ce) {
            log.error("连接超时：{}", ce);
        } catch (Exception e) {
            log.error("https请求异常：{}", e);
        }
        return jsonObject;
    }

    /**
     * 获取接口访问凭证
     *
     * @param appid 凭证
     * @param appsecret 密钥
     * @return
     */
    public static WxToken getToken(String appid, String appsecret) {
        WxToken WxToken = null;
        String requestUrl = WxToken_url.replace("APPID", appid).replace("APPSECRET", appsecret);
        // 发起GET请求获取凭证
        JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject) {
            try {
                WxToken = new WxToken();
                WxToken.setAccessToken(jsonObject.getString("access_token"));
                WxToken.setExpiresIn(jsonObject.getInt("expires_in"));
            } catch (JSONException e) {
                WxToken = null;
                // 获取WxToken失败
                log.error("获取WxToken失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
            }
        }
        return WxToken;
    }
    /**
     * 获取access_token
     * @param appid 凭证
     * @param appsecret 密钥
     * @return
     */
    /*public static AccessToken getAccessToken(SystemService systemService,String appid,String appsecret) {

        AccessTokenYw accessTocken = getRealAccessToken(systemService);

        if(accessTocken!=null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date end = new java.util.Date();
            java.util.Date start = new java.util.Date(accessTocken.getAddTime().getTime());
            if(end.getTime()-accessTocken.getAddTime().getTime()>accessTocken.getExpires_in()*1000){
                AccessToken accessToken = null;
                String requestUrl = access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
                JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
                // 如果请求成功
                if (null != jsonObject) {
                    try {
                        accessToken = new AccessToken();
                        accessToken.setToken(jsonObject.getString("access_token"));
                        accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
                        //凭证过期更新凭证
                        AccessTokenYw atyw = new AccessTokenYw();
                        atyw.setId(accessTocken.getId());
                        atyw.setExpires_in(jsonObject.getInt("expires_in"));
                        atyw.setAccess_token(jsonObject.getString("access_token"));
                        updateAccessToken(atyw,systemService);
                    } catch (Exception e) {
                        accessToken = null;
                        // 获取token失败
                        String wrongMessage = "获取token失败 errcode:{} errmsg:{}"+jsonObject.getInt("errcode")+jsonObject.getString("errmsg");
                        log.info(wrongMessage);
                    }
                }
                return accessToken;
            }else{

                AccessToken  accessToken = new AccessToken();
                accessToken.setToken(accessTocken.getAccess_token());
                accessToken.setExpiresIn(accessTocken.getExpires_in());
                return accessToken;
            }
        }else{

            AccessToken accessToken = null;
            String requestUrl = access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
            JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
            // 如果请求成功
            if (null != jsonObject) {
                try {
                    accessToken = new AccessToken();
                    accessToken.setToken(jsonObject.getString("access_token"));
                    accessToken.setExpiresIn(jsonObject.getInt("expires_in"));

                    AccessTokenYw atyw = new AccessTokenYw();
                    atyw.setExpires_in(jsonObject.getInt("expires_in"));
                    atyw.setAccess_token(jsonObject.getString("access_token"));
                    saveAccessToken(atyw,systemService);

                } catch (Exception e) {
                    accessToken = null;
                    // 获取token失败
                    String wrongMessage = "获取token失败 errcode:{} errmsg:{}"+jsonObject.getInt("errcode")+jsonObject.getString("errmsg");
                    log.info(wrongMessage);
                }
            }
            return accessToken;
        }
    }*/


    /**
     * 从数据库中读取凭证
     * @return
     */
    /*public static AccessTokenYw getRealAccessToken(SystemService systemService){
        List<AccessTokenYw> accessTockenList = systemService.findByQueryString("from AccessTokenYw");
        return accessTockenList.get(0);
    }*/

    /**
     * 保存凭证
     * @return
     */
    /*public static void saveAccessToken( AccessTokenYw accessTocken,SystemService systemService){
        systemService.save(accessTocken);
    }*/

    /**
     * 更新凭证
     * @return
     */
    /*public static void updateAccessToken( AccessTokenYw accessTocken,SystemService systemService){
        String sql = "update accesstoken set access_token='"+accessTocken.getAccess_token()+"',expires_ib="+accessTocken.getExpires_in()+",addtime=now() where id='"+accessTocken.getId()+"'";
        systemService.updateBySqlString(sql);
    }*/

    /**
     * 编码
     * @param bstr
     * @return String
     */
    public static String encode(byte[] bstr){
        return new sun.misc.BASE64Encoder().encode(bstr);
    }

    /**
     * 解码
     * @param str
     * @return string
     */
    public static byte[] decode(String str){

        byte[] bt = null;
        try {
            sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            bt = decoder.decodeBuffer( str );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bt;

    }
    /**
     * URL编码（utf-8）
     *
     * @param source
     * @return
     */
    public static String urlEncodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据内容类型判断文件扩展名
     *
     * @param contentType 内容类型
     * @return
     */
    public static String getFileExt(String contentType) {
        String fileExt = "";
        if ("image/jpeg".equals(contentType))
            fileExt = ".jpg";
        else if ("audio/mpeg".equals(contentType))
            fileExt = ".mp3";
        else if ("audio/amr".equals(contentType))
            fileExt = ".amr";
        else if ("video/mp4".equals(contentType))
            fileExt = ".mp4";
        else if ("video/mpeg4".equals(contentType))
            fileExt = ".mp4";
        return fileExt;
    }

    public static String createNoncestr(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < length; i++) {
            Random rd = new Random();
            res += chars.indexOf(rd.nextInt(chars.length() - 1));
        }
        return res;
    }
    public static String createNoncestr() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < 16; i++) {
            Random rd = new Random();
            res += chars.charAt(rd.nextInt(chars.length() - 1));
        }
        return res;
    }
    /**
     * 获取当前时间 yyyyMMddHHmmss
     *
     * @return String
     */
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }
    /**
     * 取出一个指定长度大小的随机正整数.
     *
     * @param length
     *            int 设定所取出随机数的长度。length小于11
     * @return int 返回生成的随机数。
     */
    public static int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }
    /**
     * 元转换成分
     * @param amount
     * @return
     */
    public static String getMoney(String amount) {
        if(amount==null){
            return "";
        }
        // 金额转化为分为单位
        String currency =  amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        if(index == -1){
            amLong = Long.valueOf(currency+"00");
        }else if(length - index >= 3){
            amLong = Long.valueOf((currency.substring(0, index+3)).replace(".", ""));
        }else if(length - index == 2){
            amLong = Long.valueOf((currency.substring(0, index+2)).replace(".", "")+0);
        }else{
            amLong = Long.valueOf((currency.substring(0, index+1)).replace(".", "")+"00");
        }
        return amLong.toString();
    }
    /**
     * 获取本机Ip
     *
     *  通过 获取系统所有的networkInterface网络接口 然后遍历 每个网络下的InterfaceAddress组。
     *  获得符合 <code>InetAddress instanceof Inet4Address</code> 条件的一个IpV4地址
     * @return
     */
    public static String localIp() {
        String ip = null;
        Enumeration allNetInterfaces;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                List<InterfaceAddress> InterfaceAddress = netInterface.getInterfaceAddresses();
                for (InterfaceAddress add : InterfaceAddress) {
                    InetAddress Ip = add.getAddress();
                    if (Ip != null && Ip instanceof Inet4Address) {
                        ip = Ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            //logger.warn("获取本机Ip失败:异常信息:"+e.getMessage());
        }
        return ip;
    }
    /**
     * 获取访问用户的客户端IP（适用于公网与局域网）.
     */
    public static final String getIpAddr(final HttpServletRequest request) {
        /*if (request == null) {
            throw (new Exception("getIpAddr method HttpServletRequest Object is null"));
        }*/
        String ipString = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getRemoteAddr();
        }

        // 多个路由时，取第一个非unknown的ip
        final String[] arr = ipString.split(",");
        for (final String str : arr) {
            if (!"unknown".equalsIgnoreCase(str)) {
                ipString = str;
                break;
            }
        }

        return ipString;
    }
    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     * @param strxml
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map doXMLParse(String strxml) throws JDOMException, IOException {
        strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

        if(null == strxml || "".equals(strxml)) {
            return null;
        }
        Map m = new HashMap();
        InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        Iterator it = list.iterator();
        while(it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if(children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = WxCommonUtil.getChildrenText(children);
            }
            m.put(k, v);
        }
        //关闭流
        in.close();
        return m;
    }

    /**
     * 获取子结点的xml
     * @param children
     * @return String
     */
    @SuppressWarnings("rawtypes")
    public static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if(!children.isEmpty()) {
            Iterator it = children.iterator();
            while(it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {
                    sb.append(WxCommonUtil.getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }
        return sb.toString();
    }

    /**
     * 发送XML格式数据到微信服务器 告知微信服务器回调信息已经收到。
     *
     * 作者: YUKE 日期：2015年6月10日 上午9:27:33
     *
     * @param return_code
     * @param return_msg
     * @return
     */
    public static String setXML(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code
                + "]]></return_code><return_msg><![CDATA[" + return_msg
                + "]]></return_msg></xml>";
    }

    /**
     * sign签名，必须使用MD5签名，且编码为UTF-8
     *
     * 作者: YUKE 日期：2015年6月10日 上午9:31:24
     *
     * @param characterEncoding
     * @param parameters
     * @return
     */
    public static String createSignMD5(String characterEncoding, SortedMap<Object, Object> parameters, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<Object, Object>> es = parameters.entrySet();
        Iterator<Map.Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        /** 支付密钥必须参与加密，放在字符串最后面 */
        sb.append("key=" + API_KEY);
        String sign = Md5Encrypt.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }

    /**
     * SHA1加密，该加密是对wx.config配置中使用到的参数进行SHA1加密，这里不需要key参与加密
     *
     * 作者: YUKE 日期：2015年6月10日 上午9:31:24
     *
     * @param characterEncoding
     * @param parameters
     * @return
     */
    public static String createSignSHA1(String characterEncoding, SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<Object, Object>> es = parameters.entrySet();
        Iterator<Map.Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
            }
        }
        String str = sb.toString();
        String sign = Sha1Util.getSha1(str.substring(0, str.length() - 1));
        return sign;
    }

    /**
     * 将请求参数转换为XML格式的string字符串，微信服务器接收的是xml格式的字符串
     *
     * 作者: YUKE 日期：2015年6月10日 上午9:25:51
     *
     * @param parameters
     * @return
     */
    public static String getRequestXml(SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set<Map.Entry<Object, Object>> es = parameters.entrySet();
        Iterator<Map.Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            } else {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 验证签名
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static boolean checkSignature(String token,String signature, String timestamp, String nonce) {
        String[] arr = new String[] { token, timestamp, nonce };
        // 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        content = null;
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {

        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }





    /**
     * 解密微信发过来的密文消息
     *
     * @return 加密后的内容
     */
    /*public static String decryptMsg(String msgSignature,String timeStamp,String nonce,String encrypt_msg) {
        WXBizMsgCrypt pc;
        String result ="";
        try {
            pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
            result = pc.decryptMsg(msgSignature, timeStamp, nonce, encrypt_msg);
        } catch (AesException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }*/

    /**
     * 加密给微信的消息内容
     * @param replayMsg
     * @param timeStamp
     * @param nonce
     * @return
     */
    /*public static String ecryptMsg(String replayMsg,String timeStamp, String nonce) {
        WXBizMsgCrypt pc;
        String result ="";
        try {
            pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
            result = pc.encryptMsg(replayMsg, timeStamp, nonce);
        } catch (AesException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }*/
}
