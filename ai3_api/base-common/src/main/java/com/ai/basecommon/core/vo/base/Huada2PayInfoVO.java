package com.ai.basecommon.core.vo.base;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Huada2PayInfoVO {

    private String payUserName;
    private String mchOrderNo;
    private BigDecimal amount;
    private String bankName;
    private String accName;
    private String accNo;
    private Long createTime;
    private Integer validTime;

}
