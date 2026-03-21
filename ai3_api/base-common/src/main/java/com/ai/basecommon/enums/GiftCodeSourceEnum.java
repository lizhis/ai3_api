package com.ai.basecommon.enums;

import java.io.Serializable;

public enum GiftCodeSourceEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    ADMIN(1, "后台"),

    INVITE(2, "邀请赠送"),

    SIGN(3, "签到赠送"),






    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    GiftCodeSourceEnum(Integer code, String value) {
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
