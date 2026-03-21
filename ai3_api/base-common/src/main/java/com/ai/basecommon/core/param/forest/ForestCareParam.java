package com.ai.basecommon.core.param.forest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class ForestCareParam {


    @Schema(name = "type",title = "类型 1浇水2施肥",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;


}
