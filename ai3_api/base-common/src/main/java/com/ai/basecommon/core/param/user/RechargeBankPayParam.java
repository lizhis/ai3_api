package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class RechargeBankPayParam {


    @Schema(name = "payName",title = "付款人",requiredMode = Schema.RequiredMode.REQUIRED)
    private String payName;

    @Schema(name = "amount",title = "充值金额",requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(name = "remark",title = "备注",requiredMode = Schema.RequiredMode.REQUIRED)
    private String remark;

}
