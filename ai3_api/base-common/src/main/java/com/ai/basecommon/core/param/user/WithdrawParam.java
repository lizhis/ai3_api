package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class WithdrawParam {

    @Schema(name = "amount",title = "提现金额",requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(name = "payPwd",title = "资金密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String payPwd;

    @Schema(name = "isSmallQuota",title = "是否小额",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer isSmallQuota;

}
