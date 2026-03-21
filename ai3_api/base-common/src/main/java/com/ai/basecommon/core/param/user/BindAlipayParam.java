package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class BindAlipayParam {


    @Schema(name = "account",title = "账号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String account;

}
