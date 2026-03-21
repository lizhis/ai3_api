package com.ai.basecommon.core.vo.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserAssetTrendsVO {

  private Long id;
  private BigDecimal amount;
  private Integer symbolType;
  private Integer type;
  private String typeStr;
  private Integer assetType;
  private String remark;
  private Long createTime;

}
