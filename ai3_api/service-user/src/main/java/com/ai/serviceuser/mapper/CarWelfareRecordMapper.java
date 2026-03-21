package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.CarWelfareRecordPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface CarWelfareRecordMapper {


    int insertGetId(CarWelfareRecordPO po);

    int countBy(@Param("userId") Long userId,@Param("ymd") Integer ymd);

}
