package com.ai.basecommon.core.po.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserAssetTrendsPO {

  private Long id;
  private Long userId;
  private BigDecimal amount;
  private Integer symbolType;
  private Integer type;
  private String typeStr;
  private Integer assetType;
  private String remark;
  private Integer status;
  private Integer runningDate;
  private Long createTime;
  private Long updateTime;

}
