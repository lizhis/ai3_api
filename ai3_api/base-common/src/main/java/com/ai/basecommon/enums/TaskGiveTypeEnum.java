package com.ai.basecommon.enums;

import java.io.Serializable;

public enum TaskGiveTypeEnum implements BaseEnumInterface<Integer, String>, Serializable {


    MONEY(1, "现金"),
    SHOP(2, "商品"),




    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    TaskGiveTypeEnum(Integer code, String value) {
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


    public static TaskGiveTypeEnum getByCode(int code) {
        TaskGiveTypeEnum[] values = TaskGiveTypeEnum.values();

        for (TaskGiveTypeEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
