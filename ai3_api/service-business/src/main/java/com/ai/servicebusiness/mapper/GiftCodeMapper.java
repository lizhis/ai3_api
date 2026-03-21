package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.po.shop.GiftCodePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface GiftCodeMapper {


    GiftCodePO findByCode(@Param("code") String code);

    //更新使用次数
    boolean updateUseNum(@Param("id") Long id, @Param("useNum") Integer useNum);


}
