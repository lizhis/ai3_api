package com.ai.basecommon.enums;

import java.io.Serializable;

public enum ServiceMessageSenderTypeEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    USER(1, "用户"),
    AGENT(2, "客服"),
    SYSTEM(3, "系统"),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    ServiceMessageSenderTypeEnum(Integer code, String value) {
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
