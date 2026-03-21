package com.ai.basecommon.core.param.user;

import com.ai.basecommon.core.param.PageIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WithdrawRecordParam extends PageIn {

    @Schema(name = "userId",title = "用户ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    @Schema(name = "time",title = "查询时间",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long time;

}
