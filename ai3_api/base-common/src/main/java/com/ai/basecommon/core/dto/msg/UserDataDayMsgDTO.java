package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

@Data
public class UserDataDayMsgDTO extends BaseDTO {

  private Long userId;
  private Integer step;
  private Integer ymd;
}
