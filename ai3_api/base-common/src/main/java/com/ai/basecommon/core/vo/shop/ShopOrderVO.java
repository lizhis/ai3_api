package com.ai.basecommon.core.vo.shop;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopOrderVO {

  private String orderId;
  private Integer num;
  private Integer price;
  private BigDecimal amount;
  private Integer sumEnergy;
  private BigDecimal sumAmount;
  private String shopName;
  private String shopImage;
  private Integer shopIsVirtual;
  private Integer status;

}
