package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class SysConfSmsPO {

  private Long id;
  private String username;
  private String password;
  private String tpl;
  private String aliyunSignName;
  private String aliyunAccessKey;
  private String aliyunAccessSecret;
  private String aliyunTplTemplateCode;
  private String aliyunTplParam;
  private String aliyunTplContent;
  private String giftTplTemplateCode;
  private String giftTplParam;
  private String giftTplContent;
  private Long createTime;
  private Long updateTime;

}
