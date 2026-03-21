package com.ai.basecommon.core.vo.pro;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MyProOrderTakeVO {

    private BigDecimal takeAmount;
    private BigDecimal takeEarning;
    private BigDecimal backMoney;
    private Integer todayNum;
    private Integer todayExtraNum;
    private Integer status;
    private Integer currentDays;
    private Long takeTime;
    private Long finishTime;
}
