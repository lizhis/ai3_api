package com.ai.servicebase.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;


@Component
public interface BlacklistDeviceMapper {


    int existByDeviceId(@Param("deviceId") String deviceId);

    int insert(@Param("deviceId") String deviceId);


}
