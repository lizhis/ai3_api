package com.ai.basecommon.enums;

import java.io.Serializable;

public enum UserStatusEnum implements BaseEnumInterface<Integer, String>, Serializable {


    NORMAL(1, "正常"),

    FREEZE(2, "冻结"),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    UserStatusEnum(Integer code, String value) {
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


    public static UserStatusEnum getByCode(int code) {
        UserStatusEnum[] values = UserStatusEnum.values();

        for (UserStatusEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
