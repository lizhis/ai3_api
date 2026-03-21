package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeHuada2Param {


    @Schema(name = "id",title = "渠道ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(name = "amount",title = "充值金额",requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(name = "payUserName",title = "汇款人",requiredMode = Schema.RequiredMode.REQUIRED)
    private String payUserName;



}
