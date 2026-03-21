package com.ai.basecommon.core.vo.base;

import lombok.Data;

@Data
public class AppVersionVO {

  private String version;
  private String updateExplain;
  private String downloadUrl;
  private Integer updateType;
  private Long releaseTime;
}
