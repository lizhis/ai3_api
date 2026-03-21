package com.ai.basecommon.core.vo.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SysConfSignChildVO {
  private Long id;
  private Integer days;
  private BigDecimal amount;
  private Integer giveType;
  private Integer type;
}
