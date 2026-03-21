package com.ai.basecommon.core.po.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillAmountPO {

  private Long id;
  private String flowId;
  private String orderId;
  private Long userId;
  private String realName;
  private BigDecimal amount;
  private Integer symbolType;
  private Integer type;
  private String typeStr;
  private BigDecimal billBalance;
  private String remark;
  private Integer status;
  private Integer runningDate;
  private Long createTime;
  private Long updateTime;

}
