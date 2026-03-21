package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import com.ai.basecommon.core.po.service.ServiceMessagePO;
import lombok.Data;

@Data
public class ServiceChatNewMsgDTO extends BaseDTO {
  private Long agentId;
  private ServiceMessagePO messagePO;
}
