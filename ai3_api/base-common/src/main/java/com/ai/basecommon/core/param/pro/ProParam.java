package com.ai.basecommon.core.param.pro;

import com.ai.basecommon.core.param.PageIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProParam extends PageIn {


    @Schema(name = "level",title = "前端勿传",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer level;


}
