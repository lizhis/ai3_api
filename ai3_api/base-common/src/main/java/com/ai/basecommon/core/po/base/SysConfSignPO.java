package com.ai.basecommon.core.po.base;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SysConfSignPO {

  private Long id;
  private Integer days;
  private BigDecimal amount;
  private Integer giveType;
  private Integer type;
  private Integer status;
  private Long createTime;
  private Long updateTime;

}
