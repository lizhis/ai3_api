package com.ai.basecommon.core.vo.base;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayCaiyuanVO {

    private Long id;
    private String title;
    private String img;
    private BigDecimal amountMax;

}
