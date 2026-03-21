package com.ai.basecommon.core.po.base;

import lombok.Data;

@Data
public class OssConfPO {

  private Long id;
  private String endpoint;
  private String accessKey;
  private String accessSecret;
  private String bucket;
  private String suffix;
  private Integer size;
  private String domain;
  private Long createTime;
  private Long updateTime;
}
