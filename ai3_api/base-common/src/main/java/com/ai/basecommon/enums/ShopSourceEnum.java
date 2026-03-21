package com.ai.basecommon.enums;

import java.io.Serializable;

public enum ShopSourceEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    SHOP(1, "商城购买"),

    GIFT(2, "免费礼品"),

    PUT(3, "投放赠送"),






    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    ShopSourceEnum(Integer code, String value) {
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



}
