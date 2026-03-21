package com.ai.basecommon.core.param.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class GuideParam {

    @Schema(name = "cateId",title = "类目id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long cateId;

}
