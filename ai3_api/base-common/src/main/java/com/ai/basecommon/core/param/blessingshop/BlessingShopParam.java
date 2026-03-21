package com.ai.basecommon.core.param.blessingshop;

import com.ai.basecommon.core.param.PageIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class BlessingShopParam extends PageIn {

    @Schema(name = "cateId",title = "类目id",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long cateId;

}
