package com.ai.basecommon.core.po.base;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SysConfApiHuada2PO {

  private Long id;
  private String title;
  private String img;
  private String produceId;
  private BigDecimal amountMin;
  private BigDecimal amountMax;
  private String remark;
  private Integer sort;
  private Integer status;
  private Long createTime;
  private Long updateTime;


}
