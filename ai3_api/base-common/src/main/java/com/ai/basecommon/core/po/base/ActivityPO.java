package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class ActivityPO {

  private Long id;
  private String title;
  private String image;
  private Integer linkType;
  private String linkTarget;
  private String linkArguments;
  private Integer sort;
  private Integer status;
  private Long createTime;
  private Long updateTime;


}
