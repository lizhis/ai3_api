package com.ai.basecommon.core.vo.service;

import lombok.Data;

@Data
public class ServiceChatMessageVO {
    private Long id;
    private String content;
    private Integer msgType;
    private Integer senderType;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long sendTime;
}
