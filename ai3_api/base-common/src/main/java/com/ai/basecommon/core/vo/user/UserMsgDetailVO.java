package com.ai.basecommon.core.vo.user;


import lombok.Data;

@Data
public class UserMsgDetailVO {

  private String title;
  private String content;
  private Integer contentType;
  private Long createTime;
}
