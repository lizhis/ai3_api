package com.ai.basecommon.core.param.user;

import com.ai.basecommon.core.param.PageIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserAssetTrendsParam extends PageIn {

    @Schema(name = "userId",title = "用户ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    @Schema(name = "type",title = "类型",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer type;

    @Schema(name = "assetType",title = "资产类型",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer assetType;

    @Schema(name = "time",title = "时间",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long time;

}
