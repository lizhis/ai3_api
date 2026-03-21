package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.SuggestPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface SuggestMapper {

    int insert(SuggestPO po);

    String lastReply(@Param("userId") Long userId);

}
