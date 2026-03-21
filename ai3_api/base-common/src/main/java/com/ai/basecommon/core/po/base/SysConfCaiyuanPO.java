package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class SysConfCaiyuanPO {

  private Long id;
  private String mchId;
  private String appId;
  private String secretKey;
  private Integer isOpen;
  private Integer showLevel;
  private Integer openChannel;
  private Long createTime;
  private Long updateTime;

}
