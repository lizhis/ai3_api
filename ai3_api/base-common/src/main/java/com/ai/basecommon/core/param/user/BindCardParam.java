package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class BindCardParam {


    @Schema(name = "bankName",title = "银行名称",requiredMode = Schema.RequiredMode.REQUIRED)
    private String bankName;

    @Schema(name = "openName",title = "开户行",requiredMode = Schema.RequiredMode.REQUIRED)
    private String openName;

    @Schema(name = "cardNo",title = "卡号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String cardNo;

}
