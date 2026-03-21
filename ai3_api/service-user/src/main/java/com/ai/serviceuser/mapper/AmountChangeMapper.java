package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.AmountChangePO;
import org.springframework.stereotype.Component;

@Component
public interface AmountChangeMapper {


    int insert(AmountChangePO po);

}
