package com.ai.basecommon.core.po.user;

import lombok.Data;

@Data
public class UserBlessingPO {

    private Long id;
    private Long userId;
    private Integer blessingType;
    private String orderId;
    private Integer ymd;
    private Integer status;
    private Long createTime;
    private Long updateTime;
}
