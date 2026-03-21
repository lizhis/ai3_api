package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

@Data
public class BillEnergyMsgDTO extends BaseDTO {
  private String orderId;
  private Long userId;
  private String realName;
  private Integer num;
  private Integer typeEnum;
  private Long time;
  private String remark;
}
