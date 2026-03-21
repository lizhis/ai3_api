package com.ai.servicebase.mapper;

import com.ai.basecommon.core.po.base.SysConfBankPO;
import org.springframework.stereotype.Component;

@Component
public interface SysConfBankMapper {

    //查询
    SysConfBankPO find();

}
