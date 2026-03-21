package com.ai.basecommon.core.po.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarWelfareRecordPO {

  private Long id;
  private Long userId;
  private BigDecimal amount;
  private Integer energy;
  private Integer ymd;
  private Long createTime;
  private Long updateTime;

}
