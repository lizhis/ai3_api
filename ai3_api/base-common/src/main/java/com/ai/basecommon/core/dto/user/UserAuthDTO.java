package com.ai.basecommon.core.dto.user;

import lombok.Data;

@Data
public class UserAuthDTO {

    private Long userId;
    private String realName;
    private String idcard;
    private Integer authStatus;
}
