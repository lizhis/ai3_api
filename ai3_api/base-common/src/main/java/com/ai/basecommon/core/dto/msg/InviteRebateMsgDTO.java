package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InviteRebateMsgDTO extends BaseDTO {

  private Long userId;
  private String orderId;
  private BigDecimal payAmount;

}
