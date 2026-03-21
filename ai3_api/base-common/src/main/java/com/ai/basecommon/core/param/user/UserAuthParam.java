package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserAuthParam {


    @Schema(name = "realName",title = "真实姓名",requiredMode = Schema.RequiredMode.REQUIRED)
    private String realName;

    @Schema(name = "idcard",title = "身份证号码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String idcard;

}
