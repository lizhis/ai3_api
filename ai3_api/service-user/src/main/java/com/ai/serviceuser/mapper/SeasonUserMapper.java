package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.SeasonUserPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface SeasonUserMapper {

    int insert(SeasonUserPO po);

    SeasonUserPO findByUserId(@Param("userId") Long userId);

    boolean updateRenew(@Param("id") Long id,@Param("expireTime") Long expireTime,@Param("updateTime") Long updateTime);

}
