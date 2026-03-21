package com.ai.basecommon.core.param.captcha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class YzmRegisterParam {

    @Schema(name = "tel",title = "手机号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String tel;

    @Schema(name = "deviceId",title = "设备号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String deviceId;

}
