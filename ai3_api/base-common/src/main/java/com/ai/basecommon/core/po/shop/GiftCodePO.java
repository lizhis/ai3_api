package com.ai.basecommon.core.po.shop;


import lombok.Data;

@Data
public class GiftCodePO {

  private Long id;
  private String code;
  private Long userId;
  private Integer dayNum;
  private Integer maxNum;
  private Integer useNum;
  private String remark;
  private Integer source;
  private Integer status;
  private Long createTime;
  private Long updateTime;

}
