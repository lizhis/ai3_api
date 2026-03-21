package com.ai.basecommon.core.param.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BlessingCardDetailParam {

    @Schema(name = "type",title = "类型",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

}
