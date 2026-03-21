package com.ai.basecommon.core.vo.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SysVipVO {

  private Integer level;
  private Integer sillIntegral;
  private BigDecimal rate;
  private BigDecimal redAmount;

}
