package com.ai.basecommon.core.po.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SysConfSeasonPO {

  private Long id;
  private BigDecimal seasonPrice;
  private BigDecimal yearPrice;
  private Long gift1;
  private Long gift2;
  private Long gift3;
  private Long createTime;
  private Long updateTime;

}
