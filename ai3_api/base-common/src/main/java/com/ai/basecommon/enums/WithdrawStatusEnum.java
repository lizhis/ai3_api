package com.ai.basecommon.enums;

import java.io.Serializable;

public enum WithdrawStatusEnum implements BaseEnumInterface<Integer, String>, Serializable {


    SUCCESS(1, "成功"),

    FAIL(2, "失败"),

    WAIT(3, "待处理"),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    WithdrawStatusEnum(Integer code, String value) {
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


    public static WithdrawStatusEnum getByCode(int code) {
        WithdrawStatusEnum[] values = WithdrawStatusEnum.values();

        for (WithdrawStatusEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
