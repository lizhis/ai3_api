package com.ai.basecommon.core.po.base;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlipayOrderPO {

  private Long id;
  private Long userId;
  private String orderId;
  private BigDecimal totalAmount;
  private String goodsName;
  private String appId;
  private String tradeNo;
  private BigDecimal receiptAmount;
  private String payAccount;
  private String payStatus;
  private String payTimeStr;
  private Long payTime;
  private Integer businessType;
  private Integer payWay;
  private Integer status;
  private String channelRemark;
  private Long createTime;
  private Long updateTime;

}
