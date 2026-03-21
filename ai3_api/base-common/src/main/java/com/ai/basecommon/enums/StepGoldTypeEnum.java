package com.ai.basecommon.enums;

import java.io.Serializable;

public enum StepGoldTypeEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    CLOCK(1, "打卡"),
    WATER(2, "喝水"),



    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    StepGoldTypeEnum(Integer code, String value) {
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


    public static StepGoldTypeEnum getByCode(int code) {
        StepGoldTypeEnum[] values = StepGoldTypeEnum.values();

        for (StepGoldTypeEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
