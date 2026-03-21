package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

@Data
public class ServiceChatUserStatusMsgDTO extends BaseDTO {
  private Long chatId;
  private Integer userStatus;
}
