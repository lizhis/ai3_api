package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysVipPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysVipMapper {

    //查询
    List<SysVipPO> select();

    SysVipPO findByLevel(@Param("level") Integer level);

}
