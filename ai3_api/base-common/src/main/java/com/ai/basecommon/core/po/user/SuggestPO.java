package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class SuggestPO {

  private Long id;
  private Long userId;
  private String content;
  private String images;
  private String reply;
  private Integer status;
  private Long createTime;
  private Long updateTime;

}
