package com.ai.basecommon.core.vo.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeVO {

  private Long id;
  private String rechargeId;
  private BigDecimal amount;
  private Integer type;
  private Integer status;
  private Long createTime;
}
