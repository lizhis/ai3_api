package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.base.SysConfBlessingCardPO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SysConfBlessingCardMapper {

    List<SysConfBlessingCardPO> select();

}
