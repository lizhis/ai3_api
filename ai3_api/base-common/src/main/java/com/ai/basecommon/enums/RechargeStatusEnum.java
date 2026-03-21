package com.ai.basecommon.enums;

import java.io.Serializable;

public enum RechargeStatusEnum implements BaseEnumInterface<Integer, String>, Serializable {


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


    RechargeStatusEnum(Integer code, String value) {
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


    public static RechargeStatusEnum getByCode(int code) {
        RechargeStatusEnum[] values = RechargeStatusEnum.values();

        for (RechargeStatusEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
