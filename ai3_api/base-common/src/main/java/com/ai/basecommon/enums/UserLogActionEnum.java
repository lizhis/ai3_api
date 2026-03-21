package com.ai.basecommon.enums;

import java.io.Serializable;

public enum UserLogActionEnum implements BaseEnumInterface<Integer, String>, Serializable {


    PAGE_INTO(1, "进入"),

    PAGE_OUT(2, "退出"),

    PHONE_LIGHT(3, "亮屏"),

    PHONE_DARK(4, "息屏"),

    SMS_REGISTER(7, "发送注册短信"),
    SMS_FORGET(8, "发送找回密码短信"),


    REGISTER(10, "注册"),
    LOGIN(11, "登录"),
    LOGIN_OUT(12, "退出登录"),

    EDIT_PASSWORD(13, "修改密码"),
    EDIT_PAY_PASSWORD(14, "修改支付密码"),

    FORGET(15, "找回密码"),

    RECHARGE_BANK(16, "银行卡充值"),
    RECHARGE_ALIPAY(17, "支付宝充值"),
    RECHARGE_ALIPAY_SCAN(18, "支付宝充值-当面付"),
    WITHDRAW(19, "提现"),
    RECHARGE_CAIYUAN(200, "财源支付"),
    RECHARGE_CHANGQING(201, "长卿支付"),
    RECHARGE_ZHIHUI(202, "智汇支付"),
    RECHARGE_HUADA(203, "华达支付"),
    RECHARGE_HUADA2(204, "华达2支付"),

    SIGN_IN(21, "签到"),
    AUTH(22, "实名认证"),
    BIND_CARD(23, "绑定银行卡"),
    CAR_AUTH(24, "车主认证"),
    CAR_WELFARE_RECEIVE(25, "车主领取福利"),

    LEASE(27, "影视投放"),
    SHOP_BUY(28, "商城兑换"),
    GIFT_RECEIVE(29, "0元商品领取"),

    STEP_GOLD(30, "步步生金"),

    BLESSING_SHOP_BUY(31, "福卡商品兑换"),

    BLACKLIST(886, "黑名单识别"),




    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    UserLogActionEnum(Integer code, String value) {
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


    public static UserLogActionEnum getByCode(int code) {
        UserLogActionEnum[] values = UserLogActionEnum.values();

        for (UserLogActionEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
