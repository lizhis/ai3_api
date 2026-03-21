package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class SeasonUserPO {

  private Long id;
  private Long userId;
  private Integer status;
  private Long expireTime;
  private Long createTime;
  private Long updateTime;

}
