package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.CaiyuanOrderPO;
import org.springframework.stereotype.Component;

@Component
public interface CaiyuanOrderMapper {

    int insert(CaiyuanOrderPO po);

}
