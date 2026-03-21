package com.ai.basecommon.core.po.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRecordPO {

  private Long id;
  private String withdrawId;
  private Long userId;
  private BigDecimal amount;
  private Integer type;
  private Integer status;
  private String reason;
  private Long createTime;
  private Long updateTime;

}
