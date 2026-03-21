package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CollectDayMsgDTO extends BaseDTO {

  private BigDecimal rechargeAmount;
  private BigDecimal withdrawAmount;
  private boolean isRegister = false;
  private boolean isSign = false;
  private boolean isStepGold = false;
  private boolean isCare = false;
  private BigDecimal returnAmount;

}
