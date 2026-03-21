package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeMsgDTO extends BaseDTO {
  private String rechargeId;
  private Long userId;
  private BigDecimal amount;
}
