package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LifePayParam {


    @Schema(name = "houseNumber",title = "户号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String houseNumber;

    @Schema(name = "amount",title = "金额",requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(name = "province",title = "省",requiredMode = Schema.RequiredMode.REQUIRED)
    private String province;

    @Schema(name = "city",title = "市",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String city;

    @Schema(name = "district",title = "区",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String district;

}
