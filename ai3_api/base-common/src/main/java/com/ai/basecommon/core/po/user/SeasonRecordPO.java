package com.ai.basecommon.core.po.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeasonRecordPO {

  private Long id;
  private Long userId;
  private Integer seasonType;
  private Integer days;
  private BigDecimal amount;
  private Long createTime;
  private Long updateTime;

}
