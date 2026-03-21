package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.shop.GiftCodePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface GiftCodeMapper {


    int insert(GiftCodePO po);

    int countByCode(@Param("code") String code);

    //上次邀请赠送的福利码
    GiftCodePO findLastForInvite(@Param("userId") Long userId);

    //数量
    int countByUserSource(@Param("userId") Long userId,@Param("source") Integer source);


}
