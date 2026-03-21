package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.po.user.UserPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper {

    UserPO findByUserId(@Param("userId") Long userId);


}
