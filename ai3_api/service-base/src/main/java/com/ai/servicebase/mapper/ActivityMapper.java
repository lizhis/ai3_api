package com.ai.servicebase.mapper;

import com.ai.basecommon.core.po.base.ActivityPO;
import com.ai.basecommon.core.vo.base.ActivityVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ActivityMapper {


    //查询
    List<ActivityVO> select();



}
