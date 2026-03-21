package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class SysConfChangqingPO {

  private Long id;
  private String mchId;
  private String secretKey;
  private Integer isOpen;
  private Integer showLevel;
  private Integer openChannel;
  private Long createTime;
  private Long updateTime;

}
