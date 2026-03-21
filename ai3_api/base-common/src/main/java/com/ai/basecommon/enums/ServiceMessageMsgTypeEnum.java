package com.ai.basecommon.enums;

import java.io.Serializable;

public enum ServiceMessageMsgTypeEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    TEXT(1, "文本"),
    IMAGE(2, "图片"),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    ServiceMessageMsgTypeEnum(Integer code, String value) {
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
