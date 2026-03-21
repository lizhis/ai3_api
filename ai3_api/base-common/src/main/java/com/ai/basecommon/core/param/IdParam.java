package com.ai.basecommon.core.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class IdParam {


    @Schema(name = "id",title = "ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;


}
