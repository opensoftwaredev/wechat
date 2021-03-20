package com.wechat.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wechat.bean.DeviceEntity;
import com.wechat.mapper.DeviceMapper;
import com.wechat.service.WeChatService;
import com.wechat.util.WeChatUtil;

@RestController
public class WeChatController {
	@Autowired
	private WeChatService weChatService;
	
	@Autowired
	private DeviceMapper deviceMapper;
	/**
	 * 处理微信服务器发来的get请求，进行签名的验证
	 * 
	 * signature 微信端发来的签名
	 * timestamp 微信端发来的时间戳
	 * nonce     微信端发来的随机字符串
	 * echostr   微信端发来的验证字符串
	 */
	@GetMapping(value = "wechat")
	public String validate(@RequestParam(value = "signature") String signature,
			@RequestParam(value = "timestamp") String timestamp, 
			@RequestParam(value = "nonce") String nonce,
			@RequestParam(value = "echostr") String echostr) {
		return WeChatUtil.checkSignature(signature, timestamp, nonce) ? echostr : null;

	}

	/**
	 * 此处是处理微信服务器的消息转发的
	 */
	@PostMapping(value = "wechat")
	public String processMsg(HttpServletRequest request) {
		// 调用核心服务类接收处理请求
		return weChatService.processRequest(request);		
	}
	
	/**
	 * 处理微信服务器发来的get请求，进行签名的验证
	 * 
	 */
	@GetMapping(value = "wxConfig")
	public Map<String, String> getWxConfig(@RequestParam(value = "url") String url,
			@RequestParam(value = "code") String code) {
		String openId = WeChatUtil.getOpenId(code);
		Map<String, String> jsonObj = weChatService.getSign(url);
		jsonObj.put("openId", openId);
		return jsonObj;

	}
	
	@PostMapping(value="bindDevice")
	public boolean bindDeviceWithUser(@RequestBody DeviceEntity device) {
		deviceMapper.insert(device);
		return true;
	}
	
	@GetMapping(value = "getOneDevice")
	public DeviceEntity getOneDevice(@RequestParam(value = "deviceId") String deviceId) {
		return deviceMapper.getOne(deviceId);
	}
}
