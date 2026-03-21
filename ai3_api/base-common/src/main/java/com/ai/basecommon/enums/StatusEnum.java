package com.ai.basecommon.enums;

import java.io.Serializable;

public enum StatusEnum implements BaseEnumInterface<Integer, String>, Serializable {


    YES(1, "正常/成功"),

    NO(2, "禁用/失败"),

    NEW(3, "新建"),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    StatusEnum(Integer code, String value) {
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


    public static StatusEnum getByCode(int code) {
        StatusEnum[] values = StatusEnum.values();

        for (StatusEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
