package com.ai.basecommon.core.vo.base;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayWayVO {

    private Integer way;
    private BigDecimal amountMax;
    private Object obj;

}
