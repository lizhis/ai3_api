package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysConfigPO;
import org.springframework.stereotype.Component;

@Component
public interface SysConfigMapper {

    int insert(SysConfigPO po);

    SysConfigPO find();

}
