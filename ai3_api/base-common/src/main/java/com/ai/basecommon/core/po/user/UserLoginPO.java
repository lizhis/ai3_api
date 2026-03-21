package com.ai.basecommon.core.po.user;

import lombok.Data;


@Data
public class UserLoginPO {
    private Long id;
    private Long userId;
    private String deviceId;
    private String ip;
    private String token;
    private String tokenMd5;
    private Integer status;
    private Long expireTime;
    private Long createTime;
    private Long updateTime;
}
