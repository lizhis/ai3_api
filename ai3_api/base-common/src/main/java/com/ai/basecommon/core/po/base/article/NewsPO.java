package com.ai.basecommon.core.po.base.article;


import lombok.Data;

@Data
public class NewsPO {

  private Long id;
  private String title;
  private String image;
  private String content;
  private String outId;
  private String releaseDate;
  private Long createTime;
  private Long updateTime;

}
