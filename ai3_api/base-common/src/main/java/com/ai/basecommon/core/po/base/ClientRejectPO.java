package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class ClientRejectPO {

  private Long id;
  private Long userId;
  private String deviceId;
  private String ip;
  private Long createTime;
  private Long updateTime;
}
