package com.ai.servicebase.mapper;

import com.ai.basecommon.core.vo.base.AgreementVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface AgreementMapper {

    AgreementVO findByType(@Param("type") Integer type);

}
