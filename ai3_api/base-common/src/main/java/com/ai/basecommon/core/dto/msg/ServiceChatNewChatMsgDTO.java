package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import com.ai.basecommon.core.po.service.ServiceChatPO;
import lombok.Data;

@Data
public class ServiceChatNewChatMsgDTO extends BaseDTO {
  private ServiceChatPO chatPO;
}
