package com.ai.basecommon.core.vo.service;

import lombok.Data;

import java.util.List;

@Data
public class ServiceMyChatVO {
    private Long chatId;
    private String agentName;
    private List<ServiceChatMessageVO> list;
    private List<String> typeList;
}
