package com.ai.basecommon.core.po.user;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserDataPO {

  private Long id;
  private Long userId;
  private Integer onlineDays;
  private Integer signNum;
  private Integer leaseNum;
  private BigDecimal leaseSum;
  private Integer rechargeNum;
  private BigDecimal rechargeSum;
  private Integer withdrawNum;
  private BigDecimal withdrawSum;
  private Integer careNum;
  private Integer harvestNum;
  private Integer harvestSum;
  private Integer shopNum;
  private Integer shopSumEnergy;
  private BigDecimal shopSumAmount;
  private Integer children1Num;
  private Integer children2Num;
  private Integer children3Num;
  private BigDecimal children1Sum;
  private BigDecimal children2Sum;
  private BigDecimal children3Sum;
  private BigDecimal blessingAmount;
  private Long createTime;
  private Long updateTime;

}
