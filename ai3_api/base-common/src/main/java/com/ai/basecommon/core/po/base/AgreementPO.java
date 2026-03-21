package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class AgreementPO {

  private Long id;
  private String title;
  private Integer type;
  private String content;
  private Long createTime;
  private Long updateTime;

}
