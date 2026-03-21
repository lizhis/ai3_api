package com.ai.basecommon.core.po.service;

import lombok.Data;

@Data
public class ServiceMessagePO {
    private Long id;
    private Long chatId;
    private String content;
    private Integer msgType;
    private Integer senderType;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long realSenderId;
    private Long sendTime;
    private Integer isRead;
    private Integer isRevoke;
    private Long createTime;
    private Long updateTime;
}
