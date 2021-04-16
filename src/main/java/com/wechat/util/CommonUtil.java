package com.wechat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CommonUtil {
	
	/**
    *
    * @param requestUrl     接口地址
    * @param requestMethod  请求方法：POST、GET...
    * @param output         接口入参
    * @param needCert       是否需要数字证书
    * @return
    */
   @SuppressWarnings("unused")
public static StringBuffer httpRequest(String requestUrl, String requestMethod, String output,boolean needCert)
           throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException, MalformedURLException,
           IOException, ProtocolException, UnsupportedEncodingException {


       URL url = new URL(requestUrl);
       HttpURLConnection connection = (HttpURLConnection) url.openConnection();

       connection.setDoOutput(true);
       connection.setDoInput(true);
       connection.setUseCaches(false);                                                                                
       connection.setRequestMethod(requestMethod);
       connection.setRequestProperty("Content-Type", 
               "application/json");
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

}
