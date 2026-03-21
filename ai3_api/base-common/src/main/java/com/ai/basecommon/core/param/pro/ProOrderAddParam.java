package com.ai.basecommon.core.param.pro;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class ProOrderAddParam {

    @Schema(name = "id",title = "id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(name = "payPwd",title = "支付密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String payPwd;

    @Schema(name = "skuId",title = "skuId",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long skuId;

    @Schema(name = "num",title = "数量",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer num;

}
