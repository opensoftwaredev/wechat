package com.wechat.bean;

import java.io.Serializable;

public class DeviceEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String openId;
	private String deviceId;
	private String deviceName;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	

}