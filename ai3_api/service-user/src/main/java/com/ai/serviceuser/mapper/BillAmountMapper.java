package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.po.user.BillAmountPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public interface BillAmountMapper {


    int insert(BillAmountPO po);

    BigDecimal findLastBalance(@Param("userId") Long userId);

}
