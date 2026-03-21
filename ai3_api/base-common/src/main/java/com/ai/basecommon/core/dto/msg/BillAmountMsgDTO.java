package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillAmountMsgDTO extends BaseDTO {
  private String orderId;
  private Long userId;
  private String realName;
  private BigDecimal amount;
  private Integer typeEnum;
  private Long time;
  private String remark;
}
