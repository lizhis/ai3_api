package com.ai.servicebase.mapper;

import com.ai.basecommon.core.dto.user.UserInfoDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper {


    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    UserInfoDTO userInfo(@Param("userId") Long userId);

    //查询用户的实名状态
    Integer findAuthStatue(@Param("userId") Long userId);

}
