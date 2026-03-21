package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.UserLoginPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserLoginMapper {

    int insert(UserLoginPO po);

    List<UserLoginPO> selectByOnlineUserId(@Param("userId") Long userId);


    boolean updateDisableByUserId(@Param("userId") Long userId);


}
