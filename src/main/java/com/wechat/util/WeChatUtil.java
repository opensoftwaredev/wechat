package com.wechat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;
import com.wechat.bean.ArticleItem;


/**
 * 请求校验工具类
 * 
 * @author 32950745
 *
 */
public class WeChatUtil {	

	/**
	 * 验证签名
	 * 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static boolean checkSignature(String signature, String timestamp, String nonce) {
		String[] arr = new String[] { WeChatContant.TOKEN, timestamp, nonce };
		// 将token、timestamp、nonce三个参数进行字典序排序
		// Arrays.sort(arr);
		sort(arr);
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

	private static void sort(String a[]) {
		for (int i = 0; i < a.length - 1; i++) {
			for (int j = i + 1; j < a.length; j++) {
				if (a[j].compareTo(a[i]) < 0) {
					String temp = a[i];
					a[i] = a[j];
					a[j] = temp;
				}
			}
		}
	}

	/**
	 * 解析微信发来的请求(xml)
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked"})
	public static Map<String,String> parseXml(HttpServletRequest request) throws Exception {
		// 将解析结果存储在HashMap中
		Map<String,String> map = new HashMap<String,String>();

		// 从request中取得输入流
		InputStream inputStream = request.getInputStream();
		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		// 得到xml根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();
		// 遍历所有子节点
		for (Element e : elementList)
			map.put(e.getName(), e.getText());

		// 释放资源
		inputStream.close();
		inputStream = null;
		return map;
	}
	
	public static String mapToXML(Map map) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		mapToXML2(map, sb);
		sb.append("</xml>");
		try {
			return sb.toString();
		} catch (Exception e) {
		}
		return null;
	}

	private static void mapToXML2(Map map, StringBuffer sb) {
		Set set = map.keySet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = map.get(key);
			if (null == value)
				value = "";
			if (value.getClass().getName().equals("java.util.ArrayList")) {
				ArrayList list = (ArrayList) map.get(key);
				sb.append("<" + key + ">");
				for (int i = 0; i < list.size(); i++) {
					HashMap hm = (HashMap) list.get(i);
					mapToXML2(hm, sb);
				}
				sb.append("</" + key + ">");

			} else {
				if (value instanceof HashMap) {
					sb.append("<" + key + ">");
					mapToXML2((HashMap) value, sb);
					sb.append("</" + key + ">");
				} else {
					sb.append("<" + key + "><![CDATA[" + value + "]]></" + key + ">");
				}

			}

		}
	}
	
	/**
     * 如果返回JSON数据包,转换为 JSONObject
     * @param requestUrl
     * @param requestMethod
     * @param outputStr
     * @param needCert
     * @return
     */
    public static JSONObject httpsRequestToJsonObject(String requestUrl, String requestMethod, String outputStr,boolean needCert) {
        JSONObject jsonObject = null;
        try {
             StringBuffer buffer = httpsRequest(requestUrl, requestMethod, outputStr,needCert);
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (ConnectException ce) {
            System.out.println("连接超时："+ce.getMessage());
        } catch (Exception e) {
        	System.out.println("https请求异常："+e.getMessage());
        }

        return jsonObject;
    }
    
	/**
    *
    * @param requestUrl     接口地址
    * @param requestMethod  请求方法：POST、GET...
    * @param output         接口入参
    * @param needCert       是否需要数字证书
    * @return
    */
   @SuppressWarnings("unused")
   static StringBuffer httpsRequest(String requestUrl, String requestMethod, String output,boolean needCert)
           throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException, MalformedURLException,
           IOException, ProtocolException, UnsupportedEncodingException {


       URL url = new URL(requestUrl);
       HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

       connection.setDoOutput(true);
       connection.setDoInput(true);
       connection.setUseCaches(false);
       connection.setRequestMethod(requestMethod);
       if (null != output) {
           OutputStream outputStream = connection.getOutputStream();
           outputStream.write(output.getBytes("UTF-8"));
           outputStream.close();
       }

       // 从输入流读取返回内容
       InputStream inputStream = connection.getInputStream();
       InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
       BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
       String str = null;
       StringBuffer buffer = new StringBuffer();
       while ((str = bufferedReader.readLine()) != null) {
           buffer.append(str);
       }

       bufferedReader.close();
       inputStreamReader.close();
       inputStream.close();
       inputStream = null;
       connection.disconnect();
       return buffer;
   }
	
	/**
	 * 回复文本消息
	 * @param requestMap
	 * @param content
	 * @return
	 */
	 public static String sendTextMsg(Map<String,String> requestMap,String content){
    	
    	Map<String,Object> map=new HashMap<String, Object>();
        map.put("ToUserName", requestMap.get(WeChatContant.FromUserName));
        map.put("FromUserName",  requestMap.get(WeChatContant.ToUserName));
        map.put("MsgType", WeChatContant.RESP_MESSAGE_TYPE_TEXT);
        map.put("CreateTime", new Date().getTime());
        map.put("Content", content);
        return  mapToXML(map);
    }
	 /**
	  * 回复图文消息
	  * @param requestMap
	  * @param items
	  * @return
	  */
	public static String sendArticleMsg(Map<String,String> requestMap,List<ArticleItem> items){
		if(items == null || items.size()<1){
			return "";
		}
		Map<String,Object> map=new HashMap<String, Object>();
        map.put("ToUserName", requestMap.get(WeChatContant.FromUserName));
        map.put("FromUserName", requestMap.get(WeChatContant.ToUserName));
        map.put("MsgType", "news");
        map.put("CreateTime", new Date().getTime());
        List<Map<String,Object>> Articles=new ArrayList<Map<String,Object>>();  
        for(ArticleItem itembean : items){
        	Map<String,Object> item=new HashMap<String, Object>();
	        Map<String,Object> itemContent=new HashMap<String, Object>();
	        itemContent.put("Title", itembean.getTitle());
	        itemContent.put("Description", itembean.getDescription());
	        itemContent.put("PicUrl", itembean.getPicUrl());
	        itemContent.put("Url", itembean.getUrl());
	        item.put("item",itemContent);
	        Articles.add(item);
        }
        map.put("Articles", Articles);
        map.put("ArticleCount", Articles.size());
        return mapToXML(map);
	}
	
	 /**
	  * 签名生成
	  * @param jsapi_ticket
	  * @param url
	  * @return
	  */
	public static Map<String, String> sign(String jsapi_ticket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        
        Random ran = new Random();
        String nonce_str = RandomStringUtils.randomAlphanumeric(ran.nextInt(10)+16);
        
        String timestamp = System.currentTimeMillis()+"";
        String string1;
        String signature = "";

        string1 = "jsapi_ticket=" + jsapi_ticket +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + timestamp +
                  "&url=" + url;
        System.out.println("字典排序的字符串"+string1);

        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("noncestr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }
    
    public static Map<String, String> signLocation(String appId, String url, String accesstoken) {
     Map<String, String> ret = new HashMap<String, String>();
     
     Random ran = new Random();
     String nonce_str = RandomStringUtils.randomAlphanumeric(ran.nextInt(10)+16);
     
     String timestamp = System.currentTimeMillis()+"";
     String string1;
     String signature = "";
     
     string1 = "accesstoken="+accesstoken+"&appId=" + appId +
       "&noncestr=" + nonce_str +
       "&timestamp=" + timestamp +
       "&url=" + url;
     System.out.println(string1);
     
     try
     {
      MessageDigest crypt = MessageDigest.getInstance("SHA-1");
      crypt.reset();
      crypt.update(string1.getBytes("UTF-8"));
      signature = byteToHex(crypt.digest());
     }
     catch (NoSuchAlgorithmException e)
     {
      e.printStackTrace();
     }
     catch (UnsupportedEncodingException e)
     {
      e.printStackTrace();
     }
     
     ret.put("url", url);
     ret.put("nonceStr", nonce_str);
     ret.put("timestamp", timestamp);
     ret.put("signature", signature);
     
     return ret;
    }

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	/**
	 * 获取用户的openId
	 * 
	 * @param code 微信返回的code
	 */
	public static String getOpenId(String code) {
		String openId = null;
		String oauth_url = MenuCreator2.GetOpenIdUrl.replace("APPID", WeChatContant.appID)
				.replace("SECRET", WeChatContant.appsecret).replace("CODE", code);
		System.out.println("oauth_url:" + oauth_url);
		JSONObject jsonObject = httpsRequestToJsonObject(oauth_url, "GET", null, false);
		System.out.println("jsonObject:" + jsonObject);
		Object errorCode = jsonObject.get("errcode");
		if (errorCode != null) {
			System.out.println("code不合法");
		} else {
			openId = jsonObject.getString("openid");
			System.out.println("openId:" + openId);
		}
		return openId;
	}
	
}