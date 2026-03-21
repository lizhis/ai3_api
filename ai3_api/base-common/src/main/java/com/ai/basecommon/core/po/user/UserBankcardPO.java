package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class UserBankcardPO {

  private Long id;
  private Long userId;
  private String receiver;
  private String mobile;
  private String bankName;
  private String openName;
  private String cardNo;
  private Long createTime;
  private Long updateTime;

}
