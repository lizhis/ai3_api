package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.AiChatRecordPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AiChatRecordMapper {


    int insertGetId(AiChatRecordPO po);

    int countUserYmd(@Param("userId") Long userId,@Param("ymd") Integer ymd);

    int countDeviceYmd(@Param("deviceId") String deviceId,@Param("ymd") Integer ymd);

    List<AiChatRecordPO> selectByUserId(@Param("userId") Long userId);
    List<AiChatRecordPO> selectByDeviceId(@Param("deviceId") String deviceId);

    boolean cleanByUserId(@Param("userId") Long userId);
    boolean cleanByDeviceId(@Param("deviceId") String deviceId);

    boolean updateReply(AiChatRecordPO po);
    boolean updateNotReply(AiChatRecordPO po);


}
