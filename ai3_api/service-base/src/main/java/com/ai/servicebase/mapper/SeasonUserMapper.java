package com.ai.servicebase.mapper;

import com.ai.basecommon.core.po.user.SeasonUserPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface SeasonUserMapper {

    SeasonUserPO findByUserId(@Param("userId") Long userId);

}
