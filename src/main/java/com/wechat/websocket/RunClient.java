package com.wechat.websocket;

import java.net.URI;

import org.java_websocket.enums.ReadyState;

import com.alibaba.fastjson.JSONObject;

public class RunClient {
	private static String uri = "ws://106.52.62.249:8848/messaging/4459cc5cc18c5558d45d096789d2159a";

	public static void main(String[] args) {
		boolean flag = true;
		try {
			WsClient myClient = new WsClient(new URI(uri));
			myClient.connect();
			// 判断是否连接成功，未成功后面发送消息时会报错
			while (!myClient.getReadyState().equals(ReadyState.OPEN)) {
				System.out.println("连接中···请稍后");
				Thread.sleep(1000);
			}
			JSONObject target = new JSONObject();
			target.put("type", "sub");
			target.put("topic", "/device/*/*/**");
			// target.put("parameter", new JSONObject());
			target.put("id", "123456");
			myClient.send(target.toJSONString());
			System.out.println("发送成功");

			while(flag) {
				new Thread() {
					@Override
					public void run() {
						try {
							// 间隔10秒发送心跳
							Thread.sleep(100000);
							myClient.send("心跳包");
							System.out.println("还活着");
						} catch (Exception e) {
							// 捕获异常进行重连
							myClient.reconnect();
							myClient.send(target.toJSONString());
							System.out.println("再次发送成功");
						}
					}
				}.start();
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		
	}
}
