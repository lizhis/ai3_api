package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.dto.map.LocationCodeDTO;
import com.ai.basecommon.core.dto.map.LocationDTO;
import com.ai.basecommon.core.po.base.DeviceInfoPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface DeviceInfoMapper {


    int insert(DeviceInfoPO po);

    //查询最后一次进站时间
    Long findLastTimeByDeviceId(@Param("deviceId") String deviceId);

    //查最后一次进站数据
    DeviceInfoPO findLastByDeviceId(@Param("deviceId") String deviceId);


    //查询设备最后的经纬度
    LocationDTO findLocationByDeviceId(@Param("deviceId") String deviceId);

    //查询设备最后的行政代码
    LocationCodeDTO findLocationCodeByDeviceId(@Param("deviceId") String deviceId);


}
