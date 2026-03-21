package com.ai.basecommon.core.vo.base;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayHuadaVO {

    private Long id;
    private String title;
    private String img;
    private BigDecimal amountMin;
    private BigDecimal amountMax;

}
