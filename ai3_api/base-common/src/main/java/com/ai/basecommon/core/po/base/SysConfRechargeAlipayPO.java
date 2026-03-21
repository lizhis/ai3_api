package com.ai.basecommon.core.po.base;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SysConfRechargeAlipayPO {

  private Long id;
  private String viewTitle;
  private BigDecimal amountMax;
  private Integer numMax;
  private String goodsName;
  private Integer isOpen;
  private Integer openChannel;
  private String transferUrl;
  private Integer transferOpen;
  private String scanViewTitle;
  private BigDecimal scanAmountMax;
  private Integer scanNumMax;
  private String scanGoodsName;
  private Integer scanIsOpen;
  private Integer scanOpenChannel;
  private Long createTime;
  private Long updateTime;

}
