package com.ai.basecommon.core.param.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class CustomerChatOpenParam {

    @Schema(name = "content",title = "内容",requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

}
