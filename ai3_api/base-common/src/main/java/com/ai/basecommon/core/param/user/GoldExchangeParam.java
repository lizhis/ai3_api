package com.ai.basecommon.core.param.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class GoldExchangeParam {


    @Schema(name = "gold",title = "云豆",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer gold;

}
