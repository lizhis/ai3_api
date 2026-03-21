package com.ai.basecommon.core.po.shop;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopOrderPO {

  private Long id;
  private String orderId;
  private Long shopId;
  private Long userId;
  private Integer num;
  private Integer price;
  private BigDecimal amount;
  private Integer sumEnergy;
  private BigDecimal sumAmount;
  private String shopName;
  private String shopImage;
  private String shopContent;
  private Long shopCateId;
  private Integer shopPrice;
  private Integer shopIsVirtual;
  private String addrReceiver;
  private String addrMobile;
  private String addrProvince;
  private String addrCity;
  private String addrDistrict;
  private String addrDetail;
  private Integer addrCode;
  private String deliveryCode;
  private String deliveryName;
  private String deliveryNumber;
  private Integer source;
  private Integer isGift;
  private String giftCode;
  private String linkOrderId;
  private Integer status;
  private Integer isDel;
  private Long deliveryTime;
  private Long finishTime;
  private Long createTime;
  private Long updateTime;

}
