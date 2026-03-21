package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.po.user.UserBankcardPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface UserBankcardMapper {

    UserBankcardPO findByUserId(@Param("userId") Long userId);


}
