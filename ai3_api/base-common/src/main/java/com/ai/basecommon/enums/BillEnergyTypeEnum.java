package com.ai.basecommon.enums;

import java.io.Serializable;

public enum BillEnergyTypeEnum implements BaseEnumInterface<Integer, String>, Serializable {


    FOREST_ENERGY(1, "云币森林", SymbolTypeEnum.ADD),
    INVITE(3, "邀请赠送", SymbolTypeEnum.ADD),

    SIGN_IN_ALONG(4, "连续签到", SymbolTypeEnum.ADD),
    SIGN_IN_TOTAL(5, "累计签到", SymbolTypeEnum.ADD),


    BUY(11, "商城兑换", SymbolTypeEnum.SUBTRACT),


    STEP_GOLD_CLOCK(17, "早晚打卡", SymbolTypeEnum.ADD),
    STEP_GOLD_WATER(18, "喝水打卡", SymbolTypeEnum.ADD),

    LEASE_AMOUNT(65, "投放赠送", SymbolTypeEnum.ADD),


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


    BillEnergyTypeEnum(Integer code, String value, SymbolTypeEnum symbolTypeEnum) {
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

    public static BillEnergyTypeEnum getByCode(int code) {
        BillEnergyTypeEnum[] values = BillEnergyTypeEnum.values();

        for (BillEnergyTypeEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
