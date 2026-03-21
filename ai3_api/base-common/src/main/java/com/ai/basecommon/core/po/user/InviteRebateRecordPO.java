package com.ai.basecommon.core.po.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class InviteRebateRecordPO {

  private Long id;
  private Long userId;
  private Long childUserId;
  private BigDecimal amount;
  private Integer type;
  private Long createTime;
  private Long updateTime;

}
