package com.ai.basecommon.core.param.shop;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class GiftReceiveParam {

    @Schema(name = "shopId",title = "商品ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @Schema(name = "code",title = "福利码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(name = "addrId",title = "地址ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long addrId;

}
