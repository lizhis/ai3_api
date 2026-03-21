package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class SysConfBlessingPO {
  private Long id;
  private Integer isOpen;
  private Integer userNum;
  private Long signStartTime;
  private String rankingTotal;
  private String rankingWeek;
}
