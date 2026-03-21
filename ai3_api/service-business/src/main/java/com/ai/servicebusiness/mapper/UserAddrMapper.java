package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.po.user.UserAddrPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface UserAddrMapper {

    UserAddrPO findDefaultAddr(@Param("userId") Long userId);

}
