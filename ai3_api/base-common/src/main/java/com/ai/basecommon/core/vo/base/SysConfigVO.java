package com.ai.basecommon.core.vo.base;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SysConfigVO {
  private String customerServiceUrl;
  private String announcement;
  private BigDecimal rechargeMin;
}
