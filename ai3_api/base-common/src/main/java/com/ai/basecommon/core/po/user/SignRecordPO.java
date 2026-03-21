package com.ai.basecommon.core.po.user;

import lombok.Data;

@Data
public class SignRecordPO {

  private Long id;
  private Long userId;
  private Integer years;
  private Integer months;
  private Integer days;
  private Integer ym;
  private Integer ymd;
  private Integer signDays;
  private Integer signTotalDays;
  private Long createTime;
  private Long updateTime;

}
