package com.ai.basecommon.core.po.pro;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProSkuPO {
  private Long id;
  private Long proId;
  private BigDecimal price;
  private BigDecimal takeAmountMin;
  private BigDecimal takeAmountMax;
  private Integer putDays;
  private BigDecimal incomeFloor;
  private Integer status;
  private Long createTime;
  private Long updateTime;
}
