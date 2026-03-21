package com.ai.servicebase.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;


@Component
public interface BlacklistTelMapper {


    int existByTel(@Param("tel") String tel);

    int insert(@Param("tel") String tel);

}
