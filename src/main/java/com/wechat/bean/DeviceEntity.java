package com.wechat.bean;

import java.io.Serializable;

public class DeviceEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private String openId;
	private String deviceId;
	public DeviceEntity() {
		super();
	}

	public DeviceEntity(String openId, String deviceId) {
		super();
		this.setOpenId(openId);
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	

}