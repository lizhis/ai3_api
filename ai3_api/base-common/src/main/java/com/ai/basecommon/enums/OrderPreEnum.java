package com.ai.basecommon.enums;

import java.io.Serializable;

public enum OrderPreEnum implements BaseEnumInterface<Integer, String>, Serializable {



    RECHARGE(1, "充值单"),
    WITHDRAW(2, "提现单"),

    PROJECT(3, "项目订单"),
    SHOP(4, "商品订单"),


    BILL(9, "账单流水"),






    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    OrderPreEnum(Integer code, String value) {
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


    public static OrderPreEnum getByCode(String code) {
        OrderPreEnum[] values = OrderPreEnum.values();

        for (OrderPreEnum statusEnum : values) {
            if (statusEnum.code.equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

}
