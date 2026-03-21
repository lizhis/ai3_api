package com.ai.basecommon.enums;

import java.io.Serializable;

public enum UserSmallQuotaChannelEnum implements BaseEnumInterface<Integer, String>, Serializable {


    REGISTER(1, "注册赠送"),

    SYSTEM(2, "系统赠送"),

    PROJECT(3, "投放赠送"),



    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    UserSmallQuotaChannelEnum(Integer code, String value) {
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


}
