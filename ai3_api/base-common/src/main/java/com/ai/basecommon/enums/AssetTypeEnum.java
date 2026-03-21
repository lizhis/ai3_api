package com.ai.basecommon.enums;

import java.io.Serializable;

public enum AssetTypeEnum implements BaseEnumInterface<Integer, String>, Serializable  {


    CASH(1, "现金"),
    INTEGRAL(2, "积分"),
    ENERGY(3, "云币"),
    GOLD(4, "云豆"),




    ;


    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String value;


    AssetTypeEnum(Integer code, String value) {
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
