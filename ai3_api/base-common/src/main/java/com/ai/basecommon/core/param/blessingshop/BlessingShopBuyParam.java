package com.ai.basecommon.core.param.blessingshop;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class BlessingShopBuyParam {

    @Schema(name = "shopId",title = "商品ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @Schema(name = "addrId",title = "地址ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long addrId;

    @Schema(name = "num",title = "数量",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer num;

}
