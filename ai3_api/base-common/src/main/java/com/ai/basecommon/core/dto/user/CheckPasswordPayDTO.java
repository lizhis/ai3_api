package com.ai.basecommon.core.dto.user;

import lombok.Data;

@Data
public class CheckPasswordPayDTO {
    private Long userId;
    private String passwordPay;
}
