package com.wechat.websocket;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

public class RunClient {
    private static String uri = "ws://381x02050w.qicp.vip/websocket";
    public WebSocketClient wsc;
    public RunClient() {
    }

    public void Start() throws InterruptedException, IOException {

        wsc = new WebSocketClient();
        wsc.Connect(uri);
        Thread.sleep(1000);
        wsc.Disconnect();
    }


    public static void main(String[] args) throws InterruptedException, IOException 
    {
        RunClient t = new RunClient();
        t.Start();
        Thread.sleep(1000);
    }
}
