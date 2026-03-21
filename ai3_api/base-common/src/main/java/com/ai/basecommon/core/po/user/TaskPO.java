package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class TaskPO {

  private Long id;
  private Integer days;
  private Integer number;
  private String title;
  private Integer type;
  private String content;
  private Integer giveType;
  private String giveContent;
  private String taskDescImg;
  private String taskDesc;
  private Long createTime;
  private Long updateTime;

}
