package com.ai.basecommon.core.param.step;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class ClockParam {

    @Schema(name = "type",title = "行为类型 1早晚卡 2喝水卡",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

}
