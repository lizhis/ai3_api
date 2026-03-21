package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.shop.ShopOrderPO;
import org.springframework.stereotype.Component;

@Component
public interface ShopOrderMapper {

    int insertGetId(ShopOrderPO po);

}
