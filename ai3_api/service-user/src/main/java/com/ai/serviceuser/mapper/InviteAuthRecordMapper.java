package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.InviteAuthRecordPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface InviteAuthRecordMapper {


    int insert(InviteAuthRecordPO po);


    int countByTime(@Param("userId") Long userId,@Param("time") Long time);

}
