package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class SysConfGiveGiftCodePO {

  private Long id;
  private Integer isOpenInvite;
  private Integer inviteNum;
  private String inviteTpl;
  private Integer isOpenSign;
  private Integer signDays;
  private String signChannels;
  private Long signStartTime;
  private Long createTime;
  private Long updateTime;

}
