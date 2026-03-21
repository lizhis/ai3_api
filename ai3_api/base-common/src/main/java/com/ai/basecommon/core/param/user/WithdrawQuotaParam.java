package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class WithdrawQuotaParam {

    @Schema(name = "quotaId",title = "提现券ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long quotaId;

    @Schema(name = "payPwd",title = "资金密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String payPwd;

}
