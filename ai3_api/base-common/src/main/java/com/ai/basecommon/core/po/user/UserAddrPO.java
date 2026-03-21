package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class UserAddrPO {

  private Long id;
  private Long userId;
  private String receiver;
  private String mobile;
  private String province;
  private String city;
  private String district;
  private String detail;
  private Integer code;
  private Integer isDefault;
  private Long createTime;
  private Long updateTime;
}
