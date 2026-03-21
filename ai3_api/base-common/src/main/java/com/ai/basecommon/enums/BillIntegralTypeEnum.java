package com.ai.basecommon.enums;

import java.io.Serializable;

public enum BillIntegralTypeEnum implements BaseEnumInterface<Integer, String>, Serializable {


    SIGN_IN_ALONG(4, "连续签到", SymbolTypeEnum.ADD),
    SIGN_IN_TOTAL(5, "累计签到", SymbolTypeEnum.ADD),

    CAR_PUT(7, "项目投放", SymbolTypeEnum.ADD),



    ADMIN_ADD(996, "系统操作", SymbolTypeEnum.ADD),
    ADMIN_SUBTRACT(997, "系统操作", SymbolTypeEnum.SUBTRACT),

    REVERSAL_ADD(998, "系统冲正", SymbolTypeEnum.ADD),
    REVERSAL_SUBTRACT(999, "系统冲正", SymbolTypeEnum.SUBTRACT),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    private SymbolTypeEnum symbolTypeEnum;


    BillIntegralTypeEnum(Integer code, String value, SymbolTypeEnum symbolTypeEnum) {
        this.code = code;
        this.value = value;
        this.symbolTypeEnum = symbolTypeEnum;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public SymbolTypeEnum getSymbolTypeEnum() {
        return symbolTypeEnum;
    }

    public static BillIntegralTypeEnum getByCode(int code) {
        BillIntegralTypeEnum[] values = BillIntegralTypeEnum.values();

        for (BillIntegralTypeEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
