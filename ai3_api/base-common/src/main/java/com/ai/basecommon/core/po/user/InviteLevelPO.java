package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class InviteLevelPO {

  private Long id;
  private Long userId;
  private Long childUserId;
  private Integer level;
  private Long createTime;
  private Long updateTime;

}
