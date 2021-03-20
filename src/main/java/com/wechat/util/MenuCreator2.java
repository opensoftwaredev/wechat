package com.wechat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.alibaba.fastjson.JSONObject;
import com.wechat.bean.Button;
import com.wechat.bean.Menu;

public class MenuCreator2 {
	
	public static final String OAuthUrlTpl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={0}&redirect_uri={1}&response_type=code&scope={2}&state={3}#wechat_redirect";

	public static final String TokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appid}&secret={secret}";
	
	public static final String GetOpenIdUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	
	public static MessageFormat mf = new MessageFormat(OAuthUrlTpl);
	
	public static final String appid = "wx50e494948f4a9074";
	
	public static final String appsecret = "1e6c95e1d4480cba4295659d0330956d";
	
	public static final String hostName = "http://381x02050w.qicp.vip";
	
	public static JSONObject createMenu(){
		JSONObject tokenMap = WeChatUtil.httpsRequestToJsonObject(TokenUrl.replace("{appid}", appid).replace("{secret}", appsecret), "GET" ,null, false);
		String token = (String) tokenMap.get("access_token");
		String mainUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token={ACCESS_TOKEN}";
		mainUrl = mainUrl.replace("{ACCESS_TOKEN}", token);
		String params = null;
		try {
			params = getJsonParam();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JSONObject result = WeChatUtil.httpsRequestToJsonObject(mainUrl, "POST" ,params, false);
		return result;
	}
	
	
	private static String getJsonParam() throws UnsupportedEncodingException{
		Menu menu = new Menu();
		//ä¸»è�œå�•list
		List<Button> blist = new ArrayList<Button>();
		
		Button b1 = new Button();
		b1.setType("view");
		b1.setName("绑设备");
//		b1.setUrl(hostName+"/getOpenId");
		b1.setUrl(mf.format(OAuthUrlTpl,appid,hostName+"/page/index/","snsapi_base",1));
		
		Button b3 = new Button();
		b3.setType("view");
		b3.setName("ä¼˜æƒ æ´»åŠ¨");
		b3.setUrl("about:blank");
		
		List<Button> sub_button3 = new ArrayList<Button>();
		Button b31 = new Button();
		b31.setType("view");
		b31.setName("æœ€æ–°æ´»åŠ¨");
		b31.setUrl(	hostName+"/menunew/currentact.jsp");
		Button b32 = new Button();
		b32.setType("view");
		b32.setName("æŽ¨è��æœ‰å¥–");
		b32.setUrl(mf.format(OAuthUrlTpl,appid,URLEncoder.encode(hostName+"/menunew/invitefriend.jsp","UTF-8"),"snsapi_base",1));
		
		sub_button3.add(b31);
		sub_button3.add(b32);
		b3.setSub_button(sub_button3);
		
		Button b4 = new Button();
		b4.setType("view");
		b4.setName("æˆ‘çš„");
		b4.setUrl("about:blank");
		
		List<Button> sub_button4 = new ArrayList<Button>();
		Button b41 = new Button();
		b41.setType("view");
		b41.setName("è´¦å�·ç»‘å®š");
		b41.setUrl(mf.format(OAuthUrlTpl,appid,URLEncoder.encode(hostName+"/menunew/bind.jsp","UTF-8"),"snsapi_base",1));
		
		Button b42 = new Button();
		b42.setType("view");
		b42.setName("APPä¸‹è½½");
		b42.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.eyegrid.deminapp#opened");
		
		Button b43 = new Button();
		b43.setType("view");
		b43.setName("ç”¨æˆ·å��é¦ˆ");
		//b43.setUrl(mf.format(OAuthUrlTpl,appid,hostName+"/menunew/building.html","snsapi_base",1));
		b43.setUrl(hostName+"/menunew/feedback.jsp");
		
		sub_button4.add(b41);
		sub_button4.add(b42);
		sub_button4.add(b43);
		b4.setSub_button(sub_button4);
		
		blist.add(b1);
		//blist.add(b3);
		//blist.add(b4);		
		menu.setButton(blist);
		
//		JsonConfig jsonConfig = new JsonConfig();
//		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
//			@Override
//			public boolean apply(Object obj, String key, Object value) {
//				if(value == null || "".equals(value)){
//					return true;
//				}
//				return false;
//			}
//		});
		JSONObject json =  (JSONObject) JSONObject.toJSON(menu);
		System.out.println(json);
		return json.toString();
	}
	public static void main(String[] args) throws UnsupportedEncodingException {
		getJsonParam();
//		JSONObject message = createMenu();
//		System.out.println("result:"+message);
//		if(message.toString().indexOf("\"errcode\":0")>-1){
//			System.out.println("success");
//		}else{
//			System.out.println("ç”Ÿæˆ�å¤±è´¥ï¼�");
//		}
		//sendTemplateMsg();
		
		
		//https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx3da660c5ed5fc92a&redirect_uri=http://wx.eyegrid.cn/deminwx/menunew/invitefriend.jsp&response_type=code&scope=snsapi_base&state=1#wechat_redirect
		//https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx3da660c5ed5fc92a&redirect_uri=http://wx.eyegrid.cn/deminwx/menunew/invitefriend.jsp&response_type=code&scope=snsapi_base&state=1#wechat_redirect
/*		try {
			System.out.println(mf.format(OAuthUrlTpl,appid,URLEncoder.encode(hostName+"/menunew/invitefriend.jsp", "UTF-8"),"snsapi_base",1) );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
	}
	
	public static void sendTemplateMsg() {
		JSONObject tokenMap = WeChatUtil.httpsRequestToJsonObject(TokenUrl.replace("{appid}", appid).replace("{secret}", appsecret), "GET" ,null, false);
		String token = (String) tokenMap.get("access_token");
        
        String postUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", "o7MFJ5pug06YahrtPVA_nC-O1WmE");   // openid
        jsonObject.put("template_id", "im8AlmFF6Huf5117PJdxo7ojmOIEQfhLGlz8m7VWLz0");
        jsonObject.put("url", "http://www.baidu.com");
 
        JSONObject data = new JSONObject();
        JSONObject first = new JSONObject();
        first.put("value", "您绑定的设备已经着火");
        first.put("color", "#173177");
        JSONObject keyword1 = new JSONObject();
        keyword1.put("value", "123！23123123");
        keyword1.put("color", "#173177");
        JSONObject keyword2 = new JSONObject();
        keyword2.put("value", "hello");
        keyword2.put("color", "#173177");
        JSONObject keyword3 = new JSONObject();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        keyword3.put("value", df.format(new Date()));
        keyword3.put("color", "#173177");
        JSONObject remark = new JSONObject();
        remark.put("value", "已经着火了，请注意查看");
        remark.put("color", "#173177");
        
        data.put("first",first);
        data.put("device",keyword1);
        data.put("time",keyword3);
        data.put("remark",remark);
 
        jsonObject.put("data", data);
 
		JSONObject result = WeChatUtil.httpsRequestToJsonObject(postUrl, "POST" ,jsonObject.toJSONString(), false);
        int errcode = result.getIntValue("errcode");
        if(errcode == 0){
            // 发送成功
            System.out.println("发送成功");
        } else {
            // 发送失败
            System.out.println("发送失败");
        }
	}
}

