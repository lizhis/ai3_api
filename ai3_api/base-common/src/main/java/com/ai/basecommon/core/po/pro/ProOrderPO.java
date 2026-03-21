package com.ai.basecommon.core.po.pro;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProOrderPO {

  private Long id;
  private String orderId;
  private Long userId;
  private String realName;
  private Long proId;
  private String title;
  private String image;
  private BigDecimal takeAmountMin;
  private BigDecimal takeAmountMax;
  private BigDecimal incomeFloor;
  private Integer extraNumMin;
  private Integer extraNumMax;
  private BigDecimal feeRate;
  private Integer putDays;
  private String remark;
  private String content;
  private Integer cateType;
  private BigDecimal price;
  private BigDecimal payAmount;
  private Integer giveGoodsType;
  private Long giveShop;
  private String giveShopDesc;
  private BigDecimal giveAmount;
  private String giveDesc;
  private Integer giveQuotaAmount;
  private Integer giveQuotaIsActive;
  private Integer totalTakeNum;
  private BigDecimal totalTakeAmount;
  private BigDecimal totalFee;
  private BigDecimal totalEarning;
  private Integer isPlan;
  private Integer checkType;
  private Integer isAutoNext;
  private String parentOrderId;
  private Integer status;
  private Integer ymd;
  private Long startTime;
  private Long endTime;
  private Long createTime;
  private Long updateTime;
  private Integer isDel;

}
