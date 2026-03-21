package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class UserAlipayPO {

  private Long id;
  private Long userId;
  private String realName;
  private String account;
  private Long createTime;
  private Long updateTime;

}
