package com.ai.servicebase.mapper;

import com.ai.basecommon.core.po.base.SysConfigPO;
import org.springframework.stereotype.Component;

@Component
public interface SysConfigMapper {

    SysConfigPO find();

}
