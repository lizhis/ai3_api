package com.ai.basecommon.enums;

import java.io.Serializable;

public enum PayWayEnum implements BaseEnumInterface<Integer, String>, Serializable {


    BANK(1, "银行卡"),

    ALIPAY(2, "支付宝"),

    ALIPAY_TRANSFER(3, "支付宝转账"),

    ALIPAY_SCAN(4, "支付宝当面付"),

    CAIYUAN(5, "财源支付"),

    CHANGQING(7, "长卿支付"),

    ZHIHUI(8, "智汇支付"),
    HUADA(9, "华达支付"),

    HUADA2(10, "华达2支付"),
    QILIN(11, "麒麟支付"),



    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    PayWayEnum(Integer code, String value) {
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


    public static PayWayEnum getByCode(int code) {
        PayWayEnum[] values = PayWayEnum.values();

        for (PayWayEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
