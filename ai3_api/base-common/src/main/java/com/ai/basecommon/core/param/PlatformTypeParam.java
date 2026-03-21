package com.ai.basecommon.core.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PlatformTypeParam {


    @Schema(name = "platform",title = "平台 1安卓2iOS",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer platform;


}
