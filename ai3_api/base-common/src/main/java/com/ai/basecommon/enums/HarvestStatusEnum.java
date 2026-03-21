package com.ai.basecommon.enums;

import java.io.Serializable;

public enum HarvestStatusEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    WATER(1, "待收取"),
    FINISH(2, "已收取"),
    EXPIRE(3, "已过期"),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    HarvestStatusEnum(Integer code, String value) {
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
