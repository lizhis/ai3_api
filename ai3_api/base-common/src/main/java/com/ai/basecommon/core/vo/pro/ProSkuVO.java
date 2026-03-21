package com.ai.basecommon.core.vo.pro;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProSkuVO {
  private Long id;
  private BigDecimal price;
  private BigDecimal takeAmountMin;
  private BigDecimal takeAmountMax;
  private Integer putDays;
  private BigDecimal incomeFloor;
}
