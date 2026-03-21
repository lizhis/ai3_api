package com.ai.basecommon.core.po.forest;


import lombok.Data;

@Data
public class ForestTreeLevelPO {

  private Long id;
  private Integer level;
  private Integer sillWaterTimes;
  private Integer sillFertilizeTimes;
  private Integer genEnergyMin;
  private Integer genEnergyMax;
  private Long createTime;
  private Long updateTime;

}
