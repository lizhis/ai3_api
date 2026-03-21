package com.ai.basecommon.core.param.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class AIChatParam {

    @Schema(name = "msgId",title = "msgId",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String msgId;

    @Schema(name = "content",title = "内容",requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

}
