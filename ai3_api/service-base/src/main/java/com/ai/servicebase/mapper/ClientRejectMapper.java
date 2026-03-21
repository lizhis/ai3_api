package com.ai.servicebase.mapper;

import com.ai.basecommon.core.po.base.ClientRejectPO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ClientRejectMapper {


    List<ClientRejectPO> selectAll();

}
