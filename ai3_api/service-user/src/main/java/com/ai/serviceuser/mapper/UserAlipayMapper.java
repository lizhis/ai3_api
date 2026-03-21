package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.UserAlipayPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface UserAlipayMapper {

    int insert(UserAlipayPO po);

    int countByUserId(@Param("userId") Long userId);

    UserAlipayPO findByUserId(@Param("userId") Long userId);

    boolean updateRealName(@Param("userId") Long userId,@Param("realName") String realName,@Param("updateTime") Long updateTime);


}
