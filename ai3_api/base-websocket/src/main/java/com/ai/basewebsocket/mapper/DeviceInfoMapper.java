package com.ai.basewebsocket.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface DeviceInfoMapper {

    int countByUserIdYmd(@Param("userId") Long userId,@Param("ymd") Integer ymd);

}
