package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class EditPasswordParam {


    @Schema(name = "oldpassword",title = "旧密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldpassword;

    @Schema(name = "password",title = "新密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
