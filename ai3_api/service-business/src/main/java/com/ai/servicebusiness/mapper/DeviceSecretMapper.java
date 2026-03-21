package com.ai.servicebusiness.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface DeviceSecretMapper {

    String findSecretByDeviceId(@Param("deviceId") String deviceId);

}
