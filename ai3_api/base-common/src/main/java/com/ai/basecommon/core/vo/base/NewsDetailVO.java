package com.ai.basecommon.core.vo.base;


import lombok.Data;

@Data
public class NewsDetailVO {
  private Long id;
  private String title;
  private String image;
  private String content;
  private String releaseDate;
}
