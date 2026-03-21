package com.ai.basecommon.core.dto.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description
 * @Author
 */
@Data
public class UserBalanceChangeDTO {

    private Long userId;
    private BigDecimal amount;

}
