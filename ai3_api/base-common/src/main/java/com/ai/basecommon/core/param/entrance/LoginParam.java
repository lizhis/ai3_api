package com.ai.basecommon.core.param.entrance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class LoginParam {


    @Schema(name = "account",title = "账号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String account;

    @Schema(name = "password",title = "密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
