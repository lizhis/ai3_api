package com.ai.basecommon.core.po.forest;


import lombok.Data;

@Data
public class ForestTreePO {

  private Long id;
  private Long userId;
  private Integer level;
  private Integer waterTimes;
  private Integer waterDays;
  private Integer fertilizeTimes;
  private Integer fertilizeDays;
  private Integer status;
  private Long createTime;
  private Long updateTime;

}
