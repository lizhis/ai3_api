package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdActiveMsgDTO extends BaseDTO {
  private Integer channel;
  private String activeId;
}
