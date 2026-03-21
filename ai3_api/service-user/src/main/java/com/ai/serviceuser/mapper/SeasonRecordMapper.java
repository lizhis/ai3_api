package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.SeasonRecordPO;
import org.springframework.stereotype.Component;

@Component
public interface SeasonRecordMapper {

    int insertGetId(SeasonRecordPO po);

}
