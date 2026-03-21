package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.UserDataDayPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface UserDataDayMapper {

    UserDataDayPO find(@Param("userId") Long userId, @Param("ymd") Integer ymd);



}
