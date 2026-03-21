package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.forest.ForestTreePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface ForestTreeMapper {


    int insert(ForestTreePO po);

    ForestTreePO findByUserId(@Param("userId") Long userId);

    boolean waterInc(@Param("id") Long id,@Param("waterTimes") Integer waterTimes,@Param("waterDays") Integer waterDays);
    boolean fertilizeInc(@Param("id") Long id,@Param("fertilizeTimes") Integer fertilizeTimes,@Param("fertilizeDays") Integer fertilizeDays);


    //变更等级
    boolean updateLevel(@Param("id") Long id,@Param("level") Integer level);

}
