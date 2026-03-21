package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EditPayPasswordParam {


    @Schema(name = "oldpassword",title = "旧交易密码",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oldpassword;

    @Schema(name = "password",title = "交易密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
