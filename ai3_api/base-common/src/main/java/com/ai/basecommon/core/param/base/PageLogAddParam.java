package com.ai.basecommon.core.param.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 *
 */
@Data
public class PageLogAddParam {


    @Schema(name = "route",title = "路由",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String route;

    @Schema(name = "action",title = "进入退出",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer action;

    @Schema(name = "remark",title = "备注",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;

}
