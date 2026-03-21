package com.ai.basecommon.core.po.base;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SysVipPO {

  private Long id;
  private Integer level;
  private Integer sillIntegral;
  private BigDecimal rate;
  private BigDecimal redAmount;
  private Long createTime;
  private Long updateTime;


}
