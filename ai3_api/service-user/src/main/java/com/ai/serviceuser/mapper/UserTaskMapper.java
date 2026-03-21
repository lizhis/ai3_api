package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.UserTaskPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserTaskMapper {

    UserTaskPO findLast(@Param("userId") Long userId);

    UserTaskPO findLastGold(@Param("userId") Long userId);
    int countByUserId(@Param("userId") Long userId);
    int countPassId(@Param("userId") Long userId,@Param("id") Long id);


    List<UserTaskPO> selectByUserIdTaskIds(@Param("userId") Long userId,@Param("ids") List<Long> ids);


    //用户完成的任务数量
    int countFinishByDays(@Param("userId") Long userId,@Param("days") Integer days);

    boolean delete(@Param("id") Long id);

}
