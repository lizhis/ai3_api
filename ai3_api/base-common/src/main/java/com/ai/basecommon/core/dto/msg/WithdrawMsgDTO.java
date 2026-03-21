package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawMsgDTO extends BaseDTO {
  private String withdrawId;
  private Long userId;
  private BigDecimal amount;
}
