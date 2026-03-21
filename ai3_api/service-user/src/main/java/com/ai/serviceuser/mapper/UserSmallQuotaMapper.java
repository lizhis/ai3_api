package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.UserSmallQuotaPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserSmallQuotaMapper {


    int countByChannel(@Param("userId") Long userId,@Param("channel") Integer channel);


    int insert(UserSmallQuotaPO po);

    UserSmallQuotaPO findById(@Param("id") Long id);

    UserSmallQuotaPO findOK(@Param("userId") Long userId);

    List<UserSmallQuotaPO> selectOKAndWaitList(@Param("userId") Long userId);

    List<UserSmallQuotaPO> selectWaitList(@Param("userId") Long userId);

    int countWaitList(@Param("userId") Long userId);

    boolean updateStartTime(@Param("id") Long id,@Param("startTime") Long startTime);

    boolean updateOK(@Param("id") Long id,@Param("updateTime") Long updateTime);

    boolean updateUse(@Param("id") Long id,@Param("orderId") String orderId,@Param("updateTime") Long updateTime);


}
