package com.ai.basecommon.core.param.user.addr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class UserAddrAddParam {


    @Schema(name = "receiver",title = "收货人",requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiver;

    @Schema(name = "mobile",title = "手机号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String mobile;

    @Schema(name = "province",title = "省",requiredMode = Schema.RequiredMode.REQUIRED)
    private String province;

    @Schema(name = "city",title = "市",requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @Schema(name = "district",title = "区",requiredMode = Schema.RequiredMode.REQUIRED)
    private String district;

    @Schema(name = "detail",title = "详细地址",requiredMode = Schema.RequiredMode.REQUIRED)
    private String detail;

    @Schema(name = "code",title = "地区代码",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer code;

    @Schema(name = "isDefault",title = "是否默认",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer isDefault;



}
