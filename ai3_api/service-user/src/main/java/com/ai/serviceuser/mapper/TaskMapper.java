package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.TaskPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TaskMapper {


    List<TaskPO> selectAll();

    TaskPO findById(@Param("id") Long id);

    List<TaskPO> selectCurrentList(@Param("days") Integer days);


}
