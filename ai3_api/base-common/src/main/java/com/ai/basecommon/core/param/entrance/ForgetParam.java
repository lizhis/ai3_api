package com.ai.basecommon.core.param.entrance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class ForgetParam {


    @Schema(name = "tel",title = "手机号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String tel;

    @Schema(name = "password",title = "密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(name = "code",title = "验证码",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer code;


}
