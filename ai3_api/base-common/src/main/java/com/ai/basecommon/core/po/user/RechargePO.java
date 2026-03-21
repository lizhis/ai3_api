package com.ai.basecommon.core.po.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargePO {

  private Long id;
  private String rechargeId;
  private Long userId;
  private String payName;
  private BigDecimal amount;
  private String remark;
  private Long transactionId;
  private String channelNo;
  private Integer type;
  private Integer ymd;
  private Integer status;
  private Long createTime;
  private Long updateTime;
  private Long finishTime;

}
