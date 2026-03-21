package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class SysBankPO {

  private Long id;
  private String name;
  private Integer isDetail;
  private Integer sort;
  private Long createTime;
  private Long updateTime;

}
