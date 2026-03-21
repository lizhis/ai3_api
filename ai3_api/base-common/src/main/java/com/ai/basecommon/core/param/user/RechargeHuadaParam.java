package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeHuadaParam {


    @Schema(name = "id",title = "渠道ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(name = "amount",title = "充值金额",requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

}
