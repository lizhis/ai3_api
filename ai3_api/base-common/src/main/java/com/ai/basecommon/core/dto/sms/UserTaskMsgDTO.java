package com.ai.basecommon.core.dto.sms;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

@Data
public class UserTaskMsgDTO extends BaseDTO {
  private Long userId;
  private Integer taskType;
  private Long id;
}
