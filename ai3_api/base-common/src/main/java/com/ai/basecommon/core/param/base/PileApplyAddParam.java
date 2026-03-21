package com.ai.basecommon.core.param.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PileApplyAddParam {


    @Schema(name = "applyName",title = "申请人",requiredMode = Schema.RequiredMode.REQUIRED)
    private String applyName;

    @Schema(name = "applyTel",title = "手机号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String applyTel;

    @Schema(name = "applyAddr",title = "申请地址",requiredMode = Schema.RequiredMode.REQUIRED)
    private String applyAddr;

    @Schema(name = "remark",title = "备注",requiredMode = Schema.RequiredMode.REQUIRED)
    private String remark;


}
