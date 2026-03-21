package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.po.shop.GiftCodeRecordPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface GiftCodeRecordMapper {


    int insert(GiftCodeRecordPO po);

    //查询总使用量
    int countTotalNum(@Param("code") String code);

    //查询今日使用量
    int countDayNum(@Param("code") String code,@Param("ymd") Integer ymd);

    //查询是否使用过
    int countUse(@Param("userId") Long userId,@Param("code") String code);

}
