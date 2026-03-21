package com.ai.basecommon.core.po.user;

import lombok.Data;

@Data
public class DeviceSecretPO {
    private Long id;
    private String deviceId;
    private String secret;
    private Integer ymd;
    private Long createTime;
    private Long updateTime;
}
