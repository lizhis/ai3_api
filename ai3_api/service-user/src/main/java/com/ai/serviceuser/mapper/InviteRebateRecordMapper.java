package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.InviteRebateRecordPO;
import org.springframework.stereotype.Component;

@Component
public interface InviteRebateRecordMapper {

    int insert(InviteRebateRecordPO po);

}
