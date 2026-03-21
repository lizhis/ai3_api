package com.ai.basecommon.core.po.base;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SysConfWithdrawPO {

  private Long id;
  private BigDecimal withdrawMin;
  private String withdrawDesc;
  private Integer quotaIsOpen;
  private BigDecimal quotaAmount;
  private String quotaDesc;
  private Integer quotaActiveSignDay;
  private Integer quotaUserGiveAmount;
  private Long createTime;
  private Long updateTime;

}
