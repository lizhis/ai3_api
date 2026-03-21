package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.shop.ShopPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface ShopMapper {

    ShopPO findById(@Param("id") Long id);

}
