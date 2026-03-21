package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.DeviceSecretPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface DeviceSecretMapper {

    int insert(DeviceSecretPO po);

    int countByDeviceId(@Param("deviceId") String deviceId);

    String findSecretByDeviceId(@Param("deviceId") String deviceId);

    boolean deleteByDeviceId(@Param("deviceId") String deviceId);

}
