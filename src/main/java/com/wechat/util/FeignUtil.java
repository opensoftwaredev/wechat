package com.wechat.util;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;


@FeignClient(url="https://api.weixin.qq.com",value="xxx")
public interface FeignUtil {
	/**
	 * 获取token
	 * @return
	 */
	@RequestMapping(value = "/cgi-bin/token?grant_type=client_credential&appid={appid}&secret={secret}",method=RequestMethod.GET)
	public Map<String,String> getToken(@PathVariable("appid") String appid,@PathVariable("secret") String secret);
	/**
	 * 获取用户信息
	 * @param token
	 * @param openid
	 * @return
	 */
	@RequestMapping(value="/cgi-bin/user/info?access_token={token}&openid={openid}&lang=zh_CN",method = RequestMethod.GET)
	public Map<String,String> getUserInfo(@PathVariable("token") String token,@PathVariable("openid") String openid);

	/**
	 * 发送模板消息
	 * @param token
	 * @return
	 */
	@RequestMapping(value="/cgi-bin/message/template/send?access_token={token}",method = RequestMethod.POST)
	public JSONObject setTemplateMsg(@PathVariable("token") String token);
	
	/**
	* 获取Sign用于调用微信JSAPI
	* @param token
	* @return
	*/
	@RequestMapping(value="/cgi-bin/ticket/getticket?access_token={token}&type=jsapi",method = RequestMethod.GET)
	public JSONObject getJsapiTicket(@PathVariable("token") String token);
}
