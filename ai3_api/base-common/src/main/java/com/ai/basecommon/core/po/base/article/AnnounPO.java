package com.ai.basecommon.core.po.base.article;


import lombok.Data;

@Data
public class AnnounPO {

  private Long id;
  private String title;
  private String image;
  private String content;
  private Integer status;
  private Long createTime;
  private Long updateTime;
  private Long releaseTime;

}
