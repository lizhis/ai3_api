package com.ai.servicebase.mapper;

import com.ai.basecommon.core.po.base.DeviceInfoPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface DeviceInfoMapper {


    //查询最后一次进站时间
    Long findLastTimeByDeviceId(@Param("deviceId") String deviceId);

    //查最后一次进站数据
    DeviceInfoPO findLastByDeviceId(@Param("deviceId") String deviceId);

}
