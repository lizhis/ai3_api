package com.ai.basecommon.enums;

import java.io.Serializable;

public enum SmsYzmTypeEnum implements BaseEnumInterface<Integer, String>, Serializable {


    LOGIN(0, "登录"),

    REGISTER(1, "注册"),

    EDIT_DATA(2, "修改资料"),

    PASSWORD(3, "修改密码"),

    EDIT_PAY_PASSWORD(4, "设置资金密码"),

    WITHDRAW(5, "提现"),

    FORGET(6, "找回密码"),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    SmsYzmTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static String getByCode(Integer code) {
        for (SmsYzmTypeEnum c : SmsYzmTypeEnum.values()) {
            if (c.code.compareTo(code) == 0) {
                return c.value;
            }
        }
        return null;
    }

    public static SmsYzmTypeEnum getEnumByCode(Integer code) {
        for (SmsYzmTypeEnum c : SmsYzmTypeEnum.values()) {
            if (c.code.compareTo(code) == 0) {
                return c;
            }
        }
        return null;
    }


}
