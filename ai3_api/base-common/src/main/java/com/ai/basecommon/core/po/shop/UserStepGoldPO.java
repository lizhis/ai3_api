package com.ai.basecommon.core.po.shop;


import lombok.Data;

@Data
public class UserStepGoldPO {

  private Long id;
  private Long userId;
  private Integer type;
  private Integer energy;
  private Integer timeType;
  private Integer ymd;
  private Long createTime;
  private Long updateTime;

}
