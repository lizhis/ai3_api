package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class SysConfUploadPO {

  private Long id;
  private String url;
  private String bucket;
  private Integer size;
  private String suffix;
  private String domain;
  private Long createTime;
  private Long updateTime;

}
