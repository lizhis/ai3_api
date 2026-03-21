package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class UserTaskPO {

  private Long id;
  private Long userId;
  private Long taskId;
  private Integer progress;
  private Integer status;
  private Integer ymd;
  private Long createTime;
  private Long updateTime;

}
