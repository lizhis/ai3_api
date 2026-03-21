package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class SysConfApiAlipayPO {

  private Long id;
  private String title;
  private String appid;
  private String publicKey;
  private String privateKey;
  private String encryptType;
  private String encryptKey;
  private Integer dayMaxNum;
  private Integer pullMaxNum;
  private Integer showLevel;
  private String remark;
  private Integer sort;
  private Integer status;
  private Long createTime;
  private Long updateTime;


}
