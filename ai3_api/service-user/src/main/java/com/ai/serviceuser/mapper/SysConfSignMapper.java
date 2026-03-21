package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysConfSignPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysConfSignMapper {


    List<SysConfSignPO> select();

    SysConfSignPO findByDaysAndType(@Param("days") Integer days,@Param("type") Integer type);


}
