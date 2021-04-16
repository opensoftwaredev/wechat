package com.wechat.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.wechat.bean.DeviceEntity;
import com.wechat.log.LogUtils;
import com.wechat.mapper.DeviceMapper;
import com.wechat.service.WeChatService;
import com.wechat.util.WeChatContant;
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
	 * signature 微信端发来的签名 timestamp 微信端发来的时间戳 nonce 微信端发来的随机字符串 echostr 微信端发来的验证字符串
	 */
	@GetMapping(value = "wechat")
	public String validate(@RequestParam(value = "signature") String signature,
			@RequestParam(value = "timestamp") String timestamp, @RequestParam(value = "nonce") String nonce,
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
	@PostMapping(value = "wxConfig")
	public Map<String, String> getWxConfig(@RequestBody JSONObject body, @RequestParam(value = "code") String code) {
		LogUtils.getBussinessLogger().info("收到的code为", code);
		String openId = WeChatUtil.getOpenId(code);
		Map<String, String> jsonObj = weChatService.getSign(body.get("url").toString());
		jsonObj.put("openId", openId);
		System.out.println(jsonObj);
		return jsonObj;

	}

	@GetMapping(value = "MP_verify_8sgLGwVQ8I5FBNln.txt")
	public String get123() {
		return "8sgLGwVQ8I5FBNln";

	}

	@GetMapping("/{id}")
	public String getConfigFile(@PathVariable String id) {
		File directory = new File("");// 设定为当前文件夹
		String newDir = directory.getAbsolutePath() + "\\src\\main\\resources";
		String result = "";
		try {
			System.out.println(directory.getCanonicalPath());// 获取标准的路径
			System.out.println(directory.getAbsolutePath() + "\\src\\main\\resources\\" + id + ".txt");// 获取绝对路径

			try {
				BufferedReader br = new BufferedReader(new FileReader(newDir));// 构造一个BufferedReader类来读取文件
				String s = null;
				while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
					result = result + "\n" + s;
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
		}
		return result;

	}

	@PostMapping(value = "bindDevice")
	public Long bindDeviceWithUser(@RequestBody DeviceEntity device) {
		LogUtils.getBussinessLogger().info("收到的设备信息为", device);
		deviceMapper.insert(device);
		Long id = device.getId();
		return id;
	}

	@GetMapping(value = "deleteDevice")
	public boolean deleteDevice(@RequestParam(value = "openId") String openId,
			@RequestParam(value = "deviceId") String deviceId) {
		LogUtils.getBussinessLogger().info("开始解除绑定");
		boolean handleResult = true;
		try {
			deviceMapper.delete(deviceId, openId);
		} catch (Exception e) {
			handleResult = false;
		}

		return handleResult;
	}

	@GetMapping(value = "getDeviceList")
	public List<DeviceEntity> getDeviceList(@RequestParam(value = "openId") String openId) {
		LogUtils.getBussinessLogger().info("开始查询deviceList");
		List<DeviceEntity> deviceList = deviceMapper.getOneMoreDeviceId(openId);

		return deviceList;
	}

	@PostMapping(value = "sendWXTextNotification")
	public void sendWXTextNotification(@RequestParam(value = "deviceId") String deviceId) throws IOException {
		List<DeviceEntity> openList = deviceMapper.getOneMoreOpenId(deviceId);
		for (DeviceEntity entity : openList) {
			String openId = entity.getOpenId();
			if (null != entity && null != openId) {
				WeChatUtil.sendEventTextMsg(entity.getOpenId(), "设备已经上线");
			}
		}
	}

	@PostMapping(value = "sendWXNotification")
	public List<String> sendWXNotification(@RequestParam(value = "deviceId") String deviceId,
			@RequestBody JSONObject body) throws IOException {
		List<DeviceEntity> openList = deviceMapper.getOneMoreOpenId(deviceId);
		boolean status = false;
		List<String> sendErrorList = new ArrayList();
		for (DeviceEntity entity : openList) {
			String openId = entity.getOpenId();
			if (null != entity && null != openId) {
				status = weChatService.sendTemplateMsg(entity.getOpenId(), entity.getDeviceName(), body);
				if (!status) {
					sendErrorList.add(openId);
				}
			}
		}

		return sendErrorList;
	}

	@GetMapping(value = "getDetailInfo")
	public void getDetailInfo(@RequestParam(value = "key") String key, HttpServletResponse response) {
		String endpoint = WeChatContant.END_POINT;
		String accessKeyId = WeChatContant.ACCESS_KEY;
		String accessKeySecret = WeChatContant.SEC_KEY;
		String bucketName = WeChatContant.BUCKET_NAME;
		// String objectName = postBody.get("key").toString();
		String objectName = key;

		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

//		// 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
//		// 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
//		ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File("D:\\DetectFire-2670481.jpg"));
//
//		// 关闭OSSClient。
//		ossClient.shutdown(); 
		// ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
		OSSObject ossObject = ossClient.getObject(new GetObjectRequest(bucketName, objectName));

		// 读取文件内容。
		OutputStream out = null;
		InputStream input = ossObject.getObjectContent();
		response.setContentType("image/jpeg");
		int len = 0;
		try {
			len = input.available();
			byte[] buff = new byte[len];
			out = response.getOutputStream();
			while ((len = input.read(buff)) != -1) {
				out.write(buff, 0, len);
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ossClient.shutdown();
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@GetMapping(value = "sendWXNotification1")
	public boolean sendWXNotification1(@RequestParam(value = "deviceId") String deviceId) {
//		DeviceEntity device = deviceMapper.getOne(deviceId);
//		boolean status = false;
//		if (null != device && null != device.getOpenId()) {
//			status = weChatService.sendTemplateMsg1(device.getOpenId());
//		}
//		return status;
		return true;
	}
}
