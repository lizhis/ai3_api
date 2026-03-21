package com.ai.basecommon.enums;

import java.io.Serializable;

public enum CarTypeEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    MOTOR(1, "机动车"),

    ENERGY(2, "新能源"),



    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    CarTypeEnum(Integer code, String value) {
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
