package com.ai.basecommon.core.po.service;

import lombok.Data;

@Data
public class ServiceAgentPO {
    private Long agentId;
    private String account;
    private String password;
    private String name;
    private String avatar;
    private Integer isOnline;
    private Integer status;
    private Long createTime;
    private Long updateTime;
}
