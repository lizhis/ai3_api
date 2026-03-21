package com.ai.basecommon.enums;

import java.io.Serializable;

public enum BillAmountTypeEnum implements BaseEnumInterface<Integer, String>, Serializable {

    RECHARGE(1, "充值", SymbolTypeEnum.ADD),
    WITHDRAW(2, "提现", SymbolTypeEnum.SUBTRACT),
    WITHDRAW_FAIL(3, "提现退回", SymbolTypeEnum.ADD),

    SIGN_IN_ALONG(4, "连续签到", SymbolTypeEnum.ADD),
    SIGN_IN_TOTAL(5, "累计签到", SymbolTypeEnum.ADD),
    SIGN_IN_EXTRA(106, "签到额外赠送", SymbolTypeEnum.ADD),


    BUY(6, "兑换礼品", SymbolTypeEnum.SUBTRACT),

    CAR_PUT(7, "项目投放", SymbolTypeEnum.SUBTRACT),
    INTEREST(8, "接单收益", SymbolTypeEnum.ADD),
    SEND_BACK(9, "本金返还", SymbolTypeEnum.ADD),


    CAR_OWNER_WELFARE(10, "车主福利", SymbolTypeEnum.ADD),
    LIFE_PAY(12, "生活缴费", SymbolTypeEnum.SUBTRACT),



    PUT_REBATE_1(31, "投放返佣【一级】", SymbolTypeEnum.ADD),
    PUT_REBATE_2(32, "投放返佣【二级】", SymbolTypeEnum.ADD),
    PUT_REBATE_3(33, "投放返佣【三级】", SymbolTypeEnum.ADD),


    SEASON_CARD(35, "季卡会员", SymbolTypeEnum.SUBTRACT),

    VIP(61, "VIP升级红包", SymbolTypeEnum.ADD),
    REGISTER_AMOUNT(63, "注册赠送", SymbolTypeEnum.ADD),
    AUTH_AMOUNT(64, "实名赠送", SymbolTypeEnum.ADD),
    LEASE_AMOUNT(65, "投放赠送", SymbolTypeEnum.ADD),
    RECHARGE_BACK(66, "充值返现", SymbolTypeEnum.ADD),
    BLESSING(68, "集福红包", SymbolTypeEnum.ADD),

    TASK(70, "任务奖励", SymbolTypeEnum.ADD),

    GOLD_EXCHANGE(73, "云豆提取", SymbolTypeEnum.ADD),


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


    BillAmountTypeEnum(Integer code, String value,SymbolTypeEnum symbolTypeEnum) {
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

    public static BillAmountTypeEnum getByCode(int code) {
        BillAmountTypeEnum[] values = BillAmountTypeEnum.values();

        for (BillAmountTypeEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
