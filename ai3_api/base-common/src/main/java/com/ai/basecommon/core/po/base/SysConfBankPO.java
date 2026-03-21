package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class SysConfBankPO {

  private Long id;
  private String viewTitle;
  private String title;
  private String username;
  private String account;
  private String tip;
  private String des;
  private Integer isBack;
  private Integer isOpen;
  private Long createTime;
  private Long updateTime;

}
