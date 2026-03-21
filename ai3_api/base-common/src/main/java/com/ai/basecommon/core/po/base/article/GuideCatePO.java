package com.ai.basecommon.core.po.base.article;


import lombok.Data;

@Data
public class GuideCatePO {

  private Long id;
  private String name;
  private Integer sort;
  private Integer status;
  private Integer isDel;
  private Long createTime;
  private Long updateTime;

}
