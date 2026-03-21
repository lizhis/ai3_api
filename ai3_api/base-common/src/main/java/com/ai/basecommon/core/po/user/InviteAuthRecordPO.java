package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class InviteAuthRecordPO {

  private Long id;
  private Long userId;
  private Long childUserId;
  private Long createTime;
  private Long updateTime;

}
