package com.ai.basecommon.core.po.base.article;

import lombok.Data;

@Data
public class FocusPO {

  private Long id;
  private String title;
  private String image;
  private String content;
  private Integer sort;
  private Integer status;
  private Long createTime;
  private Long updateTime;

}
