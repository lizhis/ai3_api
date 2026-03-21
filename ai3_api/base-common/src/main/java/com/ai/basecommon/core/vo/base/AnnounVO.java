package com.ai.basecommon.core.vo.base;


import lombok.Data;

@Data
public class AnnounVO {

  private Long id;
  private String title;
  private String image;
  private String content;
  private Long releaseTime;

}
