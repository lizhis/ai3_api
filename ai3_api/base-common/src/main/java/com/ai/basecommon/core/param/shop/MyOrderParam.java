package com.ai.basecommon.core.param.shop;

import com.ai.basecommon.core.param.PageIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class MyOrderParam extends PageIn {

    @Schema(name = "userId",title = "userId",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    @Schema(name = "status",title = "状态",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

}
