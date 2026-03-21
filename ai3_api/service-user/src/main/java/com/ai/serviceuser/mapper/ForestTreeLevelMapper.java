package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.forest.ForestTreeLevelPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ForestTreeLevelMapper {


    //查询全部树
    List<ForestTreeLevelPO> selectAll();

    //查询下一个等级的配置
    ForestTreeLevelPO findNextLevel(@Param("level") Integer level);



}
