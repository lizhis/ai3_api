package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.SignRecordPO;
import com.ai.basecommon.core.vo.user.SignRecordVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SignRecordMapper {


    int insertGetId(SignRecordPO po);

    int countBy(@Param("userId") Long userId,@Param("ymd") Integer ymd);

    int countByUserId(@Param("userId") Long userId);

    //当月签到次数
    int countByYm(@Param("userId") Long userId,@Param("ym") Integer ym);

    SignRecordPO findBy(@Param("userId") Long userId,@Param("ymd") Integer ymd);


    List<SignRecordVO> selectByYm(@Param("userId") Long userId, @Param("ym") Integer ym);

    List<SignRecordPO> selectStartList(@Param("userId") Long userId,@Param("startTime") Long startTime);

    int countByStartTime(@Param("userId") Long userId,@Param("startTime") Long startTime);


}
