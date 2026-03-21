package com.ai.basecommon.core.po.base.article;


import lombok.Data;

@Data
public class GuidePO {

  private Long id;
  private String title;
  private String content;
  private Long cateId;
  private Integer sort;
  private Integer status;
  private Long createTime;
  private Long updateTime;

}
