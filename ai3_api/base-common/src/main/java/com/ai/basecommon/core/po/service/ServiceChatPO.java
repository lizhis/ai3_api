package com.ai.basecommon.core.po.service;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceChatPO {
  private Long id;
  private String chatTitle;
  private String deviceId;
  private Long userId;
  private String userAvatar;
  private Long agentId;
  private String lastMsg;
  private Long lastMsgTime;
  private String userIp;
  private String userAddr;
  private Integer userRole;
  private String question;
  private Integer agentUnread;
  private Integer userUnread;
  private Integer messageCount;
  private Integer status;
  private Integer ymd;
  private Long startTime;
  private Long createTime;
  private Long updateTime;
}
