package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

@Data
public class UserLogMsgDTO extends BaseDTO {
  private String deviceId;
  private Long userId;
  private Integer source;
  private Integer action;
  private Integer level;
  private String content;
  private String remark;
  private String ip;
  private Integer ymd;
  private Long createTime;
  private Long updateTime;
}
