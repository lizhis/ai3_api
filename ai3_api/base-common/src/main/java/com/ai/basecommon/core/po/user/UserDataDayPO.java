package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class UserDataDayPO {

  private Long id;
  private Long userId;
  private Integer step;
  private Integer ymd;
  private Long createTime;
  private Long updateTime;

}
