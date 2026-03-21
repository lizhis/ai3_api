package com.ai.basecommon.core.po.base;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ZhihuiOrderPO {

  private Long id;
  private Long userId;
  private String orderId;
  private BigDecimal totalAmount;
  private String goodsName;
  private String mchId;
  private String productId;
  private String tradeNo;
  private String originTradeNo;
  private Integer businessType;
  private Integer status;
  private String channelRemark;
  private Long payTime;
  private Long createTime;
  private Long updateTime;

}
