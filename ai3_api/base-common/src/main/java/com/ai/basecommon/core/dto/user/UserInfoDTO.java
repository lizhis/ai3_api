package com.ai.basecommon.core.dto.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description
 * @Author
 */
@Data
public class UserInfoDTO {

    private Long userId;
    private String nickname;
    private String avatar;
    private String tel;
    private Integer level;
    private Integer passLevel;
    private String passwordPay;
    private String realName;
    private String idcard;
    private Integer authStatus;
    private Integer newbieStatus;
}
