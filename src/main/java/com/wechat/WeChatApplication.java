package com.wechat;

import java.net.URI;

import org.java_websocket.enums.ReadyState;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

import com.alibaba.fastjson.JSONObject;
import com.wechat.log.LogUtils;
import com.wechat.util.FeignUtil;
import com.wechat.websocket.WsClient;

@SpringBootApplication
@EnableFeignClients(clients = {FeignUtil.class})
@EnableCaching
@MapperScan("com.wechat.mapper")
public class WeChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeChatApplication.class, args);
        LogUtils.getBussinessLogger().info("======启动成功========");
        String uri = "ws://106.52.62.249:8848/messaging/4459cc5cc18c5558d45d096789d2159a";
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
            //target.put("parameter", new JSONObject());
            target.put("id", "123456");
            myClient.send(target.toJSONString());
            System.out.println("发送成功");
			while(flag) {
						try {
							// 间隔10秒发送心跳
							Thread.sleep(100000);
							myClient.send(target.toJSONString());
							System.out.println("还活着");
						} catch (Exception e) {
							LogUtils.getExceptionLogger().error(e.getMessage());
							// 捕获异常进行重连
							myClient.reconnect();
							int i=0;
				            while (!myClient.getReadyState().equals(ReadyState.OPEN)) {
				                System.out.println("连接中···请稍后");
				                Thread.sleep(1000);
				                i++;
				                if (i>10) {
				                	break;
				                }
				            }
							myClient.send(target.toJSONString());
							System.out.println("再次发送成功");
							LogUtils.getBussinessLogger().info("再次发送成功");
						}
			}
        } catch (Exception e) {
        	flag = false;
        	LogUtils.getExceptionLogger().error(e.getMessage());
            e.printStackTrace();
        }
	}
//	@Configuration
//	public class WebSocketConfig {
//	    @Bean
//	    public ServerEndpointExporter serverEndpointExporter() {
//	        return new ServerEndpointExporter();
//	    }
//
//	}
}
