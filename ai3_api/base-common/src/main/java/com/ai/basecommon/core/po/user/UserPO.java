package com.ai.basecommon.core.po.user;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class UserPO {

    private Long userId;
    private String tel;
    private String nickname;
    private String avatar;
    private Integer level;
    private Long inviteUserId;
    private Integer passLevel;
    private Integer channel;
    private String registerIp;
    private String registerAddr;
    private String realName;
    private String idcard;
    private Integer authStatus;
    private Integer newbieStatus;
    private Integer status;
    private Integer isDel;
    private String lastLoginIp;
    private String lastLoginAddr;
    private Long lastLoginTime;
    private Long lastEnterTime;
    private Integer isOnline;
    private Long createTime;
    private Long updateTime;
}
