package com.ai.basecommon.core.dto.bill;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillAmountRequestDTO extends BaseDTO {
  private String orderId;
  private Long userId;
  private String realName;
  private BigDecimal amount;
  private Integer typeEnum;
  private Long time;
}
