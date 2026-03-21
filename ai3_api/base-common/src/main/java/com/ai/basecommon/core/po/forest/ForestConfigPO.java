package com.ai.basecommon.core.po.forest;


import lombok.Data;

@Data
public class ForestConfigPO {

  private Long id;
  private Integer dayLimitWater;
  private Integer dayLimitFertilize;
  private Long createTime;
  private Long updateTime;

}
