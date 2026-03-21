package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.SeasonGiftRecordPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface SeasonGiftRecordMapper {

    int countByUserIdAndYm(@Param("userId") Long userId,@Param("ym") Integer ym);

    int insert(SeasonGiftRecordPO po);

}
