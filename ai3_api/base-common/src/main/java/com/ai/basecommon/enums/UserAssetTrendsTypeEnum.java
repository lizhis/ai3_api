package com.ai.basecommon.enums;

import java.io.Serializable;

public enum UserAssetTrendsTypeEnum implements BaseEnumInterface<Integer, String>, Serializable {


    RECHARGE(1, "充值", SymbolTypeEnum.ADD),
    WITHDRAW(2, "提现", SymbolTypeEnum.SUBTRACT),
    INVITE(3, "邀请赠送", SymbolTypeEnum.ADD),

    SIGN_IN_ALONG(4, "连续签到", SymbolTypeEnum.ADD),
    SIGN_IN_TOTAL(5, "累计签到", SymbolTypeEnum.ADD),
    SIGN_IN_EXTRA(106, "签到额外赠送", SymbolTypeEnum.ADD),

    FOREST_ENERGY(6, "云币森林", SymbolTypeEnum.ADD),
    BUY(7, "商城兑换", SymbolTypeEnum.SUBTRACT),



    CAR_PUT_ADD(11, "项目投放", SymbolTypeEnum.ADD),
    CAR_PUT_SUBTRACT(12, "项目投放", SymbolTypeEnum.SUBTRACT),

    INTEREST(13, "接单收益", SymbolTypeEnum.ADD),
    SEND_BACK(15, "本金返还", SymbolTypeEnum.ADD),


    STEP_GOLD_CLOCK(17, "早晚打卡", SymbolTypeEnum.ADD),
    STEP_GOLD_WATER(18, "喝水打卡", SymbolTypeEnum.ADD),


    //CAR_OWNER_WELFARE(21, "车主福利", SymbolTypeEnum.ADD),
    //LIFE_PAY(22, "生活缴费", SymbolTypeEnum.SUBTRACT),


    PUT_REBATE_1(31, "投放返佣【一级】", SymbolTypeEnum.ADD),
    PUT_REBATE_2(32, "投放返佣【二级】", SymbolTypeEnum.ADD),
    PUT_REBATE_3(33, "投放返佣【三级】", SymbolTypeEnum.ADD),

    SEASON_CARD(35, "季卡会员", SymbolTypeEnum.SUBTRACT),

    VIP(61, "VIP红包", SymbolTypeEnum.ADD),
    REGISTER_AMOUNT(63, "注册赠送", SymbolTypeEnum.ADD),
    AUTH_AMOUNT(64, "实名赠送", SymbolTypeEnum.ADD),
    LEASE_AMOUNT(65, "投放赠送", SymbolTypeEnum.ADD),
    RECHARGE_BACK(66, "充值返现", SymbolTypeEnum.ADD),
    BLESSING(68, "集福红包", SymbolTypeEnum.ADD),

    GOLD_EXCHANGE_ADD(73, "云豆提取", SymbolTypeEnum.ADD),
    GOLD_EXCHANGE_SUB(74, "云豆提取", SymbolTypeEnum.SUBTRACT),

    ADMIN_ADD(996, "系统操作", SymbolTypeEnum.ADD),
    ADMIN_SUBTRACT(997, "系统操作", SymbolTypeEnum.SUBTRACT),


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


    UserAssetTrendsTypeEnum(Integer code, String value, SymbolTypeEnum symbolTypeEnum) {
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

    public static UserAssetTrendsTypeEnum getByCode(int code) {
        UserAssetTrendsTypeEnum[] values = UserAssetTrendsTypeEnum.values();

        for (UserAssetTrendsTypeEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
