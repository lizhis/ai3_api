package com.ai.basecommon.core.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderIdParam {


    @Schema(name = "orderId",title = "订单号",requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderId;


}
