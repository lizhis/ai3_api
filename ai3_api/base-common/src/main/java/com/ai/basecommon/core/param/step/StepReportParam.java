package com.ai.basecommon.core.param.step;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class StepReportParam {

    @Schema(name = "num",title = "步数",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer num;

}
