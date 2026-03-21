package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class UserMsgPO {

  private Long id;
  private Long userId;
  private Long msgId;
  private String title;
  private String content;
  private Integer contentType;
  private Integer isNotice;
  private Integer isRead;
  private Integer isDel;
  private Long createTime;
  private Long updateTime;
  private Long readTime;
  private Long deleteTime;
  private String deleteReason;

}
