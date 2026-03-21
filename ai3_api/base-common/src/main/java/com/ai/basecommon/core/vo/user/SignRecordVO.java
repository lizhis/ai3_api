package com.ai.basecommon.core.vo.user;

import lombok.Data;

@Data
public class SignRecordVO {
  private Long id;
  private Integer years;
  private Integer months;
  private Integer days;
  private Integer ym;
  private Integer ymd;
  private Integer signDays;
  private Integer signTotalDays;
}
