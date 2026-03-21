package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.HuadaOrderPO;
import org.springframework.stereotype.Component;

@Component
public interface HuadaOrderMapper {

    int insert(HuadaOrderPO po);

}
