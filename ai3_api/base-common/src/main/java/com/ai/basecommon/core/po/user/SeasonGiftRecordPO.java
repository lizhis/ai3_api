package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class SeasonGiftRecordPO {

  private Long id;
  private Long userId;
  private Long shopId;
  private String orderId;
  private Integer ym;
  private Integer ymd;
  private Long createTime;
  private Long updateTime;

}
