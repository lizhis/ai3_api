package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.AlipayOrderPO;
import org.springframework.stereotype.Component;

@Component
public interface AlipayOrderMapper {

    int insert(AlipayOrderPO po);

}
