package com.ai.basecommon.enums;

import java.io.Serializable;

public enum IsEnum implements BaseEnumInterface<Integer, String>, Serializable {


    YES(1, "是"),

    NO(0, "否"),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    IsEnum(Integer code, String value) {
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
