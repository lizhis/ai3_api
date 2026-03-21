package com.ai.basecommon.enums;

import java.io.Serializable;

public enum PayBusinessTypeEnum implements BaseEnumInterface<Integer, String>, Serializable {


    RECHARGE(1, "充值"),



    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    PayBusinessTypeEnum(Integer code, String value) {
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


    public static PayBusinessTypeEnum getByCode(int code) {
        PayBusinessTypeEnum[] values = PayBusinessTypeEnum.values();

        for (PayBusinessTypeEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
