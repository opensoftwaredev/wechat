package com.wechat.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

public interface WeChatService {
	/**
	 * 核心处理方法
	 * @param request
	 * @return
	 */
	public String processRequest(HttpServletRequest request);
	/**
	 * 获取token
	 * @return
	 */
	public String getToken();
	/**
	 * 获取jsapi_ticket
	 * @return
	 */
	public String getJsapiTicket();
	/**
	 * 获取用户信息
	 * @return
	 */
	public Map<String,String> getUserInfo(String openid);
	/**
	 * 获取Sign用于调用微信JSAPI
	 * @return
	 */
	public Map<String,String> getSign(String url);
	
	/**
	 * 发送模板消息
	 * @return
	 */
	public boolean sendTemplateMsg(String openId, String deviceName, JSONObject key);
	
	public boolean sendTemplateMsg1(String openId);
}
