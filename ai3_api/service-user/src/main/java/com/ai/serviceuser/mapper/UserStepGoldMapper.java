package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.shop.UserStepGoldPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserStepGoldMapper {

    int insertGetId(UserStepGoldPO po);

    //是否存在打卡
    int existClock(@Param("userId") Long userId,@Param("timeA") Long timeA,@Param("timeB") Long timeB);

    //查询今天喝水列表
    List<UserStepGoldPO> selectWaterList(@Param("userId") Long userId, @Param("ymd") Integer ymd);

    //查询用户当天记录
    List<UserStepGoldPO> selectList(@Param("userId") Long userId, @Param("ymd") Integer ymd);



}
