package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.po.shop.GiftPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface GiftMapper {


    GiftPO findByShopId(@Param("shopId") Long shopId);


    boolean incReceiveNum(@Param("id") Long id);


}
