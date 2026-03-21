package com.ai.basecommon.core.vo.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description
 * @Author
 * @Date 2020/8/23
 */
@Data
public class UserInfoVO {

    private Long userId;
    private String nickname;
    private String avatar;
    private String tel;
    private Integer level;
    private Integer passLevel;
    private Boolean isPasswordPay;
    private String realName;
    private String idcard;
    private Integer authStatus;
    private Integer newbieStatus;
    private Integer isSeason;
    private Long seasonTime;
}
