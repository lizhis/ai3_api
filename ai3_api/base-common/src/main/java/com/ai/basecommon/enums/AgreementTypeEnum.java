package com.ai.basecommon.enums;

import java.io.Serializable;

public enum AgreementTypeEnum implements BaseEnumInterface<Integer, String>, Serializable {


    USER_AGREEMENT(1, "用户协议"),

    PRIVACY_AGREEMENT(2, "隐私政策"),

    APP_PERMISSIONS(3, "应用权限"),

    CAR_AGREEMENT(4, "车主隐私保护申明"),

    SEASON_PLUS(5, "季卡PLUS协议"),




    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    AgreementTypeEnum(Integer code, String value) {
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


    public static AgreementTypeEnum getByCode(int code) {
        AgreementTypeEnum[] values = AgreementTypeEnum.values();

        for (AgreementTypeEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
