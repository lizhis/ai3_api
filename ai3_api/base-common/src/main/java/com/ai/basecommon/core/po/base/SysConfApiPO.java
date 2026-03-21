package com.ai.basecommon.core.po.base;


import lombok.Data;

@Data
public class SysConfApiPO {

  private Long id;
  private String auth;
  private String bankCard;
  private String amapWeb;
  private String ip138;
  private String kuaidi100Key;
  private String kuaidi100Customer;
  private String alipayAppid;
  private String alipayPayPublicKey;
  private String alipayAppPrivateKey;
  private String alipayEncryptType;
  private String alipayEncryptKey;
  private String alipayScanAppid;
  private String alipayScanPayPublicKey;
  private String alipayScanAppPrivateKey;
  private String aiChatKey;
  private String aiChatModel;
  private Long createTime;
  private Long updateTime;

}
