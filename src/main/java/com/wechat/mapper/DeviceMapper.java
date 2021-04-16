package com.wechat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wechat.bean.DeviceEntity;

public interface DeviceMapper {
	
	List<DeviceEntity> getAll();
	
	List<DeviceEntity> getOneMoreOpenId(String deviceId);
	
	List<DeviceEntity> getOneMoreDeviceId(String openId);

	int insert(DeviceEntity device);

	void delete(@Param("deviceId") String deviceId, @Param("openId") String openId);

}