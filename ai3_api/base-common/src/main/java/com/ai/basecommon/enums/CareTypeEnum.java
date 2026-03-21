package com.ai.basecommon.enums;

import java.io.Serializable;

public enum CareTypeEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    WATER(1, "浇水"),
    FERTILIZE(2, "施肥"),



    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    CareTypeEnum(Integer code, String value) {
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
