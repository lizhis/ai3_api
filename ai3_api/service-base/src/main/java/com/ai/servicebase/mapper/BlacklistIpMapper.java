package com.ai.servicebase.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;


@Component
public interface BlacklistIpMapper{

    int existByIp(@Param("ip") String ip);

    int insert(@Param("ip") String ip);

}
