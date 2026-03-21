package com.ai.basecommon.core.po.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawPO {

  private Long id;
  private String withdrawId;
  private Long userId;
  private String bankName;
  private String receiver;
  private String cardNo;
  private BigDecimal amount;
  private Integer type;
  private Integer isSmallQuota;
  private String reason;
  private Integer status;
  private Long createTime;
  private Long updateTime;
  private Long finishTime;

}
