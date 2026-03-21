package com.ai.basecommon.core.vo.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description
 * @Author
 */
@Data
public class UserBalanceVO {
    private BigDecimal amount;
    private BigDecimal freezeAmount;
    private Integer energy;
    private Integer integral;
    private Integer gold;
}
