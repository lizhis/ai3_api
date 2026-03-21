package com.ai.basecommon.core.po.forest;


import lombok.Data;

@Data
public class ForestTreeCarePO {

  private Long id;
  private Long userId;
  private Integer treeLevel;
  private Integer type;
  private Integer energy;
  private Integer ymd;
  private Long createTime;
  private Long updateTime;

}
