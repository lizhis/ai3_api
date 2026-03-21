package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserDataMsgDTO extends BaseDTO {

  private Long userId;
  private boolean isOnline = false;
  private boolean isSign = false;
  private BigDecimal leaseAmount;
  private BigDecimal rechargeAmount;
  private BigDecimal withdrawAmount;
  private boolean isStepGold = false;
  private Integer shopEnergy;
  private BigDecimal shopAmount;
  private boolean isAuth = false;
}
