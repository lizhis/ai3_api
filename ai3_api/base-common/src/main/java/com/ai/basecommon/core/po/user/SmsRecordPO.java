package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class SmsRecordPO {

  private Long id;
  private String tel;
  private Integer code;
  private Integer yzmType;
  private String content;
  private String errorMsg;
  private String deviceId;
  private String ip;
  private String ipAddr;
  private Integer sendStatus;
  private Integer status;
  private Integer ymd;
  private Long createTime;
  private Long updateTime;
  private Long expireTime;

}
