package com.ai.basecommon.enums;

import java.io.Serializable;

public enum AlipayBusinessTypeEnum implements BaseEnumInterface<Integer, String>, Serializable {


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


    AlipayBusinessTypeEnum(Integer code, String value) {
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


    public static AlipayBusinessTypeEnum getByCode(int code) {
        AlipayBusinessTypeEnum[] values = AlipayBusinessTypeEnum.values();

        for (AlipayBusinessTypeEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
