package com.ai.basecommon.enums;

import java.io.Serializable;

public enum AuthStatusEnum implements BaseEnumInterface<Integer, String>, Serializable {


    //1认证成功 2认证失败 3待审核 4未认证

    YES(1, "认证成功"),

    FAIL(2, "失败"),

    WAIT(3, "待审核"),

    NO(4,"未认证"),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    AuthStatusEnum(Integer code, String value) {
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


    public static AuthStatusEnum getByCode(int code) {
        AuthStatusEnum[] values = AuthStatusEnum.values();

        for (AuthStatusEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
