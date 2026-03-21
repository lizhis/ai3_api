package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.QilinOrderPO;
import org.springframework.stereotype.Component;

@Component
public interface QilinOrderMapper {

    int insert(QilinOrderPO po);

}
