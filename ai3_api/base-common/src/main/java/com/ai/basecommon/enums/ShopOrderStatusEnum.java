package com.ai.basecommon.enums;

import java.io.Serializable;

public enum ShopOrderStatusEnum implements BaseEnumInterface<Integer, String>, Serializable {

    WAIT_DELIVERY(1, "待发货"),

    WAIT_FINISH(2, "待收货"),

    FINISH(3, "已完成"),


    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    ShopOrderStatusEnum(Integer code, String value) {
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


    public static ShopOrderStatusEnum getByCode(int code) {
        ShopOrderStatusEnum[] values = ShopOrderStatusEnum.values();

        for (ShopOrderStatusEnum statusEnum : values) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
