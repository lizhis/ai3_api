package com.ai.basecommon.core.vo.user;


import lombok.Data;

@Data
public class UserAddrVO {
  private Long id;
  private String receiver;
  private String mobile;
  private String province;
  private String city;
  private String district;
  private String detail;
  private Integer code;
  private Integer isDefault;
}
