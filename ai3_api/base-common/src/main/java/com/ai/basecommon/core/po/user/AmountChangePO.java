package com.ai.basecommon.core.po.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AmountChangePO {

  private Long id;
  private Long userId;
  private BigDecimal amount;
  private Integer symbolType;
  private Integer type;
  private Long createTime;
  private Long updateTime;

}
