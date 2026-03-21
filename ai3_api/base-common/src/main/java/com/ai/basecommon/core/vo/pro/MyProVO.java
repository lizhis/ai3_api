package com.ai.basecommon.core.vo.pro;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MyProVO {

  private String orderId;
  private String title;
  private String image;
  private BigDecimal payAmount;
  private Integer status;
  private Long createTime;

}
