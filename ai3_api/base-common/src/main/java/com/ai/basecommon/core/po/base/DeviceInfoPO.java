package com.ai.basecommon.core.po.base;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeviceInfoPO {

  private Long id;
  private String deviceId;
  private Long userId;
  private String platform;
  private String brand;
  private String model;
  private String version;
  private String browserName;
  private String computerName;
  private String hostName;
  private String oaid;
  private String idfa;
  private String appVersion;
  private String ip;
  private String ipAddr;
  private BigDecimal lng;
  private BigDecimal lat;
  private String province;
  private String city;
  private String district;
  private String street;
  private String streetNumber;//门牌
  private Integer nationCode;//国家代码
  private Integer cityCode;//城市代码
  private Integer adcode;//行政区划代码
  private Integer ymd;
  private Long createTime;
  private Long updateTime;
}
