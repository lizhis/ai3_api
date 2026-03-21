package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SeasonBuyParam {


    @Schema(name = "seasonType",title = "季卡类型",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer seasonType;

}
