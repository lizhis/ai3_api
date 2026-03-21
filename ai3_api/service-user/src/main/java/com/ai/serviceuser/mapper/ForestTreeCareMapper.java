package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.forest.ForestTreeCarePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface ForestTreeCareMapper {

    int insertGetId(ForestTreeCarePO po);

    int countCareNum(@Param("userId") Long userId,@Param("type") Integer type,@Param("ymd") Integer ymd);


}
