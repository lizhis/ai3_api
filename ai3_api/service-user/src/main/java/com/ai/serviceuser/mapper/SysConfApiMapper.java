package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysConfApiPO;
import org.springframework.stereotype.Component;

@Component
public interface SysConfApiMapper {

    SysConfApiPO find();

}
