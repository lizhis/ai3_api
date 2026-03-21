package com.ai.basecommon.core.vo.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BlessingShopOrderVO {

  private String orderId;
  private Integer num;
  private Integer price;
  private Integer sum;
  private String shopName;
  private String shopImage;
  private Integer status;

}
