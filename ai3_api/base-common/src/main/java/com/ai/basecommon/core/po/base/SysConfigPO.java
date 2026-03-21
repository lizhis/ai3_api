package com.ai.basecommon.core.po.base;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SysConfigPO {

  private Long id;
  private String customerServiceUrl;
  private BigDecimal signInAmount;
  private BigDecimal rechargeMin;
  private Integer rechargeLevel;
  private Integer popIsOpen;
  private String popImage;
  private Integer popLinkType;
  private String popLinkTarget;
  private BigDecimal registerAmount;
  private BigDecimal authAmount;
  private String announcement;
  private String inviteQr;
  private String taskGiveGold;
  private Integer aiChatVisitorLimit;
  private Integer aiChatUserLimit;
  private Integer aiChatVipLimit;
  private Long createTime;
  private Long updateTime;


}
