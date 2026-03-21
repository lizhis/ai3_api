package com.ai.basecommon.core.po.user;

import lombok.Data;

@Data
public class BlessingShopOrderPO {

  private Long id;
  private String orderId;
  private Long blessingShopId;
  private Long userId;
  private Integer num;
  private Integer price;
  private Integer sum;
  private String shopName;
  private String shopImage;
  private String shopContent;
  private Integer shopPrice;
  private Long shopCateId;
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
  private Integer status;
  private Integer isDel;
  private Long deliveryTime;
  private Long finishTime;
  private Long createTime;
  private Long updateTime;

}
