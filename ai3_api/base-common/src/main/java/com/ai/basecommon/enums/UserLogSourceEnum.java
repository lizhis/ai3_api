package com.ai.basecommon.enums;

import java.io.Serializable;

public enum UserLogSourceEnum implements BaseEnumInterface<Integer, String>, Serializable {


    PAGE(1, "页面"),

    ACTION(2, "操作"),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    UserLogSourceEnum(Integer code, String value) {
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


    public static UserLogSourceEnum getByCode(int code) {
        UserLogSourceEnum[] values = UserLogSourceEnum.values();

        for (UserLogSourceEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
