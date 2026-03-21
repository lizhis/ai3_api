package com.ai.basecommon.core.po.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserBalancePO {

  private Long id;
  private Long userId;
  private BigDecimal amount;
  private BigDecimal freezeAmount;
  private Integer integral;
  private Integer energy;
  private Integer gold;
  private Long createTime;
  private Long updateTime;


}
