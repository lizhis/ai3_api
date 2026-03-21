package com.ai.basecommon.core.param.entrance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegisterParam {


    @Schema(name = "tel",title = "手机号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String tel;

    @Schema(name = "password",title = "密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(name = "inviteTel",title = "推荐人手机号",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String inviteTel;

    @Schema(name = "code",title = "验证码",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer code;

    @Schema(name = "channel",title = "渠道",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer channel;


}
