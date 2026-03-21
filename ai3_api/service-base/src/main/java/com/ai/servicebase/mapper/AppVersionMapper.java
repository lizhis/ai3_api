package com.ai.servicebase.mapper;


import com.ai.basecommon.core.vo.base.AppVersionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface AppVersionMapper {


    //查询最新版本号
    AppVersionVO findLastVersion(@Param("platformType") Integer platformType);


}
