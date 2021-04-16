package com.wechat.websocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wechat.service.WeChatService;
import com.wechat.util.CommonUtil;
import com.wechat.util.MenuCreator2;

public class WsClient extends WebSocketClient {
	
	@Autowired
	private WeChatService weChatService;

    public WsClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        System.out.println("握手成功");

    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        System.out.println("连接关闭");
    }

    @Override
    public void onError(Exception arg0) {
        System.out.println("发生错误");
    }

    @Override
    public void onMessage(String arg0) {
        System.out.println("收到消息" + arg0);
        JSONObject jsonObj = JSONObject.parseObject(arg0);
        String payload = jsonObj.getString("payload");
        JSONObject jsonPayload = JSONObject.parseObject(payload);
        // call api to sendTemplatemsg;
        String url = MenuCreator2.hostName + "/sendWXNotification?deviceId="+jsonPayload.getString("deviceId");
        
        try {
            if ((jsonObj.getString("topic").indexOf("/offline") >=0) 
            		|| (jsonObj.getString("topic").indexOf("/online") >=0) 
            		|| (jsonObj.getString("topic").indexOf("/event") >=0)) {
            	CommonUtil.httpRequest(url, "POST" ,arg0, false);
            }
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}