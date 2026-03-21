package com.ai.basecommon.enums;

import java.io.Serializable;

public enum SeasonTypeEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    SEASON(1, "季卡"),

    YEAR(2, "年卡"),



    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    SeasonTypeEnum(Integer code, String value) {
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
