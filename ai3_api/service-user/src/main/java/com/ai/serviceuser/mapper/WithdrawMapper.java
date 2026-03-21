package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.WithdrawPO;
import org.springframework.stereotype.Component;

@Component
public interface WithdrawMapper {

    int insert(WithdrawPO po);

}
