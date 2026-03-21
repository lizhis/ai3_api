package com.ai.basecommon.core.vo.forest;


import lombok.Data;

@Data
public class ForestTreeVO {

  private Integer level;
  private Integer status;

  private Integer canWaterNum;//可浇水次数
  private Integer canFertilizeNum;//可施肥次数

}
