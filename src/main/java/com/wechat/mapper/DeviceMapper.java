package com.wechat.mapper;

import java.util.List;

import com.wechat.bean.DeviceEntity;

public interface DeviceMapper {
	
	List<DeviceEntity> getAll();
	
	DeviceEntity getOne(String deviceId);

	void insert(DeviceEntity device);

	void delete(String deviceId);

}