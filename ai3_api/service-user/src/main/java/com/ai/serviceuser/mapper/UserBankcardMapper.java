package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.UserBankcardPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface UserBankcardMapper {

    int insert(UserBankcardPO po);

    int countByUserId(@Param("userId") Long userId);

    UserBankcardPO findByUserId(@Param("userId") Long userId);


}
