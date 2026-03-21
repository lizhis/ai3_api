package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.Huada2OrderPO;
import org.springframework.stereotype.Component;

@Component
public interface Huada2OrderMapper {

    int insert(Huada2OrderPO po);

}
