package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.SmsRecordPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface SmsRecordMapper {

    int insert(SmsRecordPO po);

    //查询设备号在今天发了多少条短信
    int countByDeviceIdToday(@Param("deviceId") String deviceId,@Param("ymd") Integer ymd);

    //查询手机号发送验证码类型的最后一条记录
    SmsRecordPO findEndByTelAndYzmType(@Param("tel") String tel,@Param("yzmType") Integer yzmType);


    boolean updateYzmToUse(@Param("id") Long id,@Param("time") Long time);

}
