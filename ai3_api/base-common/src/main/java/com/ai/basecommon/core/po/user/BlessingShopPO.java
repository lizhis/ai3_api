package com.ai.basecommon.core.po.user;


import lombok.Data;

@Data
public class BlessingShopPO {

  private Long id;
  private String name;
  private String image;
  private String content;
  private Integer price;
  private Long cateId;
  private Integer sort;
  private Integer status;
  private Integer isDel;
  private Long createTime;
  private Long updateTime;

}
