package com.ai.basecommon.core.param.user;

import com.ai.basecommon.core.param.PageIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RechargeRecordParam extends PageIn {

    @Schema(name = "userId",title = "用户ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

}
