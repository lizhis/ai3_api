package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class SysConfBlessingCardPO {
  private Long id;
  private Integer type;
  private String conditions;
  private String detail;
  private Long createTime;
  private Long updateTime;
}
