package com.ai.basecommon.core.po.shop;


import lombok.Data;

@Data
public class GiftPO {

  private Long id;
  private Long shopId;
  private Integer receiveNum;
  private Integer status;
  private Long createTime;
  private Long updateTime;

}
