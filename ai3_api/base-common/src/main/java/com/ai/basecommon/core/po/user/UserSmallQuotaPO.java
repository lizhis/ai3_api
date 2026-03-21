package com.ai.basecommon.core.po.user;

import lombok.Data;

@Data
public class UserSmallQuotaPO {

    private Long id;
    private Long userId;
    private Integer amount;
    private Integer channel;
    private Integer status;
    private String orderId;
    private Long startTime;
    private Long createTime;
    private Long updateTime;
}
