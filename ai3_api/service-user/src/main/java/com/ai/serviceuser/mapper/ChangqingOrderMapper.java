package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.ChangqingOrderPO;
import org.springframework.stereotype.Component;

@Component
public interface ChangqingOrderMapper {

    int insert(ChangqingOrderPO po);

}
