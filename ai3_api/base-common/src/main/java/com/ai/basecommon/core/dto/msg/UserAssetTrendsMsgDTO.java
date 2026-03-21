package com.ai.basecommon.core.dto.msg;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserAssetTrendsMsgDTO extends BaseDTO {
  private Long userId;
  private BigDecimal amount;
  private Integer assetType;
  private Integer typeEnum;
  private Long time;
  private String remark;
}
