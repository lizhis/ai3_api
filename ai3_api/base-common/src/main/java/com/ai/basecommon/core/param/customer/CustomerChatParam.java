package com.ai.basecommon.core.param.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class CustomerChatParam {

    @Schema(name = "chatId",title = "会话ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long chatId;

    @Schema(name = "content",title = "内容",requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(name = "msgType",title = "消息类型 1文本 2图片",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer msgType;

}
