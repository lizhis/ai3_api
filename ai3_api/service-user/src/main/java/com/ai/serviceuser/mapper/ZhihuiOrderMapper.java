package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.ZhihuiOrderPO;
import org.springframework.stereotype.Component;

@Component
public interface ZhihuiOrderMapper {

    int insert(ZhihuiOrderPO po);

}
