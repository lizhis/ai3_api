package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.po.base.ClientRejectPO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ClientRejectMapper {


    List<ClientRejectPO> selectAll();

}
